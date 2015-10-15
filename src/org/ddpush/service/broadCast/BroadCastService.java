package org.ddpush.service.broadCast;

import org.ddpush.im.v1.node.IMServer;
import org.ddpush.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadCastService implements Service{
	private static Logger log = LoggerFactory.getLogger(BroadCastService.class);

	@Override
	public void install() {
		IMServer.getInstance().addPushMessageListener(Commander.getInstance());
		log.info("插件导入成功");
	}

	@Override
	public void uninstall() {
		IMServer.getInstance().removePushMessageListener(Commander.getInstance());
		Commander.getInstance().shutDowm();
		log.info("插件卸载成功");
	}

}
