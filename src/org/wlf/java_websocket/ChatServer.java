package org.wlf.java_websocket;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.wlf.java_websocket.bean.Contact;
import org.wlf.java_websocket.request.BaseRequest;
import org.wlf.java_websocket.response.GetContactsResp;
import org.wlf.java_websocket.response.TalkResp;
import org.wlf.java_websocket.util.Utils;

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
		Iterator<Entry<String, WebSocket>> iterator = chats.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String,WebSocket> entry = iterator.next();
			if(entry.getKey().equals(userId)) continue;
			WebSocket webSocket = entry.getValue();
			System.out.println("hi i got "+entry.getKey());
			GetContactsResp resp = makeGetContactsResp(entry.getKey());
			String text = Utils.objectToJson(resp);
			webSocket.send(text);
			System.out.println("i send "+text+" to him");
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

		try {
			String type2 = Utils.getProtocalType(message);
			String userId = Utils.getProtocalUserId(message);
			if(BaseRequest.TYPE_GETCONTACTS.equals(type2)){
				GetContactsResp resp = makeGetContactsResp(userId);
				String text = Utils.objectToJson(resp);
				System.out.println(userId +" want get context, i give him "+text);
				conn.send(text);
			}else if(BaseRequest.TYPE_TALK.equals(type2)){
				String toWho = Utils.getStringValueFromJson(message, "toWho");
				if(toWho == null) {
					System.out.println("to who is null");
					return;
				}
				WebSocket chat = chats.get(toWho);
				if(chat == null){
					System.out.println("sorry there is no "+toWho +" who you want to chat!");
					return;
				}
				String content = Utils.getStringValueFromJson(message, "content");
				TalkResp talkResp = new TalkResp();
				talkResp.setContent(content);
				talkResp.setFromWho(userId);
				

				String text = Utils.objectToJson(talkResp);
				chat.send(text);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("on message "+ message);
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
			Contact contact = new Contact();
			contact.setUserId(userid);
			list.add(contact);
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
