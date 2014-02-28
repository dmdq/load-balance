package org.dmdq.balance.model.domain;

import org.dmdq.balance.model.LoadOrder;

/**
 * 进程
 * 
 * @author lion
 * 
 */
public class LoadBalanceProcess implements LoadOrder{

	private String id;

	private int order;

	private String host;

	private int port;

	private int online;

	private int usedMemory;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public int getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(int usedMemory) {
		this.usedMemory = usedMemory;
	}

}
