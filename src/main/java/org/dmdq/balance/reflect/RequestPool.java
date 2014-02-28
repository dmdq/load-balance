package org.dmdq.balance.reflect;

import io.netty.channel.Channel;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.dmdq.balance.channel.DefaultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public final class RequestPool {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestPool.class);

	private static final Map<String, Callback> CALL_LIST = new HashMap<String, Callback>();

	public final static void setCallback(final Object owner, final String methodName) {
		try {
			CALL_LIST.put(methodName, new Callback(owner, methodName));
			BeanFactory.registerBean(owner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static Object execute(final String method, final Object[] args) throws Exception {
		if (null == method || "null".equalsIgnoreCase(method)) {
			throw new NoSuchMethodException("null exception");
		}
		Callback call = CALL_LIST.get(method);
		if (call == null) {
			throw new NoSuchMethodException("found not method=" + method);
		}
		Object object = call.invoke(null == args ? new Object[] {} : args);
		return object;
	}

	public final static DefaultResponse invokeHTTP(Channel channel, String uri) {
		if (null == uri || uri.length() == 0) {
			return new DefaultResponse();
		}
		try {
			uri = java.net.URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("exception:{}", e);
		}
		String fileName = "index";
		int parameIndex = uri.indexOf("?");
		int fileIndex = uri.indexOf("/");
		int uriLen = uri.length();
		if (fileIndex == -1) {
			fileName = "index.html";
		} else {
			if ("/".equals(uri) || uri.startsWith("/?")) {
				fileName = "index.html";
			} else {
				fileName = uri.substring(fileIndex, parameIndex == -1 ? uri.length() : parameIndex);
			}
		}
		if (parameIndex == -1) {
		} else {
			fileName = uri.substring(1, parameIndex < 0 ? uri.length() : parameIndex);
		}
		String parames[] = null;
		if (parameIndex == -1 && fileIndex == -1) {
			parames = uri.split("&");
		} else {
			if (parameIndex == -1) {
				parames = new String[] {};
			} else {
				parames = uri.substring(uriLen > 2 ? parameIndex + 1 : parameIndex).split("&");
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("");
		sb.append(fileName);
		Map<String, String> _parames = Maps.newHashMap();
		for (String parame : parames) {
			sb.append("");
			sb.append(parame);
			String val[] = parame.split("=");
			int len = val.length;
			if (len > 1) {
				_parames.put(val[0], val[1]);
			} else {
				_parames.put(val[0], "");
			}
		}
		sb.delete(0, sb.length());
		String rtn = sb.toString();
		DefaultResponse defaultHttpRtnObject = null;

		File file = new File(System.getProperty("config.dir") + "/html/" + fileName);

		if (fileName.length() > 0 && file.exists()) {
			if (!file.isDirectory()) {
				try {
					byte[] bytes = Files.toByteArray(file);
					defaultHttpRtnObject = new DefaultResponse(bytes);
					if (fileName.endsWith(".html") || fileName.indexOf(".") == -1) {
						defaultHttpRtnObject.setContentType("text/html");
					} else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg"))
						defaultHttpRtnObject.setContentType("image/jpeg");
					else if (fileName.endsWith("gif"))
						defaultHttpRtnObject.setContentType("image/gif");
					else if (fileName.endsWith("zip"))
						defaultHttpRtnObject.setContentType("application/zip");
					else if (fileName.endsWith("pdf"))
						defaultHttpRtnObject.setContentType("application/pdf");
					else if (fileName.endsWith("doc"))
						defaultHttpRtnObject.setContentType("application/msword");
					else if (fileName.endsWith("xls"))
						defaultHttpRtnObject.setContentType("application/vnd.ms-excel");
					else if (fileName.endsWith("ppt"))
						defaultHttpRtnObject.setContentType("application/vnd.ms-powerpoint");
					else if (fileName.endsWith("json"))
						defaultHttpRtnObject.setContentType("text/json");
					else if (fileName.endsWith("css"))
						defaultHttpRtnObject.setContentType("text/css");
					else if (fileName.endsWith("js"))
						defaultHttpRtnObject.setContentType("text/javascript");
					else
						defaultHttpRtnObject.setContentType("application/octet-stream");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// TODO
			}
		} else {
			String method = _parames.get("method");
			String args = _parames.get("args");
			String args2 = _parames.get("args2");
			String args3 = _parames.get("args3");
			String args4 = _parames.get("args4");
			String args5 = _parames.get("args5");
			String args6 = _parames.get("args6");
			String args7 = _parames.get("args7");
			String args8 = _parames.get("args8");
			String args9 = _parames.get("args9");
			String args10 = _parames.get("args10");
			if (null == method) {
				defaultHttpRtnObject = new DefaultResponse(rtn);
				defaultHttpRtnObject.setContentType("text/html");
				return defaultHttpRtnObject;
			}
			try {
				Object[] exeArgs = null;
				if (null != args10) {
					exeArgs = new Object[] { args, args2, args3, args4, args5, args6, args7, args8, args9, args10 };
				} else if (null != args9) {
					exeArgs = new Object[] { args, args2, args3, args4, args5, args6, args7, args8, args9 };
				} else if (null != args8) {
					exeArgs = new Object[] { args, args2, args3, args4, args5, args6, args7, args8 };
				} else if (null != args7) {
					exeArgs = new Object[] { args, args2, args3, args4, args5, args6, args7 };
				} else if (null != args6) {
					exeArgs = new Object[] { args, args2, args3, args4, args5, args6 };
				} else if (null != args5) {
					exeArgs = new Object[] { args, args2, args3, args4, args5 };
				} else if (null != args4) {
					exeArgs = new Object[] { args, args2, args3, args4 };
				} else if (null != args3) {
					exeArgs = new Object[] { args, args2, args3 };
				} else if (null != args2) {
					exeArgs = new Object[] { args, args2 };
				} else if (null != args) {
					exeArgs = new Object[] { args };
				}
				List<Object> args_list = Lists.newArrayList();
				args_list.add(channel);
				if (args != null) {
					for (Object a : exeArgs) {
						args_list.add(a);
					}
				}
				Object res = RequestPool.execute(method, args_list.toArray());
				if (res instanceof DefaultResponse) {
					defaultHttpRtnObject = (DefaultResponse) res;
				} else if (res instanceof java.util.Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> request = (Map<String, Object>) res;
					String contentType = (String) request.get("contentType");
					contentType = null == contentType ? "text/html" : contentType;
					request.remove("contentType");
					defaultHttpRtnObject = new DefaultResponse(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
					defaultHttpRtnObject.setContentType(contentType);
				} else {
					defaultHttpRtnObject = new DefaultResponse(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(res));
					defaultHttpRtnObject.setContentType("text/plain ;charset=utf8");
				}
			} catch (NoSuchMethodException e) {
				defaultHttpRtnObject = new DefaultResponse(rtn);
				defaultHttpRtnObject.setContentType("text/html ");
				LOGGER.warn("found not method={} args={}", method, args);
			} catch (NullPointerException e) {
				defaultHttpRtnObject = new DefaultResponse(rtn);
				defaultHttpRtnObject.setContentType("text/html");
				LOGGER.warn("null method exception");
			} catch (Exception e) {
				defaultHttpRtnObject = new DefaultResponse(rtn);
				defaultHttpRtnObject.setContentType("text/html");
				LOGGER.error("invoke method={} args={}", method, args);
				LOGGER.error("", e);
			}
		}
		return defaultHttpRtnObject;
	}

}
