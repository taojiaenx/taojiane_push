package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.ddpush.im.v1.node.PushMessage;
import org.ddpush.im.v1.node.TaskTimeoutSolver;

public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {
	protected final NettyPushListener listener;

	public PushTaskHandler(final NettyPushListener listener) {
		this.listener = listener;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		byte[] byteData = new byte[msg.readableBytes()];
		final int readerIndex = msg.readerIndex();
		msg.getBytes(readerIndex, byteData);
		PushMessage pm = new PushMessage(byteData);
		byteData = null;
		final FutureTask<Integer> f = new PushTask(new ProcessDataCallable(
				ctx.channel(), pm));
		ctx.executor().schedule(new TaskTimeoutSolver(f),
				NettyPushListener.sockTimout, TimeUnit.MILLISECONDS);
		listener.execEvent(f);
	}

}
