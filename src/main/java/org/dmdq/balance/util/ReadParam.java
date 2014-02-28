package org.dmdq.balance.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 获取配置资源文件 [公共参数] 信息
 * 
 * @author linxiaokai.cn
 */
public class ReadParam {

	private Properties properties;

	public ReadParam(String filePath) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(filePath + "not exists");
		}
		properties = new Properties();
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				throw e;
			}
		}

	}

	public String getString(String key) throws NullPointerException {
		if (null == key || key.equals("") || key.equals("null")) {
			return "";
		}
		String result = "";
		try {
			result = properties.getProperty(key);
		} catch (Exception e) {
			throw new NullPointerException(e.getMessage());
		}
		return result;
	}

	public Properties getProperties() {
		return properties;
	}
}