package org.ltc.chatserver.account;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;

public class AccountHandler extends RequestStringHandler implements IAccount{
    @Override
    public void onRequest(ChannelHandlerContext ctx, String requestBody) {
//        JsonObject jsonObject =
    }

    @Override
    public void createAccount(String userName, String password) {

    }

    @Override
    public void login(String userName, String password) {

    }

    @Override
    public String getUserInfo(String userName) {
        return null;
    }
}
