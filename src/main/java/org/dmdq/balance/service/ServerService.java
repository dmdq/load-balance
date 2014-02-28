package org.dmdq.balance.service;

import org.dmdq.balance.channel.DefaultResponse;

public interface ServerService {

	/**
	 * 启动服务器
	 * 
	 * @param port
	 *            HTTP 端口
	 */
	boolean start(int port);

	/**
	 * 启动服务器
	 * 
	 * @param port
	 *            HTTP 端口
	 */
	DefaultResponse start(String port);

	/**
	 * 停止服务器
	 */
	boolean stop(int port);

	/**
	 * 重启服务器
	 * 
	 * @param port
	 *            HTTP 端口
	 */
	boolean restart(int port);
}
