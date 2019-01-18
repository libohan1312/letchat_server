package org.ltc.chatserver.test;

import io.netty.handler.codec.http.HttpMethod;

public class RouteItem {
    public String path;
    public HttpMethod httpMethod;

    public RouteItem(String path, HttpMethod httpMethod) {
        this.path = path;
        this.httpMethod = httpMethod;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RouteItem)){
            return false;
        }
        RouteItem item = (RouteItem) obj;

        return item.path.equals(path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
