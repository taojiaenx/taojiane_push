/*
 *Copyright 2014 DDPush
 *Author: AndyKwok(in English) GuoZhengzhu(in Chinese)
 *Email: ddpush@126.com
 *

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package org.ddpush.im.v1.client.appuser.example;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.ddpush.im.util.JsonCreator;
import org.ddpush.im.util.StringUtil;
import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.UDPClientBase;
import org.ddpush.service.broadCast.BroadCast;

import com.google.gson.Gson;


public class MyUdpClient extends UDPClientBase {

	public MyUdpClient(byte[] uuid, int appid, String serverAddr, int serverPort)
			throws Exception {
		super(uuid, appid, serverAddr, serverPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasNetworkConnection() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onPushMessage(Message message) {
		if(message == null){
			System.out.println("msg is null");
		}
		if(message.getData() == null || message.getData().length == 0){
			System.out.println("msg has no data");
		}
		String str = null;
		try{
			str = new String(message.getData(),5,message.getContentLength(), "UTF-8");
		}catch(Exception e){
			str = StringUtil.convert(message.getData(),5,message.getContentLength());
		}
		System.out.println(StringUtil.convert(this.uuid)+"---"+str);

	}

	@Override
	public void trySystemSleep() {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args){
		try{
			final String ip = "127.0.0.1";
			final int port = 9966;
			final int pushPort = 9999;
			byte[] uuid = StringUtil.md5Byte("0");
			MyUdpClient myUdpClient = new MyUdpClient(uuid,3,ip,port);
			myUdpClient.setHeartbeatInterval(50);
			myUdpClient.start();
		//	TimeUnit.SECONDS.sleep(30);
			
			final byte[] clientUUID = StringUtil.md5Byte("0");
			Random random = new Random();
			ExecutorService pool = Executors.newFixedThreadPool(256);
			final BroadCast broadCast = new BroadCast();
		//	for(int j = 0; j < 600; ++j) {
          //   for(int i = 0; i < 5000; ++i) {
     			broadCast.setLat(new BigDecimal(random.nextDouble() * 180 - 90));
    			broadCast.setLon(new BigDecimal(random.nextDouble() * 360 - 180));
    			broadCast.setCreateDate(new Date(System.currentTimeMillis()));
    			broadCast.setBroadCastID(UUID.randomUUID().toString());
    			broadCast.setBody("尼玛炸了" + 1);
    			broadCast.setAuthorUUID(StringUtil.convert(myUdpClient.uuid, 0, 13));
					pool.execute(new send0x20Task(ip, pushPort, clientUUID,
							JsonCreator.toJsonWithGson(broadCast,
									BroadCast.class, new Gson()).getBytes(
									"utf-8")));
        //     }
		//	}

			synchronized(myUdpClient){
				myUdpClient.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
