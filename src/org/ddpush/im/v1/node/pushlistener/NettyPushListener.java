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
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.nio.channels.ServerSocketChannel;

import org.ddpush.im.util.PropertyUtil;
import org.ddpush.im.util.ThreadFactoryWithName;
import org.ddpush.im.v1.node.DDPushMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyPushListener implements Runnable {
	private static Logger logger = LoggerFactory
			.getLogger(NettyPushListener.class);
	static int sockTimout = 1000 * PropertyUtil
			.getPropertyInt("PUSH_LISTENER_SOCKET_TIMEOUT");
	static int sockTimeoutSeconds = PropertyUtil
			.getPropertyInt("PUSH_LISTENER_SOCKET_TIMEOUT");
	static int port = PropertyUtil.getPropertyInt("PUSH_LISTENER_PORT");
	static int BACK_LOG = PropertyUtil.getPropertyInt("BACK_LOG");
	static int QUEUE_MASK = (1 << PropertyUtil.getPropertyInt("QUEUE_MASK")) - 1;

	private int pushListenerWorkerNum = PropertyUtil
			.getPropertyInt("PUSH_LISTENER_WORKER_THREAD");
	private ChannelInitializer<SocketChannel> pushListenerChannelInitializer = new PushListenerChannelInitializer();
	// private int queue_num = QUEUE_MASK + 1;

	ServerSocketChannel channel = null;
	ServerBootstrap serverBootstarp = null;
	EventLoopGroup bossGroup = null;
	EventLoopGroup workerGroup = null;
	PushMessageWorkerExector pushMessageWorkerExector = null;

	public void init() throws Exception {
		initExecutor();
		initChannel();
	}

	public void initChannel() throws Exception {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup(pushListenerWorkerNum,
				new ThreadFactoryWithName(NettyPushListener.class));
		serverBootstarp = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_TIMEOUT, sockTimout)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR,
						PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childHandler(pushListenerChannelInitializer);

		serverBootstarp.bind(port).sync();

		logger.info("Netty TCP Push Listener nio provider: {} with {} workers", serverBootstarp
				.getClass().getCanonicalName(), pushListenerWorkerNum);
	}

	@Override
	public void run() {
		try {
			init();
		} catch (Exception e) {
			logger.error("pushListener 初始化错误");
			System.exit(1);
		}
	}

	public void stop() {
		stopExecutor();
		closeSelector();
	}

	/**
	 * 处理器
	 */
	public void initExecutor() {
		pushMessageWorkerExector = new PushMessageWorkerExector();
	}

	public void execEvent(final Runnable command) {
		pushMessageWorkerExector.execute(command);
	}

	private void stopExecutor() {
		try {
			if (pushMessageWorkerExector != null)
				pushMessageWorkerExector.shutdownNow();// ignore left overs
		} catch (Exception e) {
			e.printStackTrace();
		}
		pushMessageWorkerExector = null;
	}

	private void closeSelector() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	/**
	 * channe初始化
	 * 
	 * @author taojiaen
	 *
	 */
	private class PushListenerChannelInitializer extends
			ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast("PushMessageDecoder-" + ch.hashCode(),
					new DDPushMessageDecoder());
			ch.pipeline().addLast("processPushTask-" + ch.hashCode(),
					new PushTaskHandler(
							NettyPushListener.this));
			ch.pipeline().addLast("WritTimeout-" + ch.hashCode(),
					new WriteTimeoutHandler(sockTimeoutSeconds));
			ch.pipeline().addLast(new PushResponseHandler());

		}

	}

}
