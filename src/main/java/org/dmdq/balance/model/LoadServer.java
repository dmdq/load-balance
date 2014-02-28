package org.dmdq.balance.model;

/**
 * 组列表数据
 * 
 * @author lion
 */
public interface LoadServer extends LoadCalc<LoadProcess>, LoadXML, LoadOrder, LoadSort<LoadProcess> {

	/**
	 * 设置 组id.
	 * 
	 * @param id
	 *            组id
	 */
	void setId(String sid);

	/**
	 * 获取 组id.
	 */
	String getId();

	/**
	 * 设置 组名称.
	 * 
	 * @param name
	 *            组名称
	 */
	void setName(String name);

	/**
	 * 获取 组名称.
	 */
	String getName();

	/**
	 * 设置 组版本号 .
	 * 
	 * @param version
	 *            版本号
	 */
	void setVersion(String version);

	/**
	 * 获取 组版本号 .
	 */
	String getVersion();

	/**
	 * 设置 服务器状态.
	 * 
	 * @param status
	 *            服务器状态
	 * 
	 */
	void setStatus(int status);

	/**
	 * 获取 服务器状态.
	 */
	int getStatus();

	/**
	 * 设置 服务器维护结束时间.
	 * 
	 * @param end
	 *            服务器维护结束时间.
	 */
	void setEnd(int end);

	/**
	 * 获取 服务器维护结束时间.
	 */
	int getEnd();

	/**
	 * 获取 子服务器的数量
	 */
	int getSize();

	/**
	 * 获取 子服务器是否为空
	 */
	boolean isEmpty();

	/**
	 * 添加子服务器
	 * 
	 * @param item
	 *            子服务器
	 */
	boolean addLoadProcess(LoadProcess item);

	/**
	 * 删除子服务器
	 * 
	 * @param itemId
	 *            子服务器id
	 */
	LoadProcess remove(String itemId);

	/**
	 * 获取 服务器基本配置信息
	 */
	java.util.Map<String, Object> toMap();

	/**
	 * 子服务器表（进程列表）
	 */
	java.util.Map<String, LoadProcess> getMap();

	/**
	 * 获取服务器下的所有进程号
	 */
	java.util.Set<String> getProcessIds();

	/**
	 * 获取子进程
	 * 
	 * @param LoadProcessId
	 */
	LoadProcess getLoadProcess(String loadProcessId);

	/**
	 * 绑定父类对象
	 * 
	 * @param loadRegion
	 * 
	 */
	void setParent(LoadRegion loadRegion);

	/**
	 * 获取父类对象
	 */
	LoadRegion getParent();
	
	/**
	 * 获取在线用户的数量
	 */
	int getOnline();
}
