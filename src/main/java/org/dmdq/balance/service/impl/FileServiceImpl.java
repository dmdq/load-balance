package org.dmdq.balance.service.impl;

import io.netty.channel.Channel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.dmdq.balance.channel.DefaultResponse;
import org.dmdq.balance.service.FileService;

public final class FileServiceImpl implements FileService {

	public final DefaultResponse download(final Channel channel,final String path) {
		DefaultResponse defaultHttpRtnObject = new DefaultResponse();
		File file = new File(path);
		if (file.exists()) {
			try {
				InputStream fis = new BufferedInputStream(new FileInputStream(path));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();
				defaultHttpRtnObject.setContentType("application/octet-stream;charset=UTF-8");
				defaultHttpRtnObject.addHeader("Content-Disposition", "attachment; filename=" + new String(file.getName().getBytes("GBK"), "ISO8859-1"));
				defaultHttpRtnObject.setContent(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return defaultHttpRtnObject;
	}

	public final DefaultResponse tree(final Channel channel,final String path) throws UnsupportedEncodingException {
		// 获取系统编码
		String encoding = System.getProperty("file.encoding");
		DefaultResponse defaultHttpRtnObject = new DefaultResponse();
		// 将path路径转码为系统编码
		File dir = new File(new String(path.getBytes("UTF-8"), encoding));
		// System.getProperties().list(System.out);
		if (null != dir && dir.exists() && dir.isDirectory() && dir.canRead()) {
			StringBuilder sb = new StringBuilder();
			File parent = dir.getParentFile();
			if (null != parent && parent.exists() && parent.isDirectory()) {
				sb.append("<a href='/?method=tree&args=");
				sb.append(URLEncoder.encode(parent.getAbsolutePath(), "UTF-8"));
				sb.append("'>");
				sb.append("<b>");
				sb.append("返回上级目录 &nbsp;&nbsp;&nbsp;&nbsp;");
				String parentName = new String(parent.getName().getBytes(encoding), "UTF-8");
				sb.append(parentName);
				sb.append("/<b>");
				sb.append("</a>");
				sb.append("<br/>");
			}
			/**
			 * 查找可视文件列表
			 */
			File[] files = dir.listFiles(new FileFilter() {
				public boolean accept(File f) {
					return !f.isHidden();// 过滤隐藏文件
				}
			});
			if (null != files) {
				for (File file : files) {
					sb.append("\n");
					if (file.isDirectory()) {
						sb.append("<a href='/?method=tree&args=");
						sb.append(URLEncoder.encode(file.getAbsolutePath(), "UTF-8"));
						sb.append("'>");
						sb.append("<b>");
						String fileName = new String(file.getName().getBytes(encoding), "UTF-8");
						sb.append(fileName);
						sb.append("/<b>");
						sb.append("</a>");
					} else {
						sb.append("<a style='text-decoration:none;color:black' href='/?method=download&args=");
						sb.append(URLEncoder.encode(file.getAbsolutePath(), "UTF-8"));
						sb.append("'>");
						// fileName = new String(
						// fileName.getBytes("ISO8859-1"),
						// "UTF-8" ) ;
						String fileName = new String(file.getName().getBytes(encoding), "UTF-8");
						sb.append(fileName);
						sb.append("</a>");
					}
					// sb.append("&nbsp;&nbsp;");
					// sb.append(file.length());
					sb.append("<br/>");
				}
			}
			defaultHttpRtnObject.setContentType("text/html ;charset=utf-8");
			defaultHttpRtnObject.setContent(sb.toString());
		}
		return defaultHttpRtnObject;
	}
}
