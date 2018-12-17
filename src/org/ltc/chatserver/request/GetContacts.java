package org.ltc.chatserver.request;

/**
 * Created by Administrator on 2016/8/21.
 */
public class GetContacts extends BaseRequest {
    public GetContacts(){
        setType(BaseRequest.TYPE_GETCONTACTS);
    }
}
