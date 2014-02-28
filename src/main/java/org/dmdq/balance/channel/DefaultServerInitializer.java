package org.dmdq.balance.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;

public final class DefaultServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public final void initChannel(final SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		// Uncomment the following line if you want HTTPS
		// SSLEngine engine =
		// SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// p.addLast("ssl", new SslHandler(engine));
		p.addLast("codec", new HttpServerCodec());
		p.addLast("handler", new DefaultServerHandler());
		p.addLast("compressor", new HttpContentCompressor());
	}
}