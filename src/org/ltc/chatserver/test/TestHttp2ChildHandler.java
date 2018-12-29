package org.ltc.chatserver.test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;


public class TestHttp2ChildHandler extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;

    public TestHttp2ChildHandler(){
        try {
            sslContext = SSLUtils.getSSLContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(sslContext.newHandler(socketChannel.alloc()),new TestHttp2HandlerBuilder().build());
    }
}
