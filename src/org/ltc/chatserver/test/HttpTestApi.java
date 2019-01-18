package org.ltc.chatserver.test;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.ltc.chatserver.util.Utils;

import java.io.IOException;
import java.util.Map;

public class HttpTestApi implements NettyHttpApi {
    @Override
    public FullHttpResponse call(FullHttpRequest httpRequest) {
        try {
            Map<String,Object> params = Utils.getRequestParams(httpRequest);
            Gson gson = new Gson();
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.wrappedBuffer(gson.toJson(params).getBytes()));
            HttpHeaders heads = response.headers();
            heads.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; charset=UTF-8");
            heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
            heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
