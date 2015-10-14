package org.ddpush.service.broadCast.exeutor.mysql;

import org.ddpush.im.util.JsonConvertor;
import org.ddpush.im.v1.node.PushMessage;
import org.ddpush.service.broadCast.BroadCast;
import org.ddpush.service.broadCast.exeutor.JsonExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mysql广播存储器
 * @author taojiaen
 *
 */
public class MysqlStorer implements Runnable, JsonExecutor{
	private final static Logger log = LoggerFactory.getLogger(MysqlStorer.class);
	private  PushMessage message;
	public MysqlStorer(final PushMessage message) {
		this.message = message;
	}

	@Override
	public void run() {
		if (message != null) {
			try {
				storeMessage((BroadCast) JsonConvertor.toObject(LOCAL_GSON.get(), "", BroadCast.class));
			} catch (Exception e) {
				log.error("广播消息存储失败", e);
			}
		}
		message = null;
	}
	
	private void storeMessage(final BroadCast broadcast) {
		
	}

}
