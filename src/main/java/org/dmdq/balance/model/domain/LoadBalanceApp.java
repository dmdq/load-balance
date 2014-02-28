package org.dmdq.balance.model.domain;

import org.dmdq.balance.model.LoadBalanceForbidden;
import org.dmdq.balance.model.LoadOrder;

public class LoadBalanceApp implements LoadOrder, LoadBalanceForbidden {
	/**
	 * id
	 */
	private String id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 序号
	 */
	private int order;

	/**
	 * 过滤类型
	 */
	private int forbiddenType;

	/**
	 * 过滤值
	 */
	private String forbiddenValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getForbiddenType() {
		return forbiddenType;
	}

	public void setForbiddenType(int forbiddenType) {
		this.forbiddenType = forbiddenType;
	}

	public String getForbiddenValue() {
		return forbiddenValue;
	}

	public void setForbiddenValue(String forbiddenValue) {
		this.forbiddenValue = forbiddenValue;
	}

	/**
	 * 校验ip是否可以正常加载数据
	 * 
	 * @param channel
	 * @param forbiddenType
	 * @param id
	 * @return
	 */
	public boolean isVaildIp(String ip) {
		boolean flag = true;
		if (forbiddenType == 0) {
			flag = true;
		} else if (1 == forbiddenType) {// 白名单模式
			String forbidden_item_value_array[] = forbiddenValue.split(",");
			BalanceForbidden balanceForbidden = new BalanceForbidden();
			for (String forbidden_item_value_config : forbidden_item_value_array) {
				if (forbidden_item_value_config.indexOf("-") != -1) {
					balanceForbidden.addWhitesBetween(forbidden_item_value_config);
				} else {
					balanceForbidden.addWhite(forbidden_item_value_config);
				}
			}
			flag = balanceForbidden.isVaildWhite(ip);
		} else if (2 == forbiddenType) {// 黑名单模式
			String forbidden_item_value_array[] = forbiddenValue.split(",");
			BalanceForbidden balanceForbidden = new BalanceForbidden();
			for (String forbidden_item_value_config : forbidden_item_value_array) {
				if (forbidden_item_value_config.indexOf("-") != -1) {
					balanceForbidden.addBlackBetween(forbidden_item_value_config);
				} else {
					balanceForbidden.addBlack(forbidden_item_value_config);
				}
			}
			flag = balanceForbidden.isVaildBlack(ip);
		}
		return flag;
	}

}
