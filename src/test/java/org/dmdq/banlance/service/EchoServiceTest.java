package org.dmdq.banlance.service;

import org.dmdq.balance.service.EchoService;
import org.dmdq.balance.service.impl.EchoServiceImpl;
import org.junit.Test;

public class EchoServiceTest {

	private EchoService echoService;

	@Test
	public void test() {
		echoService = new EchoServiceImpl();
		echoService.echo(null,"hello world");
	}

}
