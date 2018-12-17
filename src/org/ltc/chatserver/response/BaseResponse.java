package org.ltc.chatserver.response;

/**
 * Created by Administrator on 2016/8/21.
 */
public class BaseResponse {
	
	public static final String TYPE_GETCONTACTS_RESP = "GetContactsResp";
	public static final String TYPE_TALK_RESP = "Talk";
	
	private String userId;
    private String type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
