package org.ddpush.im.v1.node.pushlistener;

import org.ddpush.im.v1.node.Constant;
import org.ddpush.im.v1.node.PushMessage;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class PushMessageDecoder extends LengthFieldBasedFrameDecoder{

	public PushMessageDecoder() {
		super(Constant.PUSH_MSG_MAX_CONTENT_LEN, PushMessage.DATA_LENGTH_OFFET, PushMessage.DATA_LENGTH_LENGTH);
	}

}
