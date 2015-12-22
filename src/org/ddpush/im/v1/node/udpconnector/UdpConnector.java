package org.ddpush.im.v1.node.udpconnector;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;

import org.ddpush.im.util.PropertyUtil;
import org.ddpush.im.util.ThreadFactoryWithName;
import org.ddpush.im.v1.node.ClientMessage;
import org.ddpush.im.v1.node.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpConnector {
	private static Logger logger = LoggerFactory.getLogger(UdpConnector.class);
	private int udpConnectorNum = PropertyUtil
			.getPropertyInt("UDP_CONNECTOR_NUM");

	protected Channel antenna = null;// 天线

	protected Receiver receiver;
	protected Sender sender;
	EventLoopGroup group = new EpollEventLoopGroup(udpConnectorNum,
			new ThreadFactoryWithName(UdpConnector.class));
	Bootstrap b = null;

	boolean started = false;
	boolean stoped = false;

	protected int port = PropertyUtil.getPropertyInt("CLIENT_UDP_PORT");

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return this.port;
	}

	public void init() {
	}

	public void start() throws Exception {
		if (antenna != null) {
			throw new Exception("antenna is not null, may have run before");
		}

		logger.info("udp connector port");
		this.receiver = new Receiver();

		b = new Bootstrap();
		b.group(group)
				.channel(EpollDatagramChannel.class)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.handler(new ChannelInitializer<EpollDatagramChannel>() {

					@Override
					protected void initChannel(EpollDatagramChannel ch)
							throws Exception {
						ch.pipeline().addLast(
								receiver.getClass().getName() + " "
										+ receiver.hashCode(), receiver);
						ch.pipeline().addLast(
								UDPWriteHandler.class.getName() + " "
										+ System.currentTimeMillis(),
								new UDPWriteHandler());
					}
				});
		ChannelFuture f = b.bind(port);
		antenna = f.channel();
		this.sender = new Sender(antenna);
		this.sender.init();
		f.sync();
		logger.info("udp connector port:{}", port);

	}

	public void stop() throws Exception {
		receiver.stop();
		sender.stop();
		group.shutdownGracefully();
	}

	public long getInqueueIn() {
		return this.receiver.queueIn.longValue();
	}

	public long getInqueueOut() {
		return this.receiver.queueOut.longValue();
	}

	public long getOutqueueIn() {
		return this.sender.queueIn.longValue();
	}

	public long getOutqueueOut() {
		return this.sender.queueOut.longValue();
	}

	public ClientMessage receive() throws Exception {
		return receiver.receive();
	}

	public boolean send(ServerMessage message) throws Exception {
		return sender.send(message);

	}

}
