package org.ddpush.im.v1.node.udpconnector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import org.ddpush.im.v1.node.ServerMessage;

public class UDPWriteHandler extends ChannelOutboundHandlerAdapter {
	/**
	 * 内存池
	 */
	private static PooledByteBufAllocator allocator = new PooledByteBufAllocator(
			true);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if (!(msg instanceof ServerMessage)) {
			ctx.write(msg);
		}

		final ServerMessage message = (ServerMessage) msg;
		ByteBuf messageBuf = allocator.ioBuffer(message.getData().length);
		messageBuf.writeBytes(message.getData());
		ctx.write(new DatagramPacket(messageBuf, (InetSocketAddress) message
				.getSocketAddress()));
	}
}
