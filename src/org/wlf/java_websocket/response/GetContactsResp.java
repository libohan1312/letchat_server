package org.wlf.java_websocket.response;

import java.util.List;

import org.wlf.java_websocket.bean.Contact;

/**
 * Created by Administrator on 2016/8/21.
 */
public class GetContactsResp extends BaseResponse {
    private List<Contact> contactList;

    public GetContactsResp(){
        setType(TYPE_GETCONTACTS_RESP);
    }
    
    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
