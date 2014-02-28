package org.dmdq.balance.service.impl;

import io.netty.channel.Channel;

import org.dmdq.balance.channel.DefaultResponse;
import org.dmdq.balance.service.EchoService;

public class EchoServiceImpl implements EchoService {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EchoServiceImpl.class);

	public final DefaultResponse echo(Channel channel, final String value) {
		LOGGER.debug(value);
		DefaultResponse defaultHttpRtnObject = new DefaultResponse();
		defaultHttpRtnObject.setContent(value.toUpperCase().getBytes());
		defaultHttpRtnObject.setContentType("text/plain");
		return defaultHttpRtnObject;
	}

}
