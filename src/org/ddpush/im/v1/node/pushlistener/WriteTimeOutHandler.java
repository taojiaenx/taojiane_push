package org.ddpush.im.v1.node.pushlistener;

import java.util.HashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;

public class WriteTimeOutHandler extends ChannelOutboundHandlerAdapter{
	/**
	 * 用户读状态表
	 */
	private HashMap<Channel, Boolean> writeStatus = new HashMap<>();
	@Override
    public void read(ChannelHandlerContext ctx) throws Exception {
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    }
    public  boolean isWritePending(final Channel channel) {
		return writeStatus.get(channel) == true;
	}
}
