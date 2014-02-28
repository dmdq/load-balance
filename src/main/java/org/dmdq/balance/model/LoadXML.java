package org.dmdq.balance.model;

import org.xml.sax.Attributes;

public interface LoadXML extends java.io.Serializable {

	/**
	 * 解析XML节点
	 * 
	 * @param attributes
	 */
	void parseXML(final Attributes attributes);
}
