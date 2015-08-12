package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {
	protected final NettyPushListener listener;

	public PushTaskHandler(final NettyPushListener listener) {
		this.listener = listener;
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
	private static Logger logger = LoggerFactory.getLogger(TaskTimeoutSolver.class);
	private final FutureTask<Integer> taskFuture;

	public TaskTimeoutSolver(final FutureTask<Integer> taskFuture) {
		this.taskFuture = taskFuture;
	}

	private void solveTimeout() {
		if (!taskFuture.isDone()) {
			logger.warn("超时任务！");
			taskFuture.cancel(false);
		}
	}

	@Override
	public void run() {
		solveTimeout();
	}
}
