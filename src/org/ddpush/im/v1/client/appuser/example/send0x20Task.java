package org.ddpush.im.v1.client.appuser.example;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import org.ddpush.im.v1.client.appserver.Pusher;

public class send0x20Task implements Runnable{
	protected String serverIp;
	protected int port;
	protected byte[] uuid;
	protected byte[] msg;
	
	public send0x20Task(String serverIp, int port, byte[] uuid, byte[] msg){
		this.serverIp = serverIp;
		this.port = port;
		this.uuid = uuid;
		this.msg = msg;
	}
	
	public static String md5(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(encryptStr.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer md5 = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
			md5.append(Character.forDigit((digest[i] & 0xF), 16));
		}

		encryptStr = md5.toString();
		return encryptStr;
	}
	
	public void run(){
		Pusher pusher = null;
		try{
			boolean result;
			pusher = new Pusher(serverIp,port, 1000*5);
			result = pusher.push0x20Message(uuid,msg);
			 TimeUnit.MILLISECONDS.sleep(1);
		}catch(Exception e){
		}finally{
			if(pusher != null){
				try{pusher.close();}catch(Exception e){};
			}
		}
	}
}
