package org.dmdq.balance.service;

import io.netty.channel.Channel;

import java.io.UnsupportedEncodingException;

import org.dmdq.balance.channel.DefaultResponse;

public interface FileService {

	/**
	 * 下载文件
	 * 
	 * @param path
	 *            待下载的文件绝对路径
	 */
	DefaultResponse download(Channel channel,String path);

	/**
	 * 列出目录下的文件
	 * 
	 * @param path
	 *            目录路径
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	DefaultResponse tree(Channel channel,String path) throws UnsupportedEncodingException;
}
