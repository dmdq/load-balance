package org.dmdq.balance.reflect;

import java.util.HashMap;
import java.util.Map;

/**
 * 类工厂
 */
public final class BeanFactory {

	private static final Map<Object, Object> BEANS = new HashMap<Object, Object>();

	/**
	 * 注册Bean对象
	 * 
	 * @param object
	 */
	public final static void registerBean(final Object object) {
		Class<?> clazz = object.getClass();
		BEANS.put(clazz, object);
		BEANS.put(clazz.getName(), object);
		Class<?>[] faces = object.getClass().getInterfaces();
		for (Class<?> c : faces) {
			BEANS.put(c.getName(), object);
			BEANS.put(c, object);
		}
	}

	public final static int size() {
		return BEANS.size();
	}

	public final static boolean exists(final Object key) {
		return BEANS.containsKey(key);
	}

	public final static Object get(final Object key) {
		return BEANS.get(key);
	}
}
