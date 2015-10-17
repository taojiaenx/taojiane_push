package org.ddpush.service.broadCast;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.ddpush.im.util.SeverMessageCreator;
import org.ddpush.im.v1.node.Constant;
import org.ddpush.im.v1.node.ServerMessage;

public class BroadCastMessageCreator extends SeverMessageCreator{
	
	public ServerMessage newServerMessage(SocketAddress address, String content, final int type) throws Exception {
		byte[] data = new byte[Constant.SERVER_MESSAGE_MIN_LENGTH+content.length()];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.put((byte)1);//version
		bb.put((byte)0);//app id, 0 here
		bb.put((byte)type);//cmd
		bb.putShort((short)(content.length()));
		bb.put(content.getBytes());
		bb.flip();
		return super.newServerMessage(address, data);
	}
}
