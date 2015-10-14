package org.ddpush.service.broadCast;

import org.ddpush.im.v1.node.IMServer;
import org.ddpush.service.Service;

public class BroadCastService implements Service{
	private BroadCastService() {
		
	}

	@Override
	public void install() {
		IMServer.getInstance().addPushMessageListener(Commander.getInstance());
	}

	@Override
	public void uninstall() {
		IMServer.getInstance().removePushMessageListener(Commander.getInstance());
	}

}
