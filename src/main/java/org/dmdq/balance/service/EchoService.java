package org.dmdq.balance.service;

import io.netty.channel.Channel;

import org.dmdq.balance.channel.DefaultResponse;

public interface EchoService {

	/**
	 * 转换大写回显输出
	 * 
	 * @param value
	 * 
	 */
	DefaultResponse echo(Channel channel,String value);
}
