package org.ddpush.service.broadCast.exeutor.mysql;

import java.net.SocketAddress;
import java.util.List;

import org.ddpush.im.util.JsonConvertor;
import org.ddpush.im.util.JsonCreator;
import org.ddpush.im.util.StringUtil;
import org.ddpush.im.v1.node.ClientStatMachine;
import org.ddpush.im.v1.node.IMServer;
import org.ddpush.im.v1.node.NodeStatus;
import org.ddpush.im.v1.node.PushMessage;
import org.ddpush.im.v1.node.ServerMessage;
import org.ddpush.service.broadCast.BroadCast;
import org.ddpush.service.broadCast.CommandResponse;
import org.ddpush.service.broadCast.Commander;
import org.ddpush.service.broadCast.QueryCommand;
import org.ddpush.service.broadCast.Queryer;
import org.ddpush.service.broadCast.exeutor.BaseExecutor;
import org.ddpush.service.broadCast.exeutor.QueryWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			List<Object> broadCasts = null;
			int res = 0;
			try {
				final QueryCommand command = (QueryCommand) JsonConvertor
						.toObject(LOCAL_GSON.get(),
								StringUtil.convert(message), QueryCommand.class);
				final SocketAddress adress = NodeStatus.getInstance()
						.getInstance()
						.getClientStat(message.getUuidHexString())
						.getLastAddr();
				try {
					broadCasts = WORKER.executorQuery(command);
				} catch (Exception e) {
					res = -1;
					log.error("查询执行错误x", e);
				}
					// 发送回应包
			   final int count = broadCasts == null ? 0 : broadCasts
							.size();
			    sendResponse(message.getUuidHexString(),
							command.getPackageID(), res, count);

				for (Object broadCast : broadCasts) {
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

	private void sendResponse(final String fromUUIDHex, final String packageID,
			final int res, final int count) throws Exception {
		CommandResponse reponse = COMMAND_RESPONSE.get();
		reponse.setRes(res);
		reponse.setPacketID(packageID);
		reponse.setBroadCastCount(count);

		ClientStatMachine csm = NodeStatus.getInstance().getInstance()
				.getClientStat(fromUUIDHex);
		ServerMessage message = SERVER_MESSAGE_CREATOR.newServerMessage(csm
				.getLastAddr(), JsonCreator.toJsonWithGson(reponse,
				CommandResponse.class, LOCAL_GSON.get()).getBytes("utf-8"), Commander.CMD_STORE);
		IMServer.getInstance().pushInstanceMessage(message);

	}

	private void sendBroadCast(final SocketAddress adress,
			final BroadCast broadcast) throws Exception {
		ServerMessage message = SERVER_MESSAGE_CREATOR.newServerMessage(adress,
				JsonCreator.toJsonWithGson(broadcast, BroadCast.class,
						LOCAL_GSON.get()).getBytes("utf-8"), Commander.CMD_QUERY);
		IMServer.getInstance().pushInstanceMessage(message);

	}

}
