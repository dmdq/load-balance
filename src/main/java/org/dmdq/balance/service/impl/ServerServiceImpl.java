package org.dmdq.balance.service.impl;

import io.netty.channel.Channel;

import java.net.BindException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.dmdq.balance.channel.DefaultResponse;
import org.dmdq.balance.channel.DefaultServerManager;
import org.dmdq.balance.conf.InfoCode;
import org.dmdq.balance.service.ServerService;
import org.dmdq.balance.util.SocketUtil;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ServerServiceImpl implements ServerService {

	private DefaultServerManager _defaultServerManager;

	public ServerServiceImpl() {
		_defaultServerManager = new DefaultServerManager();
	}

	public final boolean start(final int port) {
		boolean flag = false;
		try {
			_defaultServerManager.start(port);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	int getStartCode(int port) {
		int code = InfoCode.NONE;
		try {
			code = _defaultServerManager.start(port);
		} catch (BindException e) {
			code = InfoCode.PORT_IN_USE;
		} catch (InterruptedException e) {
			code = InfoCode.START_EXCEPTION;
		}
		return code;
	}

	String getMsgByCode(int code) {
		switch (code) {
		case InfoCode.NONE:
			return "失败";
		case InfoCode.PORT_IN_USE:
			return "端口占用";
		case InfoCode.START_EXCEPTION:
			return "启动异常";
		case InfoCode.SUCCESS:
			return "成功";
		case InfoCode.LEFT_ONE_PORT:
			return "至少保留一个端口";
		case InfoCode.NO_PORT:
			return "没有此端口";
		default:
			return "失败";
		}
	}

	public final DefaultResponse start(final String ports) {
		DefaultResponse defaultHttpRtnObject = null;
		String msg = "failed";
		try {
			List<Integer> _ports = Lists.transform(Lists.newArrayList(ports.split(";")), new Function<String, Integer>() {
				public Integer apply(String input) {
					return Integer.valueOf(input);
				}
			});
			StringBuffer info = new StringBuffer();
			for (int port : _ports) {
				info.append("启动端口:").append(port).append(getMsgByCode(getStartCode(port))).append("\n");
			}
			msg = info.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultHttpRtnObject = new DefaultResponse(msg);
		return defaultHttpRtnObject;
	}

	public final DefaultResponse stop(final String ports) {
		DefaultResponse defaultHttpRtnObject = null;
		String msg = "failed";
		if ("exit".equals(ports)) {
			System.exit(0);
			msg = "success";
		} else {

			List<Integer> _ports = Lists.transform(Lists.newArrayList(ports.split(";")), new Function<String, Integer>() {
				public Integer apply(String input) {
					return Integer.valueOf(input);
				}
			});
			StringBuffer info = new StringBuffer();
			for (int port : _ports) {
				info.append("停止端口:").append(port).append(getMsgByCode(_defaultServerManager.stop(port))).append("\n");
			}
			msg = info.toString();
		}
		defaultHttpRtnObject = new DefaultResponse(msg);
		return defaultHttpRtnObject;
	}

	public Map<String, Object> connect(Channel channel,final String host, final String port) {
		int code = InfoCode.SUCCESS;
		Map<String, Object> map = Maps.newHashMap();
		map.put("msg", SocketUtil.isStart(host, Integer.valueOf(port.trim())) ? "success" : "failed");
		map.put("code", code);
		return map;
	}
	
	public final DefaultResponse connect(final String jsonData) {
		DefaultResponse defaultHttpRtnObject = null;
		String msg = "failed";
		Map<?, ?> map = null;
		try {
			String host = "localhost";
			int port = 9999;
			map = new ObjectMapper().readValue(jsonData, Map.class);
			if (map.containsKey("host")) {
				host = (String) map.get("host");
			}
			if (map.containsKey("port")) {
				port = Integer.valueOf(map.get("port").toString());
			}
			msg = SocketUtil.isStart(host, port) ? "success" : "failed";
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultHttpRtnObject = new DefaultResponse(msg);
		return defaultHttpRtnObject;
	}

	public final boolean stop(int port) {
		boolean flag = false;
		try {
			_defaultServerManager.stop(port);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public final boolean restart() {
		return restart(9999);
	}

	public final boolean restart(int port) {
		return stop(port) && start(port);
	}

	public final DefaultResponse restart(String port) {
		final int _port = Integer.parseInt(port);
		boolean flag = stop(_port) && start(_port);
		String msg = flag ? "success" : "failed";
		DefaultResponse defaultHttpRtnObject = new DefaultResponse(msg);
		return defaultHttpRtnObject;
	}

}
