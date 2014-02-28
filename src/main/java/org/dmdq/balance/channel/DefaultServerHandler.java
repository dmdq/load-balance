package org.dmdq.balance.channel;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_ENCODING;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

import org.dmdq.balance.reflect.RequestPool;

public final class DefaultServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public final void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public final void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			if (is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
			boolean keepAlive = isKeepAlive(req);
			DefaultResponse rtn = null;
			String uri = req.getUri();
			HttpMethod httpMethod = req.getMethod();
			if (httpMethod == HttpMethod.POST) {
				return;
			} else if (httpMethod == HttpMethod.GET) {
				rtn = RequestPool.invokeHTTP(ctx.channel(),uri);
			}
			if (null == rtn) {
				return;
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(rtn.getContent()));
			Map<String, Object> heders = rtn.getHeaders();
			for (Map.Entry<String, Object> entry : heders.entrySet()) {
				String name = entry.getKey();
				Object value = entry.getValue();
				response.headers().set(name, value);
			}
			if (!keepAlive) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				ctx.write(response);
			}
		} else if (msg instanceof DefaultLastHttpContent) {
			DefaultLastHttpContent cont = (DefaultLastHttpContent) msg;
			ByteBuf content = cont.content();
			byte[] bytes = new byte[content.readableBytes()];
			content.readBytes(bytes);
			String value = new String(bytes);
			DefaultResponse rtn = RequestPool.invokeHTTP(ctx.channel(),value);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(rtn.getContent()));
			response.headers().set(CONTENT_TYPE, rtn.getContentType());
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			response.headers().set(CONTENT_ENCODING, "gizp");
			ctx.write(response);
		}
	}

	@Override
	public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}