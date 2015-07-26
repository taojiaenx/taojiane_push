package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;


public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {
	protected final NettyPushListener listener;
	
	public PushTaskHandler(final NettyPushListener listener) {
		this.listener = listener;
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // do something
    }

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		msg.retain();
		final FutureTask<Integer> f = new PushTask(ctx, msg);
	    listener.solveTimeout(f);
		listener.execInqueue(f, ctx.channel());
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
		}
	}

	@Override
	public void run() {
		solveTimeout();
	}
}

