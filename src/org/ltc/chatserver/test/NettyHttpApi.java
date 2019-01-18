package org.ltc.chatserver.test;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface NettyHttpApi {
    FullHttpResponse call(FullHttpRequest httpRequest);
}
