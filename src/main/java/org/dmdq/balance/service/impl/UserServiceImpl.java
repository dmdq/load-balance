package org.dmdq.balance.service.impl;

import io.netty.channel.Channel;

import java.util.Map;

import org.dmdq.balance.conf.InfoCode;
import org.dmdq.balance.conf.RedisKeys;
import org.dmdq.balance.dao.impl.RedisDaoImpl;
import org.dmdq.balance.service.UserService;
import org.dmdq.balance.util.BeanUtil;
import org.dmdq.balance.util.MD5Util;

import com.google.common.collect.Maps;

public class UserServiceImpl implements UserService {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

	private Map<String, String> users;

	public UserServiceImpl() {
		users = Maps.newHashMap();
	}

	/**
	 * 加载用户信息
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public Map<String, Object> loadUser(Channel channel, String userName, String password) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		Map<String, String> userData = BeanUtil.getBean(RedisDaoImpl.class).hgetAll(String.format(RedisKeys.KEY_DATA_USER, userName));
		if (userData.isEmpty()) {
			code = InfoCode.NOT_EXISTS_USER_NAME;
		} else {
			String truePassword = userData.get("password");
			if (!password.equals(truePassword)) {
				code = InfoCode.USER_PASSWORD_ERROR;
			} else {
				map.putAll(userData);
			}
		}
		map.put("code", code);
		return map;
	}

	/**
	 * 添加用户
	 * 
	 * @param userName
	 * @param password
	 */
	public Map<String, Object> addUser(Channel channel, String userName, String password) {
		return this.addUser(channel, userName, password, 022);
	}

	/**
	 * 添加用户
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param chmod
	 *            权限
	 */
	public Map<String, Object> addUser(Channel channel, String userName, String password, int chmod) {
		Map<String, Object> map = Maps.newHashMap();
		int code = InfoCode.SUCCESS;
		if (users.containsKey(userName)) {
			code = InfoCode.EXISTS_USER_NAME;
		} else {
			String key = String.format(RedisKeys.KEY_DATA_USER, userName);
			RedisDaoImpl redis = BeanUtil.getBean(RedisDaoImpl.class);
			if (redis.exists(key)) {
				code = InfoCode.EXISTS_USER_NAME;
			} else {
				Map<String, String> userInfo = Maps.newHashMap();
				String regTime = String.valueOf(System.currentTimeMillis());
				String md5key = MD5Util.getMD5String(userName + password + regTime).toUpperCase();
				this.users.put(userName, md5key);
				userInfo.put("name", userName);
				userInfo.put("password", password);
				userInfo.put("regTime", regTime);
				userInfo.put("md5key", md5key);
				userInfo.put("chmod", String.valueOf(chmod));
				map.put("md5key", md5key);
				redis.hmset(key, userInfo);
				redis.hset(RedisKeys.KEY_LIST_USERS, md5key, userName);
				LOGGER.info("name={} password={} chmod={}", new Object[] { userName, password, chmod });
			}
		}
		map.put("code", code);
		return map;
	}

	public Map<String, Object> removeUser(Channel channel, String userName, String md5Key) {
		Map<String, Object> map = Maps.newHashMap();
		int code = InfoCode.SUCCESS;
		RedisDaoImpl redisDao = BeanUtil.getBean(RedisDaoImpl.class);
		String trueUserName = redisDao.hget(RedisKeys.KEY_LIST_USERS, md5Key);

		if (null != trueUserName && trueUserName.equals(userName)) {
			// 删除索引
			redisDao.hdel(RedisKeys.KEY_LIST_USERS, md5Key);
			// 删除用户数据
			redisDao.del(String.format(RedisKeys.KEY_DATA_USER, userName));
			// 获取用户所有的应用配置信息
			String userAppKey = String.format(RedisKeys.KEY_INDEX_USER_APP, userName);
			Map<String, String> appNameKeyMap = redisDao.hgetAll(userAppKey);
			for (Map.Entry<String, String> entry : appNameKeyMap.entrySet()) {
				// String appNameMd5 = entry.getKey();
				String appName = entry.getValue();
				String appKey = String.format(RedisKeys.KEY_DATA_APP, appName);
				redisDao.del(appKey);
			}
			// 删除用户的应用信息
			redisDao.del(userAppKey);
			this.users.remove(userName);
		} else {
			code = InfoCode.NOT_EXISTS_USER_NAME;
		}
		map.put("code", code);
		return map;
	}

	public Map<String, String> getUsers() {
		return users;
	}

	/**
	 * 从redis恢复用户数据
	 */
	public void getFromRedis() {
		users.putAll(BeanUtil.getBean(RedisDaoImpl.class).hgetAll(RedisKeys.KEY_LIST_USERS));
	}

}
