package org.ddpush.im.v1.node.pushlistener;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ddpush.im.util.PropertyUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class PushTaskHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private int minThreads = PropertyUtil
			.getPropertyInt("PUSH_LISTENER_MIN_THREAD");
	private int maxThreads = PropertyUtil
			.getPropertyInt("PUSH_LISTENER_MAX_THREAD");
	HashMap<ChannelHandlerContext, PushTask> taskMap = new HashMap<>();
	private ExecutorService executor = new ThreadPoolExecutor(minThreads, maxThreads, 30,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private NIOPushListener listener;
	/**
	 * Creates a client-side handler.
	 */
	public PushTaskHandler() {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		addTask(ctx);
	}
	
	public void cancelTaskInLoop(final ChannelHandlerContext ctx) {
		listener.addEvent(new Runnable() {

			@Override
			public void run() {
				cancelTask(ctx);
			}
			
		});
	}



	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		removeTask(ctx);
		ctx.close();
	}
	
	@Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		removeTask(ctx);
       super.channelUnregistered(ctx);
    }
	 @Override
	 public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	        
	 }
	
	 protected void  removeTask(ChannelHandlerContext ctx) {
		taskMap.remove(ctx);
	}
	
	protected void cancelTask(final ChannelHandlerContext ctx) {
		ctx.close();
		removeTask(ctx);
	}
	
	protected void addTask(final ChannelHandlerContext ctx) {
		if (taskMap.get(ctx) == null) {
			taskMap.put(ctx, new PushTask(ctx));
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		PushTask task = taskMap.get(ctx);
		if (task.isWritePending()) return;
		executor.execute(task);
	}

}
