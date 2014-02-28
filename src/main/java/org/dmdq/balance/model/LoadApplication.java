package org.dmdq.balance.model;

/**
 * 应用相关信息
 */
public interface LoadApplication extends LoadBase, LoadOrder, LoadXML, LoadSort<LoadRegion> {

	/**
	 * 获取分区数据
	 * 
	 * @param loadRegionId
	 */
	LoadRegion getLoadRegion(String loadRegionId);

	/**
	 * 获取 应用分区表
	 * 
	 * @return 应用分区表
	 */
	java.util.Map<String, LoadRegion> getLoadRegions();

	/**
	 * 设置分区列表
	 */
	void setLoadRegions(java.util.Map<String, LoadRegion> loadRegions);

	/**
	 * 添加分区
	 * 
	 * @param loadRegion
	 *            分区相关信息
	 */
	void addLoadRegion(LoadRegion loadRegion);

	/**
	 * 获取当前应用下的所有分区id
	 */
	java.util.Set<String> getRegionIds();

	/**
	 * 移除某一分区
	 * 
	 * @param loadRegionId
	 *            分区id
	 */
	LoadRegion removeRegion(String loadRegionId);

	/**
	 * 绑定父类对象
	 */
	void setParent(LoadManager parent);

	/**
	 * 获取父类对象
	 */
	LoadManager getParent();

}