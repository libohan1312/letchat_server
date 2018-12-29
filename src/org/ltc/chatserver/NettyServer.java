package org.ltc.chatserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.ltc.chatserver.test.TestHttp2ChildHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyServer {
    Logger logInfo = Logger.getLogger(getClass().getSimpleName());
    Logger logError = Logger.getLogger(getClass().getSimpleName());
    public NettyServer(int port){
        logError.setLevel(Level.WARNING);
        this.port = port;
    }
    int port = 8081;
    private void startLoop()
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 100);
            b.group(group).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new TestHttp2ChildHandler());

            Channel ch = b.bind(port).sync().channel();
            logInfo.info(String.format("binding to %d done. server is running.", port));
            ch.closeFuture().sync();
        } catch (Exception e) {
            logError.info(String.format("start server error. %s", e.getMessage()));
        } finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args){
        new NettyServer(8443).startLoop();
    }
}
