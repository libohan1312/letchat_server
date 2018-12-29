package org.ltc.chatserver.chat;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.ltc.chatserver.database.LetDBUtils;
import org.ltc.chatserver.request.TalkRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatHandler {
    public static int saveChat(TalkRequest talkRequest) throws SQLException {
        String sql = "insert into chat(content,towho,time) values(?,?,?)";
        return LetDBUtils.getRunner().update(sql,talkRequest.content,talkRequest.toWho,System.currentTimeMillis());
    }

    public static int[] saveChatBatch(TalkRequest[] talkRequests) throws SQLException {
        String sql = "insert into chat(content,towho,time) values(?,?,?)";
        Object[][] params = new Object[talkRequests.length][];
        for (int i = 0; i < talkRequests.length; i++) {
            params[i] = new Object[]{talkRequests[i].content,talkRequests[i].toWho,System.currentTimeMillis()};
        }
        return LetDBUtils.getRunner().batch(sql,params);
    }

    public static List<TalkRequest> getChatByToWho(String toWho) throws SQLException {
        String sql = "select * from chat where towho=?";
        List<ChatBean> arrayList = LetDBUtils.getRunner().query(sql, new BeanListHandler<ChatBean>(ChatBean.class),toWho);
        if(arrayList != null && arrayList.size()>0){
            List<TalkRequest> talkRequests = new ArrayList<TalkRequest>();
            for (ChatBean chatBean : arrayList) {
                TalkRequest talkRequest = new TalkRequest();
                talkRequest.toWho = chatBean.getToWho();
                talkRequest.content = chatBean.getContent();
                talkRequests.add(talkRequest);
            }
            return talkRequests;
        }
        return null;
    }
}
