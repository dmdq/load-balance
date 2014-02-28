package org.dmdq.balance.model;

/**
 * 分区
 */
public interface LoadRegion extends LoadBase, LoadOrder, LoadXML, LoadSort<LoadServer> {

	/**
	 * 获取服务器数据
	 * 
	 * @param loadServerId
	 */
	LoadServer getLoadServer(String loadServerId);

	/**
	 * 获取服务器列表
	 */
	java.util.Map<String, LoadServer> getLoadServers();

	/**
	 * 设置服务器列表
	 * 
	 * @param loadServers
	 *            服务器列表
	 */
	void setLoadServers(java.util.Map<String, LoadServer> loadServers);

	/**
	 * 添加服务器
	 * 
	 * @param loadServer
	 *            服务器
	 */
	void addLoadServer(LoadServer loadServer);

	/**
	 * 获取某一分区下的所有服务器id列表
	 * 
	 * @param regionId
	 *            分区id
	 */
	java.util.Set<String> getServerIds();

	/**
	 * 移除某个服务器
	 * 
	 * @param serverId
	 *            服务器id
	 */
	LoadServer removeServer(String serverId);

	/**
	 *   绑定父类对象
	 * 
	 * @param loadApplication
	 */
	void setParent(LoadApplication loadApplication);

	/**
	 * 获取父类对象
	 */
	LoadApplication getParent();
}
