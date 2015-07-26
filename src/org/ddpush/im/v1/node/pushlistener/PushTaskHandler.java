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
	    listener.solveTimeout(new TaskTimeoutSolver(f));
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

