package org.ltc.chatserver.test;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RouteManager {
     static Map<RouteItem,NettyHttpApi> routeItemMap = new HashMap<>();
     static Map<RouteItem,NettyHttp2Api> routeItemMap2 = new HashMap<>();
    static {
        routeItemMap.put(new RouteItem("/login",HttpMethod.POST),new LoginApi());
        routeItemMap.put(new RouteItem("/regist",HttpMethod.POST),new RegistApi());
        routeItemMap.put(new RouteItem("/test/abc",HttpMethod.POST),new HttpTestApi());
        routeItemMap.put(new RouteItem("/test/abc",HttpMethod.GET),new HttpTestApi());
        routeItemMap2.put(new RouteItem("/test/abc",HttpMethod.GET),new Http2TestApi());
    }

    public static FullHttpResponse doCallHttp1(String path, HttpMethod method, FullHttpRequest request){
        return routeItemMap.get(new RouteItem(path,method)).call(request);
    }

    public static String doCallHttp2(String path, HttpMethod method, Map<String,Object> datas){
        return routeItemMap2.get(new RouteItem(path,method)).call(datas);
    }
}
