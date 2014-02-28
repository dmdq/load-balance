package org.dmdq.balance.model.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.dmdq.balance.model.LoadApplication;
import org.dmdq.balance.model.LoadRegion;
import org.dmdq.balance.model.LoadServer;
import org.xml.sax.Attributes;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

/**
 * 服务器分区
 */
public final class BalanceRegion implements LoadRegion {

	private static final long serialVersionUID = 1L;
	/**
	 * 服务器id=>服务器数据的映射表
	 */
	private Map<String, LoadServer> loadServers;
	/**
	 * 父类对象(具体应用)
	 */
	private LoadApplication parent;
	/**
	 * 分区名称
	 */
	private String name;

	/**
	 * 分区ID
	 */
	private String id;

	/**
	 * 分区排序
	 */
	private int order;

	public BalanceRegion() {
		loadServers = Maps.newConcurrentMap();
	}

	@JsonIgnore
	@Override
	public final Map<String, LoadServer> getLoadServers() {
		return loadServers;
	}

	@Override
	public final void setLoadServers(final Map<String, LoadServer> loadServers) {
		this.loadServers = loadServers;
	}

	@Override
	public final void addLoadServer(final LoadServer loadServer) {
		loadServers.put(loadServer.getId(), loadServer);
	}

	@Override
	public final void parseXML(final Attributes attributes) {
		String qName;
		String value;
		for (int i = 0; i < attributes.getLength(); i++) {
			qName = attributes.getQName(i);
			value = attributes.getValue(i);
			if ("id".equalsIgnoreCase(qName)) {
				id = value;
			} else if ("name".equalsIgnoreCase(qName)) {
				name = value;
			} else if ("order".equalsIgnoreCase(qName)) {
				order = Integer.parseInt(value);
			}
		}
	}

	@Override
	public final Set<String> getServerIds() {
		return loadServers.keySet();
	}

	@Override
	public final LoadServer removeServer(String serverId) {
		return loadServers.remove(serverId);
	}

	@JsonIgnore
	@Override
	public final LoadApplication getParent() {
		return parent;
	}

	public final void setParent(final LoadApplication parent) {
		this.parent = parent;
	}

	@JsonIgnore
	@Override
	public final LoadServer getLoadServer(final String loadServerId) {
		return loadServers.get(loadServerId);
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(final String name) {
		this.name = name;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final void setId(final String id) {
		this.id = id;
	}

	@Override
	public final void setOrder(final int order) {
		this.order = order;
	}

	@Override
	public final int getOrder() {
		return order;
	}

	@Override
	public final List<LoadServer> sort() {
		List<LoadServer> list = Lists.newArrayList(this.loadServers.values());
		Ordering<LoadServer> aOrder = Ordering.natural().nullsFirst().onResultOf(new Function<LoadServer, Integer>() {
			public Integer apply(LoadServer input) {
				return input.getOrder();
			}
		});
		list = aOrder.sortedCopy(list);
		return list;
	}

}
