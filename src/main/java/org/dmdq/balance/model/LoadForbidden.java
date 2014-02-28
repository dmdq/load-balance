package org.dmdq.balance.model;

import java.util.Set;

public interface LoadForbidden {

	/**
	 * 添加到白名单列表中
	 * 
	 * @param value
	 * 
	 */
	void addWhite(final String value);

	/**
	 * 添加到白名单区间中
	 * 
	 * @param value
	 */
	void addWhitesBetween(final String value);

	/**
	 * 添加到黑名单列表中
	 * 
	 * @param value
	 */
	void addBlack(final String value);

	/**
	 * 添加到黑名单区间中
	 * 
	 * @param value
	 */
	void addBlackBetween(final String value);

	/**
	 * 获取白名单
	 * 
	 * @return
	 */
	Set<String> getWhites();

	/**
	 * 设置白名单
	 * 
	 * @param whites
	 */
	void setWhites(final Set<String> whites);

	/**
	 * 获取白名单区间
	 * 
	 * @return
	 */
	Set<String> getWhitesBetween();

	/**
	 * 设置白名单区间
	 * 
	 * @param whitesBetween
	 */
	void setWhitesBetween(final Set<String> whitesBetween);

	/**
	 * 获取黑名单列表
	 * 
	 * @return
	 */
	Set<String> getBlacks();

	/**
	 * 设置黑名单列表
	 * 
	 * @param black
	 */
	void setBlacks(final Set<String> black);

	/**
	 * 获取黑名单区间
	 * 
	 * @return
	 */
	Set<String> getBlackBetween();

	/**
	 * 设置和名单区间
	 * 
	 * @param blackBetween
	 */
	void setBlackBetween(final Set<String> blackBetween);

	/**
	 * 验证IP地址是否在白名单中
	 * 
	 * @param ip
	 *            ip地址
	 */
	boolean isVaildWhite(final String ip);

	/**
	 * 验证IP地址是否在黑名单中
	 * 
	 * @param ip
	 *            ip地址
	 */
	boolean isVaildBlack(final String ip);

	/**
	 * 生成正则表达式
	 */
	String build();
}
