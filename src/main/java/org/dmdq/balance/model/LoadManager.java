package org.dmdq.balance.model;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.dmdq.balance.util.AtomicLongMap;

/**
 * 负载管理
 */
public interface LoadManager extends LoadXML, LoadSort<LoadApplication> {

	/**
	 * 获取应用id=>应用数据的映射表
	 */
	Map<String, LoadApplication> getLoadApplications();

	/**
	 * 根据应用id获取应用数据
	 * 
	 * @param id
	 * 
	 */
	LoadApplication getLoadApplication(final String id);

	/**
	 * 添加一款应用
	 * 
	 * @param loadApplication
	 *            应用
	 */
	void addLoadApplication(final LoadApplication loadApplication);

	/**
	 * 移除某一款应用
	 * 
	 * @param appId
	 *            应用id
	 */
	LoadApplication removeApp(final String appId);

	/**
	 * 添加分区表<br/>
	 * 
	 * 区id=>应用id
	 * 
	 * @param regionId
	 *            分区id
	 * @param appId
	 *            应用id
	 */
	void addRegionId(final String regionId, final String appId);

	/**
	 * 获取分区和应用的id表
	 */
	Map<String, String> getRegionIdMap();

	/**
	 * 添加服务器表
	 * 
	 * @param serverId
	 *            服务器id
	 * @param regionId
	 *            所在的分区id
	 */
	void addServerId(final String serverId, final String regionId);

	/**
	 * 取得服务器表
	 * 
	 * @return 服务器表
	 */
	Map<String, String> getServerIdMap();

	/**
	 * 添加子进程 
	 */
	void addProcess(final LoadProcess loadProcess);

	AtomicLongMap<LoadProcess> getHeartbeat();

	void setLoadManager(final LoadManager loadManager);

	File getConfig();

	void setConfig(final File config);

	String cronExpression();

	AtomicInteger getScanPeriod();

	void setScanPeriod(final AtomicInteger scanPeriod);

}
