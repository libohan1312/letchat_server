package org.ltc.chatserver.test;

import java.util.Map;

public interface NettyHttp2Api {
    String call(Map<String, Object> data);
}
