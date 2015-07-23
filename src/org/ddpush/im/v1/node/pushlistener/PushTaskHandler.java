package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		msg.retain();
		final FutureTask<Integer> f = new PushTask(ctx, msg);
		ctx.executor().execute(f);
		ctx.executor().schedule(new TaskTimeoutSolver(ctx, f),
				NettyPushListener.sockTimout, TimeUnit.MILLISECONDS);
	}

}

class TaskTimeoutSolver implements Runnable {
	private final ChannelHandlerContext ctx;
	private final FutureTask<Integer> taskFuture;

	public TaskTimeoutSolver(final ChannelHandlerContext ctx,
			final FutureTask<Integer> taskFuture) {
		this.ctx = ctx;
		this.taskFuture = taskFuture;
	}

	private void solveTimeout() {
		if (!taskFuture.isDone()) {
			taskFuture.cancel(false);
			ctx.close();
		}
	}

	@Override
	public void run() {
		solveTimeout();
	}
}
