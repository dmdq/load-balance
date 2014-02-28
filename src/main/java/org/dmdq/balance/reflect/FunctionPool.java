package org.dmdq.balance.reflect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dmdq.balance.dao.impl.RedisDaoImpl;
import org.dmdq.balance.service.impl.EchoServiceImpl;
import org.dmdq.balance.util.PackUtil;

public class FunctionPool {

	public final static void init() {
		try {
			regRequestPool(EchoServiceImpl.class.getPackage().getName());
			regRequestPool(RedisDaoImpl.class.getPackage().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 注册回调
	 * 
	 * @param impl
	 *            回调池接口实现类
	 * @return 无返回值
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private final static Object regRequestPool(final Class<?> impl) throws InstantiationException, IllegalAccessException {
		if (null != impl) {
			Object obj = null;
			int modifiers = impl.getModifiers();
			if (modifiers != 0 && modifiers != IClassModifiers._ABSTRACT && modifiers != IClassModifiers._INTERFACE && (obj = impl.newInstance()) instanceof Object) {
				return obj;
			} else {
				return null;
			}
		}
		return impl;
	}

	/**
	 * 注册回调
	 * 
	 * @param pack
	 *            回调包
	 * @return 无返回值
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public final static void regRequestPool(final String packageName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (null == packageName) {
			return;
		}
		List<String> classNames = PackUtil.getClassName(packageName, false);
		if (classNames == null) {
			return;
		}
		final Set<Object> calls_pool = new HashSet<Object>();
		for (String className : classNames) {
			className = className.replace("/", ".");
			int index = className.indexOf(packageName);
			className = className.substring(index);
			Class<?> clazz = Class.forName(className);
			Object object = regRequestPool(clazz);
			if (object != null) {
				calls_pool.add(object);
			}
		}
		for (Object impl : calls_pool) {
			Method[] methods = impl.getClass().getDeclaredMethods();
			for (Method method : methods) {
				// Class<?> rtnType = method.getReturnType();
				// if (rtnType == RPC.Builder.class) {
				RequestPool.setCallback(impl, method.getName());
				// }
			}
		}
	}
}
