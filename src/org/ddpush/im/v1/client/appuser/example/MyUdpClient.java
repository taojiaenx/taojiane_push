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

import org.ddpush.im.util.JsonCreator;
import org.ddpush.im.util.StringUtil;
import org.ddpush.im.util.json.DateTimeDeserializer;
import org.ddpush.im.util.json.DateTimeSerializer;
import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.UDPClientBase;
import org.ddpush.service.broadCast.BroadCast;
import org.ddpush.service.broadCast.QueryCommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		if (message == null) {
			System.out.println("msg is null");
		}
		if (message.getData() == null || message.getData().length == 0) {
			System.out.println("msg has no data");
		}
		String str = null;
		try {
			str = new String(message.getData(), 5, message.getContentLength(),
					"UTF-8");
		} catch (Exception e) {
			str = StringUtil.convert(message.getData(), 5,
					message.getContentLength());
		}
		System.out.println(StringUtil.convert(this.uuid) + "---" + str);

	}

	@Override
	public void trySystemSleep() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		try {
			System.out.println(System.getProperty("file.encoding"));
			final String ip = "121.42.153.240";
			final int port = 9966;
			final int pushPort = 9999;
			byte[] uuid = StringUtil.md5Byte("0");
			MyUdpClient[] myUdpClients = new MyUdpClient[32];
			for (int i = 0; i < myUdpClients.length; ++i) {
				myUdpClients[i] = new MyUdpClient(StringUtil.md5Byte(String.valueOf(i + System.currentTimeMillis())), 3, ip, port);	
				myUdpClients[i].setHeartbeatInterval(50);
				myUdpClients[i].start();
			}
		  //  TimeUnit.SECONDS.sleep(30);

			Random random = new Random();
			ExecutorService pool = Executors.newFixedThreadPool(64);
			for (int j = 0; j < 6000; ++j) {
				for (int i = 0; i < 500; ++i) {
					if ((random.nextInt() & 31) == 0) {
					final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTimeSerializer()).
							registerTypeAdapter(Date.class, new DateTimeDeserializer()).create();
						BroadCast broadCast = new BroadCast();
						broadCast.setLat(new BigDecimal(
								random.nextDouble() + 50));
						broadCast.setLon(new BigDecimal(
								random.nextDouble() + 121));
						broadCast.setBroadCastID(UUID.randomUUID().toString());
						broadCast.setBody("尼玛炸了" + i);
						broadCast.setAuthorUUID(StringUtil.convert(
								myUdpClients[i % 32].uuid, 0, 13));
						pool.execute(new send0x20Task(ip, pushPort, myUdpClients[i % 5].uuid,
								JsonCreator.toJsonWithGson(broadCast,
										BroadCast.class, gson).getBytes(
										"utf-8")));
					} else {
						QueryCommand command = new QueryCommand();
						command.setPackageID(UUID.randomUUID().toString());
						command.setLat(random.nextDouble() + 50);
						command.setLon(random.nextDouble() + 121);
						command.setDistance(100);
						command.setPageIndx(0);
						pool.execute(new QueryTask(ip, pushPort, myUdpClients[i % 32].uuid,
								JsonCreator.toJsonWithGson(command,
										QueryCommand.class, new Gson())
										.getBytes("utf-8")));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
