package org.ddpush.im.v1.node.udpconnector;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import org.ddpush.im.util.PropertyUtil;
import org.ddpush.im.v1.node.ClientMessage;
import org.ddpush.im.v1.node.ServerMessage;
import org.ddpush.im.v1.node.pushlistener.NettyPushListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpConnector {
	private static Logger logger = LoggerFactory.getLogger(UdpConnector.class);
	
	protected DatagramChannel antenna;//天线
	
	protected Receiver receiver;
	protected Sender sender;
	
	protected Thread receiverThread;
	protected Thread senderThread;
	
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
		antenna = DatagramChannel.open();
		antenna.socket().bind(new InetSocketAddress(port));
		logger.info("udp connector port:{}", port);
		//non-blocking
		antenna.configureBlocking(false);
		antenna.socket().setReceiveBufferSize(1024*1024*PropertyUtil.getPropertyInt("CLIENT_UDP_BUFFER_RECEIVE"));
		antenna.socket().setSendBufferSize(1024*1024*PropertyUtil.getPropertyInt("CLIENT_UDP_BUFFER_SEND"));
		logger.info("udp connector recv buffer size:{}", antenna.socket().getReceiveBufferSize());
		logger.info("udp connector send buffer size:{}", antenna.socket().getSendBufferSize());
		
		
		this.receiver = new Receiver(antenna);
		this.receiver.init();
		this.sender = new Sender(antenna);
		this.sender.init();
		
		this.senderThread = new Thread(sender,"AsynUdpConnector-sender");
		this.receiverThread = new Thread(receiver,"AsynUdpConnector-receiver");
		this.receiverThread.start();
		this.senderThread.start();
	}
	public void stop() throws Exception{
		receiver.stop();
		sender.stop();
		try{
			receiverThread.join();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			senderThread.join();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{antenna.socket().close();}catch(Exception e){}
		try{antenna.close();}catch(Exception e){}
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
