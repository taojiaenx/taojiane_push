package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * 写解析器
 * @author taojiaen
 *
 */
public class PushResponseHandler extends MessageToByteEncoder<Byte>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Byte msg, ByteBuf out)
			throws Exception {
		 out.writeByte(msg);
	}

}
