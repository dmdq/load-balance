package org.dmdq.balance.model;

/**
 * ip 过滤器 用来显示访问的ip
 * 
 * @author lion
 */
public interface LoadIPFilter {

	/**
	 * 获取黑白名单对象
	 * 
	 * @return
	 */
	LoadForbidden getLoadForbidden();
}
