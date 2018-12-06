package org.wlf.java_websocket.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.wlf.java_websocket.bean.Contact;

/**
 * Created by Administrator on 2016/8/21.
 */
public class Utils {

    public static String objectToJson( Object object){
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(object);
        return json;
    }



    public static String getProtocalType(String msg) throws IOException {
        return getStringValueFromJson(msg, "type");
    }
    
    public static String getProtocalUserId(String msg) throws IOException {
        return getStringValueFromJson(msg, "userId");
    }
    
    public static String getStringValueFromJson(String msg,String key) throws IOException{
    	System.out.println(msg);
    	JsonReader jsonReader = new JsonReader(new StringReader(msg));
        jsonReader.beginObject();

        while (jsonReader.hasNext()){
            String name = jsonReader.nextName();
            System.out.println(name);
            if(key.equals(name)){
                return jsonReader.nextString();
            }else {
				jsonReader.skipValue();
			}
        }
        jsonReader.endObject();
        jsonReader.close();
        return null;
    }

    public static <T> T jsonToObject(String json,Class<T> clazz){
        Gson gson = new GsonBuilder().serializeNulls().create();
        T object = gson.fromJson(json,clazz);
        return object;
    }

    public static void main(String[] args){
    	try {
			String ttt = getProtocalUserId("{\"type\":\"GetContactsResp\",\"userId\":\"lbh2\"}");
			System.out.println(ttt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}