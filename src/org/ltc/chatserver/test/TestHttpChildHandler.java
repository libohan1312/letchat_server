package org.ltc.chatserver.test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class TestHttpChildHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast("decoder",new HttpRequestDecoder())
                .addLast("encoder",new HttpResponseEncoder())
                .addLast("aggregator",new HttpObjectAggregator(512*1024))
                .addLast("handler", new TestHttpHandler());
    }
}
