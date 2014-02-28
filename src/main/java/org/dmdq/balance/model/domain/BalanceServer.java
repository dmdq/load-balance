package org.dmdq.balance.model.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.dmdq.balance.conf.InfoCode;
import org.dmdq.balance.model.LoadProcess;
import org.dmdq.balance.model.LoadRegion;
import org.dmdq.balance.model.LoadServer;
import org.dmdq.balance.util.SocketUtil;
import org.xml.sax.Attributes;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

/**
 * 组服务器数据
 * 
 * @author lion
 */
public final class BalanceServer implements LoadServer {

	private static final long serialVersionUID = 1L;
	/**
	 * 组id
	 */
	private String id;
	/**
	 * 组名称
	 */
	private String name;
	/**
	 * 组版本
	 */
	private String version;
	/**
	 * 进程id=>进程数据的映射
	 */
	private Map<String, LoadProcess> map;
	/**
	 * 服务器状态
	 */
	private int status;
	/**
	 * 维护结束时间
	 */
	private int end;
	/**
	 * 服务器序号
	 */
	private int order;
	/**
	 * 父类对象(所在的区)
	 */
	private LoadRegion parent;

	public BalanceServer() {
		id = UUID.randomUUID().toString();
		name = "game";
		version = "v1.0.0 20140127";
		status = InfoCode.SERVER_STOP;
		map = Maps.newConcurrentMap();
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getVersion() {
		return version;
	}

	public final void setVersion(final String version) {
		this.version = version;
	}

	public final boolean addLoadProcess(final LoadProcess loadProcess) {
		boolean flag = false;
		synchronized (map) {
			String host = loadProcess.getHost();
			int port = loadProcess.getPort();
			if (port >= 1024) {
				if (SocketUtil.isStart(host, port)) {
					List<LoadProcess> oldSamles = getItemIdByHost(host, port);
					int status = loadProcess.getStatus();
					for (LoadProcess oldSamle : oldSamles) {
						int oldStatus = oldSamle.getStatus();
						// 是否是系统状态
						if (oldStatus != status && oldStatus != InfoCode.SERVER_STOP) {
							status = oldStatus;
						}
						map.remove(oldSamle.getId());// 移除已经存在的相同主机地址和相同端口的数据
					}
					// 状态延续
					loadProcess.setStatus(status);
					map.put(loadProcess.getId(), loadProcess);
					flag = true;
				} else {
					loadProcess.setOnline(0);
					loadProcess.setUsedMemory(0);
					loadProcess.setStatus(InfoCode.SERVER_STOP);
					map.put(loadProcess.getId(), loadProcess);
				}
				loadProcess.setParent(this);
			}
			return flag;
		}
	}

	private final List<LoadProcess> getItemIdByHost(final String host, final int port) {
		Collection<LoadProcess> sames = Collections2.filter(map.values(), new Predicate<LoadProcess>() {
			public boolean apply(LoadProcess input) {
				return input.getHost().equals(host) && input.getPort() == port;
			}
		});
		return Lists.newArrayList(sames);
	}

	@JsonIgnore
	@Override
	public final Map<String, LoadProcess> getMap() {
		return map;
	}

	public final void setMap(final Map<String, LoadProcess> map) {
		this.map = map;
	}

	public final int getStatus() {
		return status;
	}

	public final void setStatus(final int status) {
		this.status = status;
	}

	public final int getEnd() {
		return end;
	}

	public final void setEnd(final int end) {
		this.end = end;
	}

	@JsonIgnore
	@Override
	public final Map<String, Object> toMap() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("name", name);
		map.put("version", version);
		map.put("id", id);
		map.put("size", map.size());
		map.put("status", status);
		return map;
	}

	public final void parseXML(final Attributes attributes) {
		String qName;
		String value;
		for (int i = 0; i < attributes.getLength(); i++) {
			qName = attributes.getQName(i);
			value = attributes.getValue(i);
			if ("order".equalsIgnoreCase(qName)) {
				order = Integer.valueOf(value);
			} else if ("id".equalsIgnoreCase(qName)) {
				id = value;
			} else if ("name".equalsIgnoreCase(qName)) {
				name = value;
			} else if ("version".equalsIgnoreCase(qName)) {
				version = value;
			}
		}
	}

	@JsonIgnore
	@Override
	public final String toString() {
		return toMap().toString();
	}

	public final LoadProcess remove(final String itemId) {
		return map.remove(itemId);
	}

	@JsonIgnore
	@Override
	public final LoadProcess getItem() {
		List<LoadProcess> list = sort();
		return list.isEmpty() ? null : list.get(0);
	}

	@JsonIgnore
	@Override
	public final List<LoadProcess> sort() {
		List<LoadProcess> list = Lists.newArrayList(this.map.values());
		Ordering<LoadProcess> aOrder = Ordering.natural().nullsFirst().onResultOf(new Function<LoadProcess, Integer>() {
			public Integer apply(LoadProcess input) {
				return input.getStatus();
			}
		}).compound(Ordering.natural().nullsFirst().onResultOf(new Function<LoadProcess, Integer>() {
			public Integer apply(LoadProcess input) {
				return input.getOrder();
			}
		})).compound(Ordering.natural().nullsFirst().onResultOf(new Function<LoadProcess, Integer>() {
			public Integer apply(LoadProcess input) {
				return -(input.getUsedMemory() + 1) / (input.getOnline() + 1);
			}
		})).nullsFirst();
		list = aOrder.sortedCopy(list);
		// if (!list.isEmpty()) {
		// Collections.sort(list, new Comparator<LoadItem>() {
		// public int compare(LoadItem item1, LoadItem item2) {
		//
		// int value1 = (item1.getTotalMemory() - item1.getFreeMemory()) /
		// (item1.getOnline() + 1);
		// int value2 = (item2.getTotalMemory() - item2.getFreeMemory()) /
		// (item2.getOnline() + 1);
		// return value1 - value2;
		// }
		// });
		// Collections.sort(list, new Comparator<LoadItem>() {
		// public int compare(LoadItem item1, LoadItem item2) {
		// int staus1 = item1.getStatus();
		// int staus2 = item2.getStatus();
		// return staus1 - staus2;
		// }
		// });
		// }
		return list;
	}

	public final int getSize() {
		return map.size();
	}

	public final boolean isEmpty() {
		return map.isEmpty();
	}

	@JsonIgnore
	@Override
	public final Set<String> getProcessIds() {
		return map.keySet();
	}

	@JsonIgnore
	@Override
	public final LoadRegion getParent() {
		return parent;
	}

	public final void setParent(final LoadRegion parent) {
		this.parent = parent;
	}

	@Override
	public final LoadProcess getLoadProcess(final String LoadProcessId) {
		return map.get(LoadProcessId);
	}

	@Override
	public final void setOrder(final int order) {
		this.order = order;
	}

	@Override
	public final int getOrder() {
		return this.order;
	}

	@Override
	public int getOnline() {
		int online = 0;
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			online += map.get(key).getOnline();
		}
		return online;
	}

}