package org.ltc.chatserver.test;

import io.netty.handler.codec.http2.*;

import static io.netty.handler.logging.LogLevel.INFO;

public class TestHttp2HandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder {
    private static final Http2FrameLogger logger = new Http2FrameLogger(INFO, TestHttp2Handler.class);
    public TestHttp2HandlerBuilder(){
        frameLogger(logger);
    }

    @Override
    protected Http2ConnectionHandler build() {
        return super.build();
    }

    @Override
    protected Http2ConnectionHandler build(Http2ConnectionDecoder http2ConnectionDecoder, Http2ConnectionEncoder http2ConnectionEncoder, Http2Settings http2Settings) throws Exception {
        TestHttp2Handler testHttp2Handler = new TestHttp2Handler(http2ConnectionDecoder,http2ConnectionEncoder,http2Settings);
        frameListener(testHttp2Handler);
        return testHttp2Handler;
    }
}
