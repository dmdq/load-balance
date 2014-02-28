package org.dmdq.balance.model;

/**
 * 负载运算
 * 
 * @author lion
 */
public interface LoadCalc<T> {

	/**
	 * 获取 负载最小的 .
	 */
	T getItem();
}
