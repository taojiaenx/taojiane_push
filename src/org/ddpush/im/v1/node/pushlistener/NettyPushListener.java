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
package org.ddpush.im.v1.node.pushlistener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.ddpush.im.util.PropertyUtil;
import org.ddpush.im.util.StringUtil;

public class NettyPushListener implements Runnable {
	static int sockTimout = 1000 * PropertyUtil
			.getPropertyInt("PUSH_LISTENER_SOCKET_TIMEOUT");
	static int port = PropertyUtil.getPropertyInt("PUSH_LISTENER_PORT");
	static int BACK_LOG = PropertyUtil.getPropertyInt("BACK_LOG");
	
	private int minThreads = PropertyUtil.getPropertyInt("PUSH_LISTENER_MIN_THREAD");
	private int minTimeoutThread = PropertyUtil.getPropertyInt("MIN_TIMEOUT_ThREAD");

	ServerSocketChannel channel = null;
	ServerBootstrap serverBootstarp = null;
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null;

	protected ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(minTimeoutThread, new TimeOutThreadFactory());

	public void init() throws Exception {
		initChannel();

	}

	public void initChannel() throws Exception {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup(minThreads, new WorkerGroupThreadFactory());
		serverBootstarp = new ServerBootstrap().group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_TIMEOUT, sockTimout)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						ch.pipeline().addLast("PushMessageDecoder-" + ch.hashCode(), new PushMessageDecoder());
						ch.pipeline().addLast(
								"processPushTask-" + ch.hashCode(),
								new PushTaskHandler(NettyPushListener.this));
						ch.pipeline().addLast("WritTimeout-" + ch.hashCode(),
								new WriteTimeoutHandler(sockTimout));
					}
				});

		serverBootstarp.bind(port);

		System.out.println("Netty TCP Push Listener nio provider: "
				+ serverBootstarp.getClass().getCanonicalName());
	}

	@Override
	public void run() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void stop() {
		stopExecutor();
		closeSelector();
	}

	private void stopExecutor() {

	}

	private void closeSelector() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	
	public void solveTimeout(Runnable command) {
		timer.schedule(command,
				NettyPushListener.sockTimout, TimeUnit.MILLISECONDS);
	}

	public static void main(String[] args) {
		class test implements Runnable {
			AtomicInteger cnt;

			public test(AtomicInteger cnt) {
				this.cnt = cnt;
			}

			public void run() {
				try {
					Socket s = new Socket(
							"localhost",
							PropertyUtil
									.getPropertyInt("Constant.PUSH_LISTENER_PORT"));
					s.setSoTimeout(0);
					InputStream in = s.getInputStream();
					OutputStream out = s.getOutputStream();

					// for(int i = 600000; i< 700000; i++){
					// while(true){
					int key = cnt.addAndGet(1);
					if (key > 10000) {
						return;
					}
					out.write(1);
					out.write(1);
					out.write(16);
					out.write(StringUtil.md5Byte("" + key));
					out.write(0);
					out.write(0);
					out.flush();

					byte[] b = new byte[1];
					int read = in.read(b);
					System.out.println(b[0]);
					// }
					// while(true){
					// int read = in.read(b);
					// System.out.println(b[0]);
					// if(read < 0){
					// break;
					// }
					// }
					s.close();
					System.out.println("bye~~");
					// long time = din.readLong();
					// System.out.println("time:"+time);
					// din.readLong();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		Thread[] worker = new Thread[10000];
		AtomicInteger cnt = new AtomicInteger(-1);
		for (int i = 0; i < worker.length; i++) {
			Thread t = new Thread(new test(cnt));
			worker[i] = t;
		}
		for (int i = 0; i < worker.length; i++) {
			worker[i].start();
			try {
				Thread.sleep(2);
			} catch (Exception e) {
			}
		}

		for (int i = 0; i < worker.length; i++) {
			try {
				worker[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("done~~~~~~~~~~~~~");
	}
}
class TimeOutThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r,"TimeOutThread-"+ r.hashCode());
	}
	
}

class WorkerGroupThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r,"WorkerGroupThread-"+ r.hashCode());
	}
	
}