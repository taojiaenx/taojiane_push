package org.ddpush.im.v1.node.udpconnector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.ddpush.im.util.PropertyUtil;
import org.ddpush.im.v1.node.Constant;
import org.ddpush.im.v1.node.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sender {
	private static Logger logger = LoggerFactory.getLogger(Sender.class);
	protected Channel channel;
	protected AtomicLong queueIn = new AtomicLong(0);
	protected AtomicLong queueOut = new AtomicLong(0);

	protected boolean stoped = false;

	public Sender(Channel antenna) {
		this.channel = antenna;
	}

	public void init() {
	}

	public void stop() {
		this.stoped = true;
	}

	protected void processMessage(final ServerMessage pendingMessage)
			throws Exception {
			channel.writeAndFlush(pendingMessage);

	}

	protected boolean enqueue(ServerMessage message) {
		boolean result = false;
		queueIn.addAndGet(1);
		try {
			processMessage(message);
			queueOut.addAndGet(1);
			result = true;
		} catch (Exception e) {
			logger.error("向发送客户端发送推送消息错误", e);
		}
		return result;
	}

	public boolean send(ServerMessage message) {
		return enqueue(message);
	}
}