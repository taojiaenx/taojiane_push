package org.ddpush.im.util;

import java.net.SocketAddress;

import org.ddpush.im.v1.node.ServerMessage;

public class SeverMessageCreator {
	public ServerMessage newServerMessage(SocketAddress address, final byte[] content) throws Exception {
		return new ServerMessage(address, content);
		
	}
}
