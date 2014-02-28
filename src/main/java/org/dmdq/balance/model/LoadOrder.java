package org.dmdq.balance.model;

/**
 * 排序
 */
public interface LoadOrder {
	/**
	 * 设置排序编号
	 * 
	 * @param order
	 *            排序编号、号越小越在前
	 */
	void setOrder(int order);

	/**
	 * 获取排序编号
	 */
	int getOrder();
	
}
