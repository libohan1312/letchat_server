package org.ltc.chatserver.account;

public interface IAccount {
    void createAccount(String userName, String password);
    void login(String userName,String password);
    String getUserInfo(String userName);
}