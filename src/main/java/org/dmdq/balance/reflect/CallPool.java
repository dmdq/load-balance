package org.dmdq.balance.reflect;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallPool {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallPool.class);

	protected final Map<String, Vector<Callback>> _list = new ConcurrentHashMap<String, Vector<Callback>>();

	public final void addCallback(final Object owner, final String method) {
		if (!_list.containsKey(method)) {
			_list.put(method, new Vector<Callback>());
		}
		_list.get(method).add(new Callback(owner, method));
	}

	public void execute(final String method, final Object... args) {
		Vector<Callback> calls = _list.get(method);
		if (null == calls) {
			LOGGER.warn("can'not find {} method  in list this err in {}  execute method!", method, CallPool.class);
			return;
		}
		for (Callback call : calls) {
			try {
				call.invoke(args);
			} catch (Exception e) {
				LOGGER.warn("method={}", method);
				e.printStackTrace();
			}
		}
	}
}
