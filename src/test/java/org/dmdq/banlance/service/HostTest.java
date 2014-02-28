package org.dmdq.banlance.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class HostTest {

	@Test
	public void test() throws UnknownHostException {
		long time = 0;
		// 得到本机名
		InetAddress address1 = InetAddress.getLocalHost();
		System.out.println("本机名： " + address1.getHostName());
		// 直接返回域名
		InetAddress address2 = InetAddress.getByName("www.oracle.com");
		time = System.currentTimeMillis();
		System.out.print("直接得到域名： " + address2.getHostName());
		System.out.println("  所用时间：" + String.valueOf(System.currentTimeMillis() - time) + " 毫秒");
		// 通过DNS查找域名
		String host = "141.146.8.66";
		InetAddress address3 = InetAddress.getByName(host);
		System.out.println("address3:  " + address3); // 域名为空
		time = System.currentTimeMillis();
		System.out.print("通过DNS查找域名： " + address3.getHostName());
		System.out.println("  所用时间：" + String.valueOf(System.currentTimeMillis() - time) + " 毫秒");
		System.out.println("address3:  " + address3); // 同时输出域名和IP地址
	}

}
