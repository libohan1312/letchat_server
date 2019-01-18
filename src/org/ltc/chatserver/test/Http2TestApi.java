package org.ltc.chatserver.test;

import com.google.gson.Gson;

import java.util.Map;

public class Http2TestApi implements NettyHttp2Api {
    @Override
    public String call(Map<String, Object> data) {
        return new Gson().toJson(data);
    }
}
