package org.ddpush.im.v1.node.udpconnector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.ddpush.im.v1.node.ClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Receiver extends SimpleChannelInboundHandler<DatagramPacket> {
	private static Logger logger = LoggerFactory.getLogger(Receiver.class);


	protected AtomicLong queueIn = new AtomicLong(0);
	protected AtomicLong queueOut = new AtomicLong(0);
	protected ConcurrentLinkedQueue<ClientMessage> mq = new ConcurrentLinkedQueue<ClientMessage>();


	public void stop() {
	}

	protected void processMessage(ChannelHandlerContext ctx, DatagramPacket msg)
			throws Exception {
		byte[] swap = null;
		swap = new byte[msg.content().readableBytes()];
		final int readerIndex = msg.content().readerIndex();
		msg.content().getBytes(readerIndex, swap);
		ClientMessage m = new ClientMessage(msg.sender(), swap);
		swap = null;
		enqueue(m);
	}

	protected boolean enqueue(ClientMessage message) {
		boolean result = mq.add(message);
		if (result == true) {
			queueIn.addAndGet(1);
		}
		return result;
	}

	protected ClientMessage dequeue() {
		ClientMessage m = mq.poll();
		if (m != null) {
			queueOut.addAndGet(1);
		}
		return m;
	}

	public ClientMessage receive() {

		ClientMessage m = null;
		while (true) {
			m = dequeue();
			if (m == null) {
				return null;
			}
			if (m.checkFormat() == true) {// 检查包格式是否合法，为了网络快速响应，在这里检查，不在接收线程检查
				return m;
			}
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
			throws Exception {
		processMessage(ctx, msg);

	}

}
