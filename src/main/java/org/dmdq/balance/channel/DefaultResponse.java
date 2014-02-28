package org.dmdq.balance.channel;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_ENCODING;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import java.util.Map;

import com.google.common.collect.Maps;

public final class DefaultResponse {

	private byte[] _content;

	private String _contentType;

	private Map<String, Object> _headers;

	public DefaultResponse() {
		_content = new byte[] {};
		_contentType = "text/plain";
		_headers = Maps.newHashMap();
	}

	public DefaultResponse(final String content) {
		_content = content.getBytes();
		_headers = Maps.newHashMap();
		_contentType = "text/plain";
	}

	public DefaultResponse(final byte[] bytes) {
		_content = bytes;
		_headers = Maps.newHashMap();
		_contentType = "text/plain";
	}

	public final byte[] getContent() {
		return null != _content ? _content : new byte[] {};
	}

	public final void setContent(final byte[] content) {
		_content = content;
	}

	public final void setContent(final String content) {
		_content = content.getBytes();
	}

	public final String getContentType() {
		return _contentType;
	}

	public final void setContentType(final String contentType) {
		_contentType = contentType;
	}

	public final Map<String, Object> getHeaders() {
		if (null == _content) {
			_content = new byte[] {};
		}
		if (null == _headers) {
			_headers = Maps.newHashMap();
		}
		_headers.put(CONTENT_TYPE, _contentType);
		_headers.put(CONTENT_LENGTH, _content.length);
		_headers.put(CONTENT_ENCODING, "gizp");
		return _headers;
	}

	public final void setHeaders(final Map<String, Object> headers) {
		_headers = headers;
	}

	public final void addHeader(final String key, final Object value) {
		_headers.put(key, value);
	}

}
