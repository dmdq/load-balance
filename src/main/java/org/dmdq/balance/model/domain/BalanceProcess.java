package org.dmdq.balance.model.domain;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.dmdq.balance.model.LoadProcess;
import org.dmdq.balance.model.LoadServer;
import org.xml.sax.Attributes;

import com.google.common.collect.Maps;

/**
 * 子服务器数据
 * 
 * @author lion
 */
public final class BalanceProcess implements LoadProcess {

	private static final long serialVersionUID = 1L;
	/** 服务器所在组 Server ID */
	private String sid;
	/** 服务器进程id */
	private String id;
	/** 服务器IP地址 */
	private String host;
	/** 服务器端口 */
	private int port;
	/** 服务器在线人数 */
	private int online;
	/** 服务器使用内存 */
	private int usedMemory;
	/** 服务器状态 */
	private int status;
	/** 父类对象 */
	private LoadServer parent;
	/** 进程序号 */
	private int order;
	/** 失败的重连次数 */
	private AtomicInteger failedNum;

	private String config;

	public BalanceProcess() {
		sid = "1";
		host = "localhost";
		port = 8888;
		config = "http://hy.yxpai.com/resources/json/version.json";
		failedNum = new AtomicInteger();
	}

	public BalanceProcess(final String sid) {
		this();
		this.sid = sid;
	}

	@Override
	public final String getSid() {
		return sid;
	}

	@Override
	public final void setSid(final String sid) {
		this.sid = sid;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final void setId(final String id) {
		this.id = id;
	}

	@Override
	public final String getHost() {
		return host;
	}

	@Override
	public final void setHost(final String host) {
		this.host = host;
	}

	@Override
	public final int getPort() {
		return port;
	}

	@Override
	public final void setPort(final int port) {
		this.port = port;
	}

	@Override
	public final int getOnline() {
		return online;
	}

	@Override
	public final void setOnline(final int online) {
		this.online = online;
	}

	@Override
	public final int getUsedMemory() {
		return usedMemory;
	}

	@Override
	public final void setUsedMemory(final int usedMemory) {
		this.usedMemory = usedMemory;
	}

	@Override
	public final int getStatus() {
		return status;
	}

	@Override
	public final void setStatus(final int status) {
		this.status = status;
	}

	@JsonIgnore
	@Override
	public final Map<String, Object> toMap() {
		Map<String, Object> map = toConfig();
		map.put("id", id);
		map.put("online", online);
		map.put("usedMemory", usedMemory / 1024 / 1024);
		return map;
	}

	@JsonIgnore
	@Override
	public final Map<String, Object> toConfig() {
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("group", sid);
		map.put("host", host);
		map.put("port", port);
		map.put("config", null == config ? "http://hy.yxpai.com/resources/json/version.json": config);
		return map;
	}

	@Override
	public final void parseXML(final Attributes attributes) {
		String qName;
		String value;
		for (int i = 0; i < attributes.getLength(); i++) {
			qName = attributes.getQName(i);
			value = attributes.getValue(i);
			if ("id".equalsIgnoreCase(qName)) {
				id = value;
			} else if ("host".equalsIgnoreCase(qName)) {
				host = value;
			} else if ("port".equalsIgnoreCase(qName)) {
				port = Integer.parseInt(value);
			} else if ("order".equalsIgnoreCase(qName)) {
				order = Integer.valueOf(value);
			}
		}
	}

	@Override
	public final String toString() {
		return toMap().toString();
	}

	@Override
	public final void setParent(final LoadServer parent) {
		this.parent = parent;
	}

	@JsonIgnore
	@Override
	public final LoadServer getParent() {
		return parent;
	}

	@Override
	public final void setOrder(final int order) {
		this.order = order;
	}

	@Override
	public final int getOrder() {
		return order;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public AtomicInteger getFailedNum() {
		return failedNum;
	}

	public void setFailedNum(AtomicInteger failedNum) {
		this.failedNum = failedNum;
	}

	@Override
	public LoadProcess remove() {
		LoadServer parent = this.getParent();
		return null == parent ? null : parent.remove(this.id);
	}
}
