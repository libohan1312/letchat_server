package org.ltc.chatserver.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class TestHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final FullHttpRequest fullHttpRequest) throws Exception {

        new Thread(){
            @Override
            public void run() {
                String uri = fullHttpRequest.uri();
                System.out.println("url : "+fullHttpRequest.uri());
                String path = uri.substring(0,uri.indexOf("?"));
                System.out.println("url:"+fullHttpRequest.uri() +"  path:"+path);
                FullHttpResponse response = RouteManager.doCallHttp1(path,fullHttpRequest.method(),fullHttpRequest);
                channelHandlerContext.writeAndFlush(response);
            }
        }.start();

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
