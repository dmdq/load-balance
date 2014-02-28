package org.dmdq.balance.model.domain;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.dmdq.balance.conf.InfoCode;
import org.dmdq.balance.model.LoadApplication;
import org.dmdq.balance.model.LoadManager;
import org.dmdq.balance.model.LoadProcess;
import org.dmdq.balance.util.AtomicLongMap;
import org.xml.sax.Attributes;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public final class BalanceManager implements LoadManager {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 应用id=>应用的映射表
	 */
	private Map<String, LoadApplication> loadApplications;

	/**
	 * 分区id=>应用id的映射表
	 */
	private Map<String, String> regionIdMap;

	/**
	 * 服务器id=>分区id的映射表
	 */
	private Map<String, String> serverIdMap;
	
	/**
	 * 进程id =>服务器id的映射表
	 */
	private Map<String, String> processIdMap;

	/**
	 * 等待重新连接的队列
	 */
	private AtomicLongMap<LoadProcess> heartbeatItem;

	/**
	 * 进程服的心跳检测时间
	 */
	private AtomicInteger scanPeriod;

	// com.google.common.util.concurrent. AtomicLongMap ;
	/**
	 * 配置文件
	 */
	private File config;

	public BalanceManager() {
		scanPeriod = new AtomicInteger(30);
		loadApplications = Maps.newConcurrentMap();
		regionIdMap = Maps.newConcurrentMap();
		serverIdMap = Maps.newConcurrentMap();
		processIdMap = Maps.newConcurrentMap();
		heartbeatItem = AtomicLongMap.create();
	}

	public final LoadApplication getLoadApplication(final String id) {
		return loadApplications.get(id);
	}

	public final void addLoadApplication(final LoadApplication loadApplication) {
		loadApplications.put(loadApplication.getId(), loadApplication);
	}

	@Override
	public final Map<String, LoadApplication> getLoadApplications() {
		return loadApplications;
	}

	public final void setLoadApps(
			final Map<String, LoadApplication> loadApplications) {
		this.loadApplications = loadApplications;
	}

	@Override
	public final void addRegionId(final String regionId, final String appId) {
		regionIdMap.put(regionId, appId);
	}

	@Override
	public final Map<String, String> getRegionIdMap() {
		return regionIdMap;
	}

	@Override
	public final void addServerId(final String serverId, final String regionId) {
		serverIdMap.put(serverId, regionId);
	}

	@Override
	public final Map<String, String> getServerIdMap() {
		return serverIdMap;
	}

	@Override
	public final LoadApplication removeApp(final String appId) {
		return loadApplications.remove(appId);
	}

	public synchronized final void addProcess(final LoadProcess loadItem) {
		int itemStatus = loadItem.getStatus();
		if (itemStatus == 0 || itemStatus == InfoCode.SERVER_STOP) {
			if (!heartbeatItem.containsKey(loadItem))
				heartbeatItem.addAndGet(loadItem, 0);
		} else {
			heartbeatItem.remove(loadItem);
		}
	}

	@JsonIgnore
	@Override
	public final AtomicLongMap<LoadProcess> getHeartbeat() {
		return heartbeatItem;
	}

	@Override
	public final void setLoadManager(final LoadManager loadManager) {
		this.heartbeatItem = loadManager.getHeartbeat();
		this.loadApplications = loadManager.getLoadApplications();
		this.regionIdMap = loadManager.getRegionIdMap();
		this.serverIdMap = loadManager.getServerIdMap();
		this.config = loadManager.getConfig();
		this.scanPeriod = loadManager.getScanPeriod();
	}

	@Override
	public final File getConfig() {
		return config;
	}

	@Override
	public final void setConfig(final File config) {
		this.config = config;
	}

	@Override
	public final void parseXML(final Attributes attributes) {
		String qName;
		String value;
		for (int i = 0; i < attributes.getLength(); i++) {
			qName = attributes.getQName(i);
			value = attributes.getValue(i);
			if ("scanPeriod".equalsIgnoreCase(qName)) {
				scanPeriod.set(Integer.valueOf(value));
			}
		}
	}

	@Override
	public final String cronExpression() {
		return String.format("0/%d * * ? * *", scanPeriod.get());
	}

	@Override
	public final AtomicInteger getScanPeriod() {
		return scanPeriod;
	}

	@Override
	public final void setScanPeriod(final AtomicInteger scanPeriod) {
		this.scanPeriod = scanPeriod;
	}

	public Map<String, String> getProcessIdMap() {
		return processIdMap;
	}

	public void setProcessIdMap(Map<String, String> processIdMap) {
		this.processIdMap = processIdMap;
	}

	public void setRegionIdMap(Map<String, String> regionIdMap) {
		this.regionIdMap = regionIdMap;
	}

	public void setServerIdMap(Map<String, String> serverIdMap) {
		this.serverIdMap = serverIdMap;
	}

	@Override
	public final List<LoadApplication> sort() {
		List<LoadApplication> list = Lists.newArrayList(this.loadApplications
				.values());
		Ordering<LoadApplication> aOrder = Ordering.natural().nullsFirst()
				.onResultOf(new Function<LoadApplication, Integer>() {
					public Integer apply(LoadApplication input) {
						return input.getOrder();
					}
				});
		list = aOrder.sortedCopy(list);
		return list;
	}

}
