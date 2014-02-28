package org.dmdq.balance.util;

import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.dmdq.balance.model.LoadForbidden;
import org.dmdq.balance.model.domain.BalanceForbidden;
import org.dmdq.balance.reflect.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BeanUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

	@SuppressWarnings("unchecked")
	public final static <T> T getBean(Class<T> clazz) {
		return (T) BeanFactory.get(clazz);
	}


	public static final LoadForbidden LOAD_FORBIDDEN = new BalanceForbidden();

	public static final ObjectMapper MAPPER = new ObjectMapper();

	public static final String getUUid() {
		return UUID.randomUUID().toString();
	}

	public static String writeValueAsString(Object object) {
		String value = null;
		try {
			value = MAPPER.writeValueAsString(object);
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return value;
	}

	public static <T> T readValue(String content, Class<T> valueType) {
		T t = null;
		try {
			t = MAPPER.readValue(content, valueType);
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return t;
	}

}
