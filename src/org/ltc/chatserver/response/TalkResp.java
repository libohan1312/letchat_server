package org.ltc.chatserver.response;

public class TalkResp extends BaseResponse {
	private String content;
	private String fromWho;
	
	public TalkResp(){
		setType(TYPE_TALK_RESP);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromWho() {
		return fromWho;
	}

	public void setFromWho(String fromWho) {
		this.fromWho = fromWho;
	}
	
	
}
