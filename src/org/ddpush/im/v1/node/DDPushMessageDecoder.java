package org.ddpush.im.v1.node;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class DDPushMessageDecoder extends LengthFieldBasedFrameDecoder{

	public DDPushMessageDecoder() {
		super(Constant.PUSH_MSG_MAX_CONTENT_LEN, PushMessage.DATA_LENGTH_OFFET, PushMessage.DATA_LENGTH_LENGTH);
	}

}
