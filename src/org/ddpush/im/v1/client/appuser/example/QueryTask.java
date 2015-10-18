package org.ddpush.im.v1.client.appuser.example;

import java.util.concurrent.TimeUnit;

import org.ddpush.im.v1.client.appserver.Pusher;

public class QueryTask extends send0x20Task{

	public QueryTask(String serverIp, int port, byte[] uuid, byte[] msg) {
		super(serverIp, port, uuid, msg);
	}
	@Override
	public void run(){
		Pusher pusher = null;
		try{
			boolean result;
			pusher = new Pusher(serverIp,port, 1000*5);
			result = pusher.pushQueryMessage(uuid,msg);
			 TimeUnit.MILLISECONDS.sleep(1);
		}catch(Exception e){
		}finally{
			if(pusher != null){
				try{pusher.close();}catch(Exception e){};
			}
		}
	}
}
