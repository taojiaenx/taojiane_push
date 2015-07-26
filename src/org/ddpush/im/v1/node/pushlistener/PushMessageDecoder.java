package org.ddpush.im.v1.node.pushlistener;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import org.ddpush.im.v1.node.Constant;
import org.ddpush.im.v1.node.PushMessage;

public class PushMessageDecoder extends LengthFieldBasedFrameDecoder{

	public PushMessageDecoder() {
		super(Constant.PUSH_MSG_MAX_CONTENT_LEN, PushMessage.DATA_LENGTH_OFFET, PushMessage.DATA_LENGTH_LENGTH);
	}

}
