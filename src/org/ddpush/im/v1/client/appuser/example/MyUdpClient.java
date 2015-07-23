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

import org.ddpush.im.util.StringUtil;
import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.UDPClientBase;


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
		System.out.println(StringUtil.convert(this.uuid)+"---"+StringUtil.convert(message.getData()));

	}

	@Override
	public void trySystemSleep() {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args){
		try{
			final String ip = "10.0.0.65";
			final int port = 9966;
			final int pushPort = 9999;
			byte[] uuid = StringUtil.md5Byte("0");
			MyUdpClient myUdpClient = new MyUdpClient(uuid,3,ip,port);
			myUdpClient.setHeartbeatInterval(50);
			myUdpClient.start();
			
			final byte[] clientUUID = StringUtil.md5Byte("陶加恩");

				new Thread(new send0x20Task(ip,pushPort,clientUUID, ("test " + 1).getBytes("utf-8"))).start();

			synchronized(myUdpClient){
				myUdpClient.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
