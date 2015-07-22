package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ddpush.im.util.PropertyUtil;

public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static int minThreads = PropertyUtil
			.getPropertyInt("PUSH_LISTENER_MIN_THREAD");
	private static int maxThreads = PropertyUtil
			.getPropertyInt("PUSH_LISTENER_MAX_THREAD");
	/**
	 * 会在关机的时候由NIOPushListener关闭
	 */
	static final ExecutorService executor = new ThreadPoolExecutor(minThreads,
			maxThreads, 30, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());


	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		try {
			final FutureTask<Integer> f = new PushTask(ctx, msg.array());
			executor.execute(f);
			ctx.executor().schedule(new TaskTimeoutSolver(ctx, f),
					NettyPushListener.sockTimout, TimeUnit.MILLISECONDS);
		} finally {
			msg.release();
		}
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
			taskFuture.cancel(true);
			ctx.close();
		}
	}

	@Override
	public void run() {
		solveTimeout();
	}
}
