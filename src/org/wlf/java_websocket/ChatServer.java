package org.wlf.java_websocket;
import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.wlf.java_websocket.bean.Contact;
import org.wlf.java_websocket.bean.Regist;
import org.wlf.java_websocket.request.BaseRequest;
import org.wlf.java_websocket.response.GetContactsResp;
import org.wlf.java_websocket.response.TalkResp;
import org.wlf.java_websocket.util.Utils;

import com.google.gson.Gson;

/**
 * @author wlf(Andy)
 * @datetime 2016-02-16 09:28 GMT+8
 * @email 411086563@qq.com
 */
public class ChatServer extends WebSocketServer {
	
	ConcurrentHashMap<String,WebSocket> chats;

	public ChatServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		chats = new ConcurrentHashMap<String,WebSocket>();
	}

	public ChatServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {

		String userId = handshake.getFieldValue("UserId");
		
		chats.put(userId, conn);
		
		conn.send("you are "+userId);

		System.out.println(conn.getRemoteSocketAddress().getAddress()
				.getHostAddress()
				+ " 进入房间 ！");
		
		System.out.println("当前人数："+chats.size()+"\n都有谁：");
		for(String name : chats.keySet()){
			System.out.println(name);
		}
		
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {

		Iterator<Entry<String, WebSocket>> it = chats.entrySet().iterator();
		
		System.out.println("realclose:"+conn.isClosed());
		
		while(it.hasNext()){
			WebSocket close = it.next().getValue();
			//System.out.println(close.getRemoteSocketAddress());
			if(close.isClosed()){
				System.out.println(conn.getRemoteSocketAddress().getAddress()
						.getHostAddress()
						+ " 离开房间 ！");
				it.remove();
			}else if(close.isConnecting()){
				System.out.println("is connecting");
			}else if(close.isOpen()){
				System.out.println("is opened");
//				close.send("fasfda");
			}else if(close.isClosing()){
				System.out.println("is closeing");
			}else if(close.isFlushAndClose()){
				System.out.println("is flush and closeing");
			}
		
		}
		
//		sendToAll(conn.getRemoteSocketAddress().getAddress().getHostAddress()
//				+ " 离开房间 ！");

		
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		
		Gson gson = new Gson();
		
//		String type = message.substring(0, 2);
//		if("01".equals(type)){
//			Regist regist = gson.fromJson(message.substring(2), Regist.class);
//			chats.put(regist.getUserId(), conn);
//			conn.send(regist.getUserId());
//		}else if("02".equals(type)){
//			
//		}
		
		try {
			String type2 = Utils.getProtocalType(message);
			String userId = Utils.getProtocalUserId(message);
			if(BaseRequest.TYPE_GETCONTACTS.equals(type2)){
				GetContactsResp resp = makeGetContactsResp(userId);
				String text = Utils.objectToJson(resp);
				conn.send(text);
			}else if(BaseRequest.TYPE_TALK.equals(type2)){
				String toHow = Utils.getStringValueFromJson(message, "toHow");
				String content = Utils.getStringValueFromJson(message, "content");
				TalkResp talkResp = new TalkResp();
				talkResp.setContent(content);
				talkResp.setFromWho(userId);
				
				WebSocket chat = chats.get(toHow);
				
				String text = Utils.objectToJson(talkResp);
				chat.send(text);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		conn.send(type);
//		conn.send("啊，你是"+conn.getRemoteSocketAddress().getAddress().getHostAddress()+"吧，你说"+message+"是吧，我收到了");
//		sendToAll("["
//				+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
//				+ "]" + message);
		System.out.println("["
				+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
				+ "]" + message);
	}
	

	@Override
	public void onError(WebSocket conn, Exception e) {
		e.printStackTrace();
		if (conn != null) {
			conn.close();
		}
	}

	// 发送给所有的聊天者
	private void sendToAll(String text) {
		Collection<WebSocket> conns = connections();
		synchronized (conns) {
			for (WebSocket client : conns) {
				client.send(text);
			}
		}
	}
	
	private GetContactsResp makeGetContactsResp(String userId){
		GetContactsResp resp = new GetContactsResp();
		resp.setUserId(userId);
		ArrayList<Contact> list = new ArrayList<Contact>();
		for(String userid : chats.keySet()){
			if(!userid.equals(userId)){
				Contact contact = new Contact();
				contact.setUserId(userid);
				list.add(contact);
			}
		}
		resp.setContactList(list);
		return resp;
	}

	// 测试
	public static void main(String[] args) throws InterruptedException,
			IOException {

		int port = 8889;

		ChatServer server = new ChatServer(port);
		server.start();

		System.out.println("房间已开启，等待客户端接入，端口号: " + server.getAddress());

		BufferedReader webSocketIn = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {
			String stringIn = webSocketIn.readLine();
			server.sendToAll(stringIn);
		}
	}
	
	
}
