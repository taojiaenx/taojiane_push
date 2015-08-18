package org.ddpush.im.v1.node.udpconnector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

import org.ddpush.im.util.PropertyUtil;
import org.ddpush.im.v1.node.ClientMessage;
import org.ddpush.im.v1.node.ServerMessage;
import org.ddpush.im.v1.node.pushlistener.NettyPushListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpConnector {
	private static Logger logger = LoggerFactory.getLogger(UdpConnector.class);
	
	protected Channel antenna = null;//天线
	
	protected Receiver receiver;
	protected Sender sender;
	EventLoopGroup group = new NioEventLoopGroup();
	Bootstrap b = null;
	
	
	boolean started = false;
	boolean stoped = false;
	
	protected int port = PropertyUtil.getPropertyInt("CLIENT_UDP_PORT");
	
	public void setPort(int port){
		this.port = port;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void init(){
	}
	
	public void start() throws Exception{
		if(antenna != null){
			throw new Exception("antenna is not null, may have run before");
		}
			
		logger.info("udp connector port");	
		this.receiver = new Receiver(antenna);
		this.sender = new Sender(antenna);
		this.sender.init();
		
		
		b = new Bootstrap();
	    b.group(group).channel(NioDatagramChannel.class).handler(receiver);
	    antenna = b.bind(port).channel();
		logger.info("udp connector port:{}", port);	
		
	}
	public void stop() throws Exception{
		receiver.stop();
		sender.stop();
		group.shutdownGracefully();
	}
	
	public long getInqueueIn(){
		return this.receiver.queueIn.longValue();
	}
	
	public long getInqueueOut(){
		return this.receiver.queueOut.longValue();
	}
	
	public long getOutqueueIn(){
		return this.sender.queueIn.longValue();
	}
	
	public long getOutqueueOut(){
		return this.sender.queueOut.longValue();
	}


	public ClientMessage receive() throws Exception {
		return receiver.receive();
	}


	public boolean send(ServerMessage message) throws Exception {
		return sender.send(message);
		
	}
	
}
