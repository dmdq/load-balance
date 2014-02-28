package org.dmdq.balance.reflect;

import io.netty.channel.Channel;

import java.lang.reflect.Method;

/**
 * Callback
 * 
 * @author linxiaokai.cn
 * @version 20120909
 * 
 */
public final class Callback {

	protected Object _owner;

	protected String _method;

	public Callback(Object owner, String method) {
		_owner = owner;
		_method = method;
	}

	public final Object getOwner() {
		return _owner;
	}

	protected final Class<?>[] getParameterTypes(final Object[] args) {
		if (args == null)
			return null;
		Class<?>[] parameterTypes = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			if (args[i] instanceof Integer) {
				parameterTypes[i] = Integer.TYPE;
			} else if (args[i] instanceof Byte) {
				parameterTypes[i] = Byte.TYPE;
			} else if (args[i] instanceof Short) {
				parameterTypes[i] = Short.TYPE;
			} else if (args[i] instanceof Float) {
				parameterTypes[i] = Float.TYPE;
			} else if (args[i] instanceof Double) {
				parameterTypes[i] = Double.TYPE;
			} else if (args[i] instanceof Character) {
				parameterTypes[i] = Character.TYPE;
			} else if (args[i] instanceof Long) {
				parameterTypes[i] = Long.TYPE;
			} else if (args[i] instanceof Boolean) {
				parameterTypes[i] = Boolean.TYPE;
			} else if (args[i] instanceof Channel) {
				parameterTypes[i] = Channel.class;
			} else {
				parameterTypes[i] = args[i].getClass();
			}
		}
		return parameterTypes;
	}

	public final Object invoke(Object[] args) throws Exception {
		Class<?>[] parameterTypes = getParameterTypes(args);
		Method method = _owner.getClass().getDeclaredMethod(_method, parameterTypes);
		return method.invoke(_owner, args);
	}

	public final void setOwner(Object owner) {
		_owner = owner;
	}
}
