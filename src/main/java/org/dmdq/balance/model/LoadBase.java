package org.dmdq.balance.model;

public interface LoadBase extends LoadOrder {

	/**
	 * 设置 id
	 * 
	 * @param id
	 */
	void setId(String id);

	/**
	 * 获取id
	 * 
	 * @return id
	 */
	String getId();

	/**
	 * 设置名称
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	String getName();

}
