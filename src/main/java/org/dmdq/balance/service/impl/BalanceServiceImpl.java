package org.dmdq.balance.service.impl;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

import org.dmdq.balance.conf.Constant;
import org.dmdq.balance.conf.InfoCode;
import org.dmdq.balance.conf.RedisKeys;
import org.dmdq.balance.dao.impl.RedisDaoImpl;
import org.dmdq.balance.model.LoadOrder;
import org.dmdq.balance.model.domain.BalanceForbidden;
import org.dmdq.balance.model.domain.LoadBalanceApp;
import org.dmdq.balance.model.domain.LoadBalanceProcess;
import org.dmdq.balance.model.domain.LoadBalanceRegion;
import org.dmdq.balance.model.domain.LoadBalanceServer;
import org.dmdq.balance.service.BalanceService;
import org.dmdq.balance.util.BeanUtil;
import org.dmdq.balance.util.MD5Util;
import org.dmdq.balance.util.SocketUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public final class BalanceServiceImpl implements BalanceService {

	public static final Ordering<LoadOrder> LOAD_ORDER = Ordering.natural().nullsFirst().onResultOf(new Function<LoadOrder, Integer>() {
		public Integer apply(LoadOrder input) {
			return input.getOrder();
		}
	});

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BalanceServiceImpl.class);

	public List<?> loadApps(Channel channel, String userName) {
		String key = String.format(RedisKeys.KEY_INDEX_USER_APP, userName);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		Map<String, String> appIdMap = redis.hgetAll(key);
		List<LoadBalanceApp> list = Lists.newArrayList();
		for (Map.Entry<String, String> entry : appIdMap.entrySet()) {
			String name = entry.getValue();
			String contentApps = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_APP, name)));
			LoadBalanceApp loadBalanceApp = BeanUtil.readValue(contentApps, LoadBalanceApp.class);
			list.add(loadBalanceApp);
		}
		list = LOAD_ORDER.sortedCopy(list);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> addApp(Channel channel, String userName, String appName, String order, String forbiddenType, String forbiddenValue) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		String appId = MD5Util.getMD5String(appName);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		boolean existsUser = redis.exists(String.format(RedisKeys.KEY_DATA_USER, userName));
		if (!existsUser) {
			code = InfoCode.NOT_EXISTS_USER_NAME;
		} else {
			boolean existsAppName = redis.exists(String.format(RedisKeys.KEY_DATA_APP, appName));
			if (existsAppName) {
				code = InfoCode.EXISTS_APP_NAME;
			} else {
				map.put("id", appId);
				String userKey = String.format(RedisKeys.KEY_INDEX_USER_APP, userName);
				// 保存用户应用列表
				redis.hset(userKey, appId, appName);
				// 保存应用具体信息
				String appKey = String.format(RedisKeys.KEY_DATA_APP, appName);
				Map<String, String> appData = Maps.newHashMap();
				appData.put("id", appId);
				appData.put("name", appName);
				appData.put("order", order);
				appData.put("forbiddenType", forbiddenType);
				appData.put("forbiddenValue", forbiddenValue);
				redis.hmset(appKey, appData);
			}
		}
		map.put("code", code);
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> removeApp(Channel channel, String userName, String appId) {

		int code = InfoCode.SUCCESS;

		Map<String, Object> map = Maps.newHashMap();
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);

		// 用户键
		String key_user = String.format(RedisKeys.KEY_INDEX_USER_APP, userName);
		// 应用名称
		String name = null;

		if (redis.hexists(key_user, appId)) {
			// 获取应用名称
			name = redis.hget(key_user, appId);
			//
			String key_data = String.format(RedisKeys.KEY_DATA_APP, name);
			// 获取应用下的所有分区,一并进行删除
			String key_children = String.format(RedisKeys.KEY_INDEX_APP_REGION, appId);
			// 从用户索引中删除该应用数据
			redis.hdel(key_user, appId);
			// 删除该应用的详细配置数据
			redis.del(key_data);
			// 删除子分区
			Map<String, String> childrenMap = redis.hgetAll(key_children);

			for (Map.Entry<String, String> entry : childrenMap.entrySet()) {
				// String server_name= entry.getKey();
				String regionId = entry.getValue();
				// 删除服务器
				removeRegion(channel, appId, regionId);
			}
		} else {
			// 不存在该应用
			code = InfoCode.NOT_EXISTS_APP_NAME;
		}
		LOGGER.info("userName={} appId={} ,appName={} code={}", new Object[] { userName, appId, name, code });
		map.put("code", code);
		return map;
	}

	public Map<String, Object> update(Channel channel, String type, String uuid, String name, String order, String forbiddenType, String forbiddenValue) {
		Map<String, Object> map = Maps.newHashMap();
		int code = InfoCode.SUCCESS;
		map.put("code", code);
		return map;
	}

	/**
	 * 更新应用相关信息配置
	 * 
	 * @param channel
	 * @param appId
	 * @param appName
	 * @param order
	 * @return
	 */
	public Map<String, Object> updateApp(Channel channel, String userName, String appId, String appName, String order) {
		Map<String, Object> map = Maps.newHashMap();
		int code = InfoCode.SUCCESS;
		String key_index = String.format(RedisKeys.KEY_INDEX_USER_APP, userName);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		// 用户所绑定的应用映射表
		Map<String, String> appMap = redis.hgetAll(key_index);
		// 验证是否存在应用
		if (appMap.containsKey(appId)) {
			// 原来的应用名称
			String srcName = appMap.get(appId);
			// 原来的应用key
			String key_data = String.format(RedisKeys.KEY_DATA_APP, srcName);
			// 名称发生变化
			if (!srcName.equals(appName)) {
				// 是否已经存在该名称
				if (redis.exists(String.format(RedisKeys.KEY_DATA_APP, appName))) {
					code = InfoCode.EXISTS_APP_NAME;
				} else {
					redis.hset(key_index, appId, appName);
					final String oldKey = key_data;
					final String newKey = String.format(RedisKeys.KEY_DATA_APP, appName);
					redis.renamenx(oldKey, newKey);
					redis.hset(newKey, "order", order);
					redis.hset(newKey, "name", appName);
				}
			} else {
				// 名称保存不变,只是修改序号
				redis.hset(key_data, "order", order);
			}
		} else {
			// 异常操作
			code = InfoCode.NONE;
		}
		map.put("code", code);
		return map;
	}

	/**
	 * 更新应用相关信息配置
	 * 
	 * @param channel
	 * @param appId
	 * @param appName
	 * @param order
	 * @return
	 */
	public Map<String, Object> updateRegion(Channel channel, String appId, final String regionId, String regionName, String order, String forbiddenType, String forbiddenValue) {
		Map<String, Object> map = Maps.newHashMap();
		int code = InfoCode.SUCCESS;
		String key_index = String.format(RedisKeys.KEY_INDEX_APP_REGION, appId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);

		// 用户所绑定的分区映射表
		Map<String, String> regionMap = redis.hgetAll(key_index);

		Map<String, String> src = Maps.filterValues(regionMap, new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.equalsIgnoreCase(regionId);
			}
		});
		String key_data = String.format(RedisKeys.KEY_DATA_REGION, regionId);
		// 验证是否存在分区
		if (!src.isEmpty()) {
			// 原来的应用名称
			String srcName = Lists.newArrayList(src.keySet()).get(0);
			// 名称发生变化
			if (!srcName.equals(regionName)) {
				// 是否已经存在该名称
				if (regionMap.containsKey(regionName)) {
					code = InfoCode.EXISTS_REGION_NAME;
				} else {
					// 新增索引
					redis.hset(key_index, regionName, regionId);
					// 删除原有键
					redis.hdel(key_index, srcName);
					// 修改保存呢最新配置
					redis.hset(key_data, "name", regionName);
					redis.hset(key_data, "order", order);
					redis.hset(key_data, "forbiddenType", forbiddenType);
					redis.hset(key_data, "forbiddenValue", forbiddenValue);
				}
			} else {
				// 名称保存不变,其他修改
				redis.hset(key_data, "order", order);
				redis.hset(key_data, "forbiddenType", forbiddenType);
				redis.hset(key_data, "forbiddenValue", forbiddenValue);
			}
		} else {
			// 异常操作
			code = InfoCode.NONE;
		}
		map.put("code", code);
		return map;
	}

	/**
	 * 更新应用相关信息配置
	 * 
	 * @param channel
	 * @param appId
	 * @param appName
	 * @param order
	 * @return
	 */
	public Map<String, Object> updateServer(Channel channel, final String regionId, final String serverId, String serverName, String order, String forbiddenType, String forbiddenValue,
			String version, String status) {
		Map<String, Object> map = Maps.newHashMap();
		int code = InfoCode.SUCCESS;
		String key_index = String.format(RedisKeys.KEY_INDEX_REGION_SERVER, regionId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		// 用户所绑定的分区映射表
		Map<String, String> serverMap = redis.hgetAll(key_index);

		Map<String, String> src = Maps.filterValues(serverMap, new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.equalsIgnoreCase(serverId);
			}
		});
		String key_data = String.format(RedisKeys.KEY_DATA_SERVER, serverId);
		// 验证是否存在分区
		if (!src.isEmpty()) {
			// 原来的应用名称
			String srcName = Lists.newArrayList(src.keySet()).get(0);
			// 名称发生变化
			if (!srcName.equals(serverName)) {
				// 是否已经存在该名称
				if (serverMap.containsKey(serverName)) {
					code = InfoCode.EXISTS_SERVER_NAME;
				} else {
					// 新增索引
					redis.hset(key_index, serverName, serverId);
					// 删除原有键
					redis.hdel(key_index, srcName);
					// 修改保存呢最新配置
					redis.hset(key_data, "name", serverName);
					redis.hset(key_data, "version", version);
					redis.hset(key_data, "status", status);
					redis.hset(key_data, "order", order);
					redis.hset(key_data, "forbiddenType", forbiddenType);
					redis.hset(key_data, "forbiddenValue", forbiddenValue);
				}
			} else {
				// 名称保存不变,其他修改
				redis.hset(key_data, "version", version);
				redis.hset(key_data, "status", status);
				redis.hset(key_data, "order", order);
				redis.hset(key_data, "forbiddenType", forbiddenType);
				redis.hset(key_data, "forbiddenValue", forbiddenValue);
			}
		} else {
			// 异常操作
			code = InfoCode.NONE;
		}
		map.put("code", code);
		return map;
	}

	/**
	 * 更新进程相关信息
	 * 
	 * @param channel
	 * @param sid
	 * @param pid
	 * @param json
	 * @return
	 */
	public Map<String, Object> updateProcess(Channel channel, String sid, String pid, String json) {
		Map<String, Object> map = Maps.newHashMap();
		String key_index = String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, sid);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		Map<String, String> data = redis.hgetAll(key_index);
		if (data.containsKey(pid)) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, String> jsonObject = BeanUtil.readValue(json, Map.class);
				jsonObject.remove("id");
				String key_data = String.format(RedisKeys.KEY_DATA_PROCESS, sid, pid);
				redis.hmset(key_data, jsonObject);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
		map.put("code", InfoCode.SUCCESS);
		return map;
	}

	/**
	 * 应用分区
	 * 
	 * @param appId
	 * @return
	 */
	public List<?> loadRegions(Channel channel, String appId) {
		String key_region = String.format(RedisKeys.KEY_INDEX_APP_REGION, appId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		Map<String, String> regionMap = redis.hgetAll(key_region);
		List<LoadBalanceRegion> list = Lists.newArrayList();
		for (Map.Entry<String, String> entry : regionMap.entrySet()) {
			String contentRegion = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_REGION, entry.getValue())));
			LoadBalanceRegion loadBalanceRegion = BeanUtil.readValue(contentRegion, LoadBalanceRegion.class);
			list.add(loadBalanceRegion);
		}
		list = LOAD_ORDER.sortedCopy(list);
		return list;
	}

	public Map<String, Object> changeName(Channel channel, String type, String id, String newName) {
		Map<String, Object> map = Maps.newHashMap();
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		String key = null;
		int code = InfoCode.SUCCESS;
		int _type = Integer.valueOf(type);
		if (_type == Constant.TYPE_APP) {
			key = String.format(RedisKeys.KEY_DATA_APP, id);
		} else if (_type == Constant.TYPE_REGION) {
			key = String.format(RedisKeys.KEY_DATA_REGION, id);
		} else if (_type == Constant.TYPE_SERVER) {
			key = String.format(RedisKeys.KEY_DATA_SERVER, id);
		}
		if (null != key) {
			if (!redis.exists(key)) {
				redis.hset(key, "name", newName);
			} else {
				code = InfoCode.NAME_EXISTS;
			}
		}
		map.put("code", code);
		return map;
	}

	/**
	 * 应用分区
	 * 
	 * @param appName
	 * @return
	 */
	public List<?> loadServers(Channel channel, String regionId) {
		String key = String.format(RedisKeys.KEY_INDEX_REGION_SERVER, regionId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		Map<String, String> data = redis.hgetAll(key);
		List<LoadBalanceServer> list = Lists.newArrayList();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String id = entry.getValue();
			String contentServer = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_SERVER, id)));
			LoadBalanceServer loadBalanceServer = BeanUtil.readValue(contentServer, LoadBalanceServer.class);
			list.add(loadBalanceServer);
		}
		list = LOAD_ORDER.sortedCopy(list);
		return list;
	}

	public List<?> loadProcesses(Channel channel, String serverId) {
		String key = String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, serverId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		Map<String, String> data = redis.hgetAll(key);
		List<LoadBalanceProcess> list = Lists.newArrayList();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String id = entry.getKey();
			String contentProcess = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_PROCESS, serverId, id)));
			LoadBalanceProcess loadBalanceProcess = BeanUtil.readValue(contentProcess, LoadBalanceProcess.class);
			list.add(loadBalanceProcess);
		}
		list = LOAD_ORDER.sortedCopy(list);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> addRegion(Channel channel, String appId, String regionName, String order, String forbiddenType, String forbiddenValue) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		String key_region = String.format(RedisKeys.KEY_INDEX_APP_REGION, appId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		boolean existsRegionName = redis.hexists(key_region, regionName);
		if (!existsRegionName) {
			String uuid = BeanUtil.getUUid();
			// 新建索引
			redis.hset(key_region, regionName, uuid);
			// 新增数据
			Map<String, String> regionData = Maps.newHashMap();
			regionData.put("id", uuid);
			regionData.put("name", regionName);
			regionData.put("order", order);
			regionData.put("forbiddenType", forbiddenType);
			regionData.put("forbiddenValue", forbiddenValue);
			redis.hmset(String.format(RedisKeys.KEY_DATA_REGION, uuid), regionData);
			map.put("id", uuid);
		} else {
			code = InfoCode.EXISTS_REGION_NAME;
		}
		map.put("code", code);
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> removeRegion(Channel channel, String appId, String regionId) {

		int code = InfoCode.SUCCESS;

		Map<String, Object> map = Maps.newHashMap();
		// 数据配置键
		String key_data = String.format(RedisKeys.KEY_DATA_REGION, regionId);
		// 索引键
		String key_index = String.format(RedisKeys.KEY_INDEX_APP_REGION, appId);
		//
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		// 服务器名称
		String name = redis.hget(key_data, "name");
		// 是否存在该服务器
		boolean existsName = redis.hexists(key_index, null != name ? name : "");
		// 存在则进行对应删除动作
		if (existsName) {
			// 删除数据详情
			redis.del(key_data);
			// 从分区索引中删除
			redis.hdel(key_index, name);
			// 获取分区下的所有服务器,一并进行删除
			String key_children = String.format(RedisKeys.KEY_INDEX_REGION_SERVER, regionId);
			// 分区下的服务器索引映射表
			Map<String, String> childrenMap = redis.hgetAll(key_children);

			for (Map.Entry<String, String> entry : childrenMap.entrySet()) {
				// String server_name= entry.getKey();
				String server_id = entry.getValue();
				// 删除服务器
				removeServer(channel, regionId, server_id);
			}
		} else {
			// 不存在该分区名称
			code = InfoCode.NOT_EXISTS_REGION_NAME;
		}
		map.put("code", code);

		LOGGER.info("appId={} regionId={} ,regionName={} code={}", new Object[] { appId, regionId, name, code });
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> addServer(Channel channel, String regionId, String serverName, String version, String order, String forbiddenType, String forbiddenValue) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		String key_server = String.format(RedisKeys.KEY_INDEX_REGION_SERVER, regionId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		boolean existsServerName = redis.hexists(key_server, serverName);
		if (!existsServerName) {
			String uuid = BeanUtil.getUUid();
			// 新建索引
			redis.hset(key_server, serverName, uuid);
			// 新增数据
			Map<String, String> data = Maps.newHashMap();
			data.put("id", uuid);
			data.put("name", serverName);
			data.put("version", version);
			data.put("order", order);
			data.put("status", String.valueOf(InfoCode.SERVER_NORMAL));
			data.put("forbiddenType", forbiddenType);
			data.put("forbiddenValue", forbiddenValue);
			redis.hmset(String.format(RedisKeys.KEY_DATA_SERVER, uuid), data);
			map.put("id", uuid);
		} else {
			code = InfoCode.EXISTS_SERVER_NAME;
		}
		map.put("code", code);
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> removeServer(Channel channel, String regionId, String serverId) {
		int code = InfoCode.SUCCESS;

		Map<String, Object> map = Maps.newHashMap();
		// 数据配置键
		String key_data = String.format(RedisKeys.KEY_DATA_SERVER, serverId);
		// 索引键
		String key_index = String.format(RedisKeys.KEY_INDEX_REGION_SERVER, regionId);
		//
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		// 服务器名称
		String name = redis.hget(key_data, "name");
		// 是否存在该服务器
		boolean existsName = redis.hexists(key_index, null != name ? name : "");
		// 存在则进行对应删除动作
		if (existsName) {
			// 删除数据详情
			redis.del(key_data);
			// 从分区索引中删除
			redis.hdel(key_index, name);
			// 获取该服务器下的进程配置
			Map<String, String> processMap = redis.hgetAll(String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, serverId));
			for (Map.Entry<String, String> entry : processMap.entrySet()) {
				String processId = entry.getKey();
				this.removeProcess(channel, serverId, processId);
			}
		} else {
			// 不存在该服务器名称
			code = InfoCode.NOT_EXISTS_SERVER_NAME;
		}
		map.put("code", code);
		LOGGER.info("regionId={} serverId={} ,serverName={} code={}", new Object[] { regionId, serverId, name, code });
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> removeProcess(Channel channel, String serverId, String processId) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		// 数据键
		String key_data = String.format(RedisKeys.KEY_DATA_PROCESS, serverId, processId);
		// 索引键
		String key_index = String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, serverId);
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		// 如果存在则进行对应的删除动作
		if (redis.hexists(key_index, processId)) {
			redis.del(key_data);
			redis.hdel(key_index, processId);
		} else {
			code = InfoCode.NOT_EXISTS_PROCESS;
		}
		map.put("code", code);
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> addProcess(Channel channel, String serverId, String processId, String order, String host, String port, String usedMemory, String online) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		// 服务器下的进程映射表
		String key_index = String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, serverId);
		// 进程uuid
		String uuid = (processId.length() < 1 ? BeanUtil.getUUid() : processId);

		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);

		Map<String, String> processMap = redis.hgetAll(key_index);

		// if (!processMap.containsKey(uuid)) {
		final String server_index = host + ":" + port;
		// 是否存在相同主机地址以及端口配置
		Map<String, String> same = Maps.filterValues(processMap, new Predicate<String>() {
			public boolean apply(String input) {
				return input.equalsIgnoreCase(server_index);
			}
		});

		if (!same.isEmpty()) {
			// 存在相同主机地址和端口
			// code = InfoCode.EXISTS_HOST_AND_PORT;
			// 获取旧的键
			String old_uuid = Lists.newArrayList(same.keySet()).get(0);
			// 删除旧数据
			redis.hdel(key_index, old_uuid);
			// 删除数据键
			redis.del(String.format(RedisKeys.KEY_DATA_PROCESS, serverId, old_uuid));
		}
		int _port = Integer.valueOf(port);
		if (_port <= 0) {
			_port = 9999;
			port = "9999";
		}

		// 新增数据
		Map<String, String> data = Maps.newHashMap();
		if (uuid.length() > 0) {
			data.put("id", uuid);
		}
		if (host.length() > 0) {
			data.put("host", host);
		}
		if (port.length() > 0) {
			data.put("port", port);
		}
		if (usedMemory.length() > 0) {
			data.put("usedMemory", String.valueOf(usedMemory));
		}
		if (online.length() > 0) {
			data.put("online", String.valueOf(online));
		}
		if (order.length() > 0) {
			data.put("order", order);
		}
		String key_data = String.format(RedisKeys.KEY_DATA_PROCESS, serverId, uuid);
		// 保存完整数据
		redis.hmset(key_data, data);
		// 新建索引
		redis.hset(key_index, uuid, server_index);
		map.put("id", uuid);

		// }
		map.put("code", code);
		return map;
	}

	/**
	 * 校验ip是否可以正常加载数据
	 * 
	 * @param channel
	 * @param forbiddenType
	 * @param id
	 * @return
	 */
	public boolean isVaildIp(Channel channel, int forbiddenType, String id) {
		boolean flag = true;
		String ip = channel.remoteAddress().toString().replace("/", "").split(":")[0];
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		String key = null;
		if (forbiddenType == Constant.TYPE_REGION) {
			key = String.format(RedisKeys.KEY_DATA_REGION, id);
		} else if (forbiddenType == Constant.TYPE_SERVER) {
			key = String.format(RedisKeys.KEY_DATA_SERVER, id);
		}

		Map<String, String> dataMap = redis.hgetAll(key);
		// 获取过滤的类型
		String forbidden_item_type = dataMap.get("forbiddenType");
		// 获取过滤类型的值
		String forbidden_item_value = dataMap.get("forbiddenValue");

		if (null == forbidden_item_type || "0".equals(forbidden_item_type)) {
			flag = true;
		} else if ("1".equals(forbidden_item_type)) {// 白名单模式
			String forbidden_item_value_array[] = forbidden_item_value.split(",");
			BalanceForbidden balanceForbidden = new BalanceForbidden();
			for (String forbidden_item_value_config : forbidden_item_value_array) {
				if (forbidden_item_value_config.indexOf("-") != -1) {
					balanceForbidden.addWhitesBetween(forbidden_item_value_config);
				} else {
					balanceForbidden.addWhite(forbidden_item_value_config);
				}
			}
			flag = balanceForbidden.isVaildWhite(ip);
		} else if ("2".equals(forbidden_item_type)) {// 黑名单模式
			String forbidden_item_value_array[] = forbidden_item_value.split(",");
			BalanceForbidden balanceForbidden = new BalanceForbidden();
			for (String forbidden_item_value_config : forbidden_item_value_array) {
				if (forbidden_item_value_config.indexOf("-") != -1) {
					balanceForbidden.addBlackBetween(forbidden_item_value_config);
				} else {
					balanceForbidden.addBlack(forbidden_item_value_config);
				}
			}
			flag = balanceForbidden.isVaildBlack(ip);
		}
		return flag;
	}

	public List<?> loadByServer(Channel channel, String serverId) {
		Map<String, String> map = Maps.newHashMap();
		List<LoadBalanceProcess> processList = Lists.newArrayList();
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		map = redis.hgetAll(String.format(RedisKeys.KEY_DATA_SERVER, serverId));
		if (!map.isEmpty()) {
			LoadBalanceServer loadBalanceServer = null;
			String content = BeanUtil.writeValueAsString(map);
			loadBalanceServer = BeanUtil.readValue(content, LoadBalanceServer.class);
			String ip = channel.remoteAddress().toString().replace("/", "").split(":")[0];
			if (null != loadBalanceServer && loadBalanceServer.isVaildIp(ip)) {
				// 获取服务器下的进程列表
				Map<String, String> processMap = redis.hgetAll(String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, serverId));
				for (Map.Entry<String, String> processEntry : processMap.entrySet()) {
					String processId = processEntry.getKey();
					String contentProcess = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_PROCESS, serverId, processId)));
					LoadBalanceProcess loadBalanceProcess = BeanUtil.readValue(contentProcess, LoadBalanceProcess.class);
					processList.add(loadBalanceProcess);
				}
				// 根据是否能够连接情况进行排序
				Ordering<LoadBalanceProcess> ORDER_BY_HEARTBEAT = Ordering.natural().nullsFirst().onResultOf(new Function<LoadBalanceProcess, Integer>() {
					public Integer apply(LoadBalanceProcess input) {
						String host = input.getHost();
						int port = input.getPort();
						return SocketUtil.isStart(host, port) ? -1 : 0;
					}
				});
				// 根据负载进行排序
				Ordering<LoadBalanceProcess> ORDER_BY_LOAD = Ordering.natural().nullsFirst().onResultOf(new Function<LoadBalanceProcess, Integer>() {
					public Integer apply(LoadBalanceProcess input) {
						float usedMemory = input.getUsedMemory() + 1;
						float online = input.getOnline() + 1;
						float order = input.getOnline() + 1;
						return (int) (order / (online / usedMemory));
					}
				});
				processList = ORDER_BY_HEARTBEAT.compound(ORDER_BY_LOAD).nullsFirst().nullsLast().sortedCopy(processList);
			}
		}
		return processList;
	}

	/**
	 * 加载分区下的服务器信息
	 * 
	 * @param channel
	 * @param regionId
	 * @return
	 */
	public List<?> loadByRegion(Channel channel, String regionId) {
		Map<String, String> map = Maps.newHashMap();
		List<LoadBalanceServer> list = Lists.newArrayList();
		RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
		map = redis.hgetAll(String.format(RedisKeys.KEY_DATA_REGION, regionId));
		if (!map.isEmpty()) {
			String contentRegion = BeanUtil.writeValueAsString(map);
			LoadBalanceRegion loadBalanceRegion = BeanUtil.readValue(contentRegion, LoadBalanceRegion.class);
			String ip = channel.remoteAddress().toString().replace("/", "").split(":")[0];
			if (null != loadBalanceRegion && loadBalanceRegion.isVaildIp(ip)) {
				String key_index = String.format(RedisKeys.KEY_INDEX_REGION_SERVER, regionId);
				Map<String, String> serverMap = redis.hgetAll(key_index);

				for (Map.Entry<String, String> entry : serverMap.entrySet()) {
					// 服务器名称
					// String serverName = entry.getKey();
					// 服务器id
					String serverId = entry.getValue();

					String contentServer = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_SERVER, serverId)));

					LoadBalanceServer loadBalanceServer = BeanUtil.readValue(contentServer, LoadBalanceServer.class);

					if (null != loadBalanceServer && loadBalanceServer.isVaildIp(ip)) {
						// 获取服务器下的进程列表
						Map<String, String> processMap = redis.hgetAll(String.format(RedisKeys.KEY_INDEX_SERVER_PROCESS, serverId));
						if (processMap.isEmpty()) {
							continue;
						}
						List<LoadBalanceProcess> processList = Lists.newArrayList();
						for (Map.Entry<String, String> processEntry : processMap.entrySet()) {
							String processId = processEntry.getKey();
							// String processConfig = processEntry.getValue();
							String contentProcess = BeanUtil.writeValueAsString(redis.hgetAll(String.format(RedisKeys.KEY_DATA_PROCESS, serverId, processId)));
							LoadBalanceProcess loadBalanceProcess = BeanUtil.readValue(contentProcess, LoadBalanceProcess.class);
							processList.add(loadBalanceProcess);
						}
						if (processList.isEmpty()) {
							continue;
						}
						// 排序最前的
						LoadBalanceProcess min = LOAD_ORDER.sortedCopy(processList).get(0);
						loadBalanceServer.setHost(min.getHost());
						loadBalanceServer.setPort(min.getPort());
						loadBalanceServer.setOnline(min.getOnline());
						list.add(loadBalanceServer);
					}
				}
			}
		}
		list = LOAD_ORDER.sortedCopy(list);
		return list;
	}
}
