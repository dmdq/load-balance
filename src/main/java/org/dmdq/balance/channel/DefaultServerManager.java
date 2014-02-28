package org.dmdq.balance.channel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.BindException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.dmdq.balance.conf.InfoCode;

import com.google.common.collect.Maps;

public final class DefaultServerManager {

	private ConcurrentMap<Integer, ServerRunning> serverMap;

	public DefaultServerManager() {
		serverMap = Maps.newConcurrentMap();
	}

	public final synchronized int start(final int port) throws InterruptedException, BindException {
		int code = InfoCode.NONE;
		if (serverMap.containsKey(port)) {
			code = InfoCode.PORT_IN_USE;
		} else {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			serverBootstrap.group(bossGroup, workerGroup);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.childHandler(new DefaultServerInitializer());
			Channel serverChannel = serverBootstrap.bind(port).syncUninterruptibly().channel();
			ServerRunning serverInfo = new ServerRunning(serverChannel, serverBootstrap);
			serverMap.put(port, serverInfo);
			code = InfoCode.SUCCESS;
		}
		return code;
	}

	public final synchronized int stop(int port) {
		int code = InfoCode.NONE;
		if (serverMap.containsKey(port)) {
			if (serverMap.size() == 1) {// 至少保留一个监听端口
				code = InfoCode.LEFT_ONE_PORT;
			} else {
				ServerRunning serverInfo = serverMap.get(port);
				serverInfo.serverChannel.close();
				serverInfo.serverBootstrap.group().shutdownGracefully();
				serverInfo.serverBootstrap.childGroup().shutdownGracefully();
				serverMap.remove(port);
				code = InfoCode.SUCCESS;
			}
		} else {
			if (port <= 0) {
				stopAll();
				code = InfoCode.SUCCESS;
			} else {
				code = InfoCode.NO_PORT;
			}
		}
		return code;
	}

	public final synchronized void stopAll() {
		for (Entry<Integer, ServerRunning> entry : serverMap.entrySet()) {
			int port = entry.getKey();
			stop(port);
		}
	}

	public static class ServerRunning {

		protected Channel serverChannel;

		protected ServerBootstrap serverBootstrap;

		public ServerRunning() {
		}

		public ServerRunning(Channel serverChannel, ServerBootstrap serverBootstrap) {
			this.serverChannel = serverChannel;
			this.serverBootstrap = serverBootstrap;
		}

		public Channel getServerChannel() {
			return serverChannel;
		}

		public void setServerChannel(Channel serverChannel) {
			this.serverChannel = serverChannel;
		}

		public ServerBootstrap getServerBootstrap() {
			return serverBootstrap;
		}

		public void setServerBootstrap(ServerBootstrap serverBootstrap) {
			this.serverBootstrap = serverBootstrap;
		}

	}

}
