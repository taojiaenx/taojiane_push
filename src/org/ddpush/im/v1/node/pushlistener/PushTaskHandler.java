package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {
	protected final NettyPushListener listener;

	public PushTaskHandler(final NettyPushListener listener) {
		this.listener = listener;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (ctx.channel().isActive()) {
			ctx.disconnect();
			ctx.close();
		}
		// cause.printStackTrace();
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		msg.retain();
		final FutureTask<Integer> f = new PushTask(ctx, msg);
		ctx.executor().schedule(new TaskTimeoutSolver(f),NettyPushListener.sockTimout, TimeUnit.MILLISECONDS);
		listener.execEvent(f);
	}

}

class TaskTimeoutSolver implements Runnable {
	private final FutureTask<Integer> taskFuture;

	public TaskTimeoutSolver(final FutureTask<Integer> taskFuture) {
		this.taskFuture = taskFuture;
	}

	private void solveTimeout() {

		if (!taskFuture.isDone()) {
			taskFuture.cancel(false);
		}
	}

	@Override
	public void run() {
		solveTimeout();
	}
}
