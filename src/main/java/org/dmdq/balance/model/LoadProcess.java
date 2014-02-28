package org.dmdq.balance.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 子服务器（进程）
 * 
 * @author lion
 */
public interface LoadProcess extends LoadXML, LoadOrder {

	/**
	 * 获取 服务器组id
	 */
	String getSid();

	/**
	 * 设置 服务器组id
	 * 
	 * @param group
	 *            组id
	 */
	void setSid(String group);

	/**
	 * 获取 服务器id
	 */
	String getId();

	/**
	 * 设置 服务器id
	 * 
	 * @param id
	 *            服务器id
	 */
	void setId(String id);

	/**
	 * 获取 服务器IP地址
	 */
	String getHost();

	/**
	 * 设置 服务器IP地址
	 * 
	 * @param host
	 *            服务器ip地址
	 */
	void setHost(String host);

	/**
	 * 获取 服务器端口
	 */
	int getPort();

	/**
	 * 设置 服务器端口
	 * 
	 * @param port
	 *            服务器端口
	 */
	void setPort(int port);

	/**
	 * 获取 服务器在线人数
	 */
	int getOnline();

	/**
	 * 设置 服务器在线人数
	 * 
	 * @param online
	 *            在线人数、连接数
	 */
	void setOnline(int online);

	/**
	 * 获取 服务器总内存
	 */
	int getUsedMemory();

	/**
	 * 设置 服务器总内存
	 * 
	 * @param usedMemory
	 *            总共内存
	 */
	void setUsedMemory(int usedMemory);

	//
	// /**
	// * 获取 服务器剩余内存
	// */
	// int getFreeMemory();
	//
	// /**
	// * 设置 服务器剩余内存
	// *
	// * @param freeMemory
	// * 服务器剩余内存
	// */
	// void setFreeMemory(int freeMemory);

	/**
	 * 获取 服务器状态
	 */
	int getStatus();

	/**
	 * 设置 服务器状态
	 * 
	 * @param status
	 *            服务器状态
	 */
	void setStatus(int status);

	/**
	 * 获取 基本配置信息
	 */
	java.util.Map<String, Object> toConfig();

	/**
	 * 获取 详细配置信息
	 */
	java.util.Map<String, Object> toMap();

	/**
	 * 绑定父类对象
	 * 
	 * @param loadServer
	 */
	void setParent(LoadServer loadServer);

	/**
	 * 获取父类对象
	 */
	LoadServer getParent();

	public String getConfig();

	public void setConfig(String config);

	/**
	 * 移除对象
	 */
	LoadProcess remove();

	AtomicInteger getFailedNum();

	void setFailedNum(AtomicInteger failedNum);

}
