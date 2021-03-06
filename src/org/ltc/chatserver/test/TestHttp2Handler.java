package org.ltc.chatserver.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class TestHttp2Handler extends Http2ConnectionHandler implements Http2FrameListener {

    private static ByteBuf getBF(String msg){
        return unreleasableBuffer(copiedBuffer(msg,CharsetUtil.UTF_8));
    }

//    private static Http2Headers http1HeadersToHttp2Headers(FullHttpRequest request) {
//        CharSequence host = request.headers().get(HttpHeaderNames.HOST);
//        Http2Headers http2Headers = new DefaultHttp2Headers()
//                .method(HttpMethod.GET.asciiName())
//                .path(request.uri())
//                .scheme(HttpScheme.HTTP.name());
//        if (host != null) {
//            http2Headers.authority(host);
//        }
//        return http2Headers;
//    }
//
//    /**
//     * Handles the cleartext HTTP upgrade event. If an upgrade occurred, sends a simple response via HTTP/2
//     * on stream 1 (the stream specifically reserved for cleartext HTTP upgrade).
//     */
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent) {
//            HttpServerUpgradeHandler.UpgradeEvent upgradeEvent =
//                    (HttpServerUpgradeHandler.UpgradeEvent) evt;
//            onHeadersRead(ctx, 1, http1HeadersToHttp2Headers(upgradeEvent.upgradeRequest()), 0 , true);
//        }
//        super.userEventTriggered(ctx, evt);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    private void sendResponse(ChannelHandlerContext ctx, int streamId, ByteBuf payload) {
        // Send a frame for the response status
        Http2Headers headers = new DefaultHttp2Headers().status(OK.codeAsText());
        encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());
        encoder().writeData(ctx, streamId, payload, 0, true, ctx.newPromise());

        // no need to call flush as channelReadComplete(...) will take care of it.
    }

    protected TestHttp2Handler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        super(decoder, encoder, initialSettings);
        System.out.println("thread create:"+Thread.currentThread().getName());
    }

    private void dispatchPost(final ChannelHandlerContext ctx, final int streamId, final Map<String,Object> params){
        String path = getUrlPath(this.path);
        String json = RouteManager.doCallHttp2(path,HttpMethod.POST,params);
        ByteBuf byteBuf1 = Unpooled.wrappedBuffer(json.getBytes());
        sendResponse(ctx, streamId, byteBuf1);
    }

    @Override
    public int onDataRead(ChannelHandlerContext channelHandlerContext, int i, ByteBuf byteBuf, int i1, boolean b) throws Http2Exception {
        System.out.println("thread read data:"+Thread.currentThread().getName());

        int processed = byteBuf.readableBytes() + i1;
        if (b) {
            System.out.println("test streamid : "+i +"  data:"+byteBuf.toString(CharsetUtil.UTF_8));

            byte[] bytes = new byte[byteBuf.capacity()];
            byteBuf.readBytes(bytes);
            String s = new String(bytes);
            Map<String,Object> params = getForm(s);
            dispatchPost(channelHandlerContext,i,params);
        }
        return processed;
    }

    private Map<String,Object> getForm(String data){
        Map<String,Object> params = new HashMap<>();
        String[] form = data.split("&");
        for (String s : form) {
            String[] kv = s.split("=");
            if(kv.length == 2){
                params.put(kv[0],kv[1]);
            }
        }
        return params;
    }

    private Map<String,Object> getUrlParams(String url){
        if (url == null) {
            return new HashMap<>();
        }
        String params = url.substring(url.indexOf("?")+1);
        return getForm(params);
    }

    private String getUrlPath(String url){
        if (url == null) {
            return null;
        }
        return url.substring(url.indexOf("/"),url.indexOf("?"));
    }

    private void dispatchGet(ChannelHandlerContext ctx, int id, String path){
        ByteBuf content = ctx.alloc().buffer();

        String data = RouteManager.doCallHttp2(getUrlPath(path),HttpMethod.GET,getUrlParams(path));
        content.writeBytes(getBF(data));
//        content.writeBytes(getBF("test").duplicate());
//        ByteBufUtil.writeAscii(content, " - via HTTP/2");
        sendResponse(ctx, id, content);
    }
    String path;
    @Override
    public void onHeadersRead(ChannelHandlerContext channelHandlerContext, int i, Http2Headers http2Headers, int i1, boolean b) throws Http2Exception {
        System.out.println("thread read head:"+Thread.currentThread().getName());
        System.out.println("test streamid : "+i +"  head:"+http2Headers.path());
        path = http2Headers.path().toString();
//        if (b) {
//            path = http2Headers.path().toString();
            if (http2Headers.method().toString().equals("GET")) {
                dispatchGet(channelHandlerContext,i,http2Headers.path().toString());
            }
//        }
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext channelHandlerContext, int i, Http2Headers http2Headers, int i1, short i2, boolean b, int i3, boolean b1) throws Http2Exception {
        onHeadersRead(channelHandlerContext,i,http2Headers,i1,b);
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext channelHandlerContext, int i, int i1, short i2, boolean b) throws Http2Exception {

    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext channelHandlerContext, int i, long l) throws Http2Exception {

    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext channelHandlerContext) throws Http2Exception {

    }

    @Override
    public void onSettingsRead(ChannelHandlerContext channelHandlerContext, Http2Settings http2Settings) throws Http2Exception {

    }

    @Override
    public void onPingRead(ChannelHandlerContext channelHandlerContext, long l) throws Http2Exception {

    }

    @Override
    public void onPingAckRead(ChannelHandlerContext channelHandlerContext, long l) throws Http2Exception {

    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext channelHandlerContext, int i, int i1, Http2Headers http2Headers, int i2) throws Http2Exception {

    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext channelHandlerContext, int i, long l, ByteBuf byteBuf) throws Http2Exception {

    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext channelHandlerContext, int i, int i1) throws Http2Exception {

    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext channelHandlerContext, byte b, int i, Http2Flags http2Flags, ByteBuf byteBuf) throws Http2Exception {

    }
}
