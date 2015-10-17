package org.ddpush.service.broadCast.exeutor.mysql;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.ddpush.im.util.JsonConvertor;
import org.ddpush.im.util.JsonCreator;
import org.ddpush.im.util.StringUtil;
import org.ddpush.im.v1.node.IMServer;
import org.ddpush.im.v1.node.NodeStatus;
import org.ddpush.im.v1.node.PushMessage;
import org.ddpush.im.v1.node.ServerMessage;
import org.ddpush.service.broadCast.BroadCast;
import org.ddpush.service.broadCast.Commander;
import org.ddpush.service.broadCast.QueryCommand;
import org.ddpush.service.broadCast.Queryer;
import org.ddpush.service.broadCast.dao.BroadCastListHandler;
import org.ddpush.service.broadCast.exeutor.BaseExecutor;
import org.ddpush.service.broadCast.exeutor.QueryWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.media.sound.InvalidDataException;

public class MysqlQueryer implements Runnable, BaseExecutor, Queryer {
	private final static Logger log = LoggerFactory
			.getLogger(MysqlQueryer.class);
	private PushMessage message;
	/**
	 * 查询解析器
	 */
	private static QueryWorker WORKER = new MysqlWorker();

	public MysqlQueryer(final PushMessage message) {
		this.message = message;
	}

	@Override
	public void run() {
		if (message != null) {
			List<BroadCast> broadCasts = null;
			try {
				final SocketAddress adress = NodeStatus.getInstance()
						.getInstance()
						.getClientStat(message.getUuidHexString())
						.getLastAddr();
				
				for (Object broadCast : WORKER.executorQuery((QueryCommand) JsonConvertor
						.toObject(LOCAL_GSON.get(),
								StringUtil.convert(message), QueryCommand.class))) {
					try {
						sendBroadCast(adress, (BroadCast) broadCast);
					} catch (Exception e) {
						log.error("单个查询错误", e);
					}
				}

			} catch (Exception e) {
				log.error("查询执行错误", e);
			} finally {
				if (broadCasts != null) {
					broadCasts.clear();
				}
			}
		}
		message = null;
	}


	private void sendBroadCast(final SocketAddress adress,
			final BroadCast broadcast) throws Exception {
		ServerMessage message = SERVER_MESSAGE_CREATOR.newServerMessage(adress,
				JsonCreator.toJsonWithGson(broadcast, BroadCast.class,
						LOCAL_GSON.get()), Commander.CMD_QUERY);
		IMServer.getInstance().pushInstanceMessage(message);

	}

}
