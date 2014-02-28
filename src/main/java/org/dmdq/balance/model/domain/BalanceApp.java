package org.dmdq.balance.model.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.dmdq.balance.model.LoadApplication;
import org.dmdq.balance.model.LoadManager;
import org.dmdq.balance.model.LoadRegion;
import org.xml.sax.Attributes;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public final class BalanceApp implements LoadApplication {

	private static final long serialVersionUID = 1L;

	/**
	 * 区id=>区数据的映射表
	 */
	private Map<String, LoadRegion> loadRegions;

	/**
	 * 父类对象(应用管理器)
	 */
	private LoadManager parent;

	/**
	 * 应用名称
	 */
	private String name;
	/**
	 * 应用ID
	 */
	private String id;
	/**
	 * 应用排序
	 */
	private int order;

	public BalanceApp() {
		loadRegions = Maps.newConcurrentMap();
	}

	@JsonIgnore
	@Override
	public final Map<String, LoadRegion> getLoadRegions() {
		return loadRegions;
	}

	@Override
	public final void setLoadRegions(Map<String, LoadRegion> loadRegions) {
		this.loadRegions = loadRegions;
	}

	@Override
	public final void addLoadRegion(final LoadRegion loadRegion) {
		loadRegions.put(loadRegion.getId(), loadRegion);
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
				order = Integer.valueOf(value);
			}
		}
	}

	@Override
	public final Set<String> getRegionIds() {
		return loadRegions.keySet();
	}

	@Override
	public final LoadRegion removeRegion(final String loadRegionId) {
		return loadRegions.remove(loadRegionId);
	}

	public final void setParent(final LoadManager parent) {
		this.parent = parent;
	}

	@JsonIgnore
	@Override
	public final LoadManager getParent() {
		return parent;
	}

	@Override
	public final LoadRegion getLoadRegion(final String loadRegionId) {
		return loadRegions.get(loadRegionId);
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
	public final List<LoadRegion> sort() {
		List<LoadRegion> list = Lists.newArrayList(this.loadRegions.values());
		Ordering<LoadRegion> aOrder = Ordering.natural().nullsFirst().onResultOf(new Function<LoadRegion, Integer>() {
			public Integer apply(LoadRegion input) {
				return input.getOrder();
			}
		});
		list = aOrder.sortedCopy(list);
		return list;
	}

}
