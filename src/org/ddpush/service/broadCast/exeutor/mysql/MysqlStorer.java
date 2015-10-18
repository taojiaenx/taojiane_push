package org.ddpush.service.broadCast.exeutor.mysql;

import io.netty.util.concurrent.FastThreadLocal;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

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
import org.ddpush.service.broadCast.Storer;
import org.ddpush.service.broadCast.exeutor.BaseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddpush.dao.DbHelper;
import com.google.gson.Gson;
import com.sun.media.sound.InvalidDataException;

/**
 * mysql广播存储器
 * 
 * @author taojiaen
 *
 */
public class MysqlStorer implements Runnable, BaseExecutor, Storer {
	private final static Logger log = LoggerFactory
			.getLogger(MysqlStorer.class);
	private final static String __INSERT_SQL = "insert into broadcast_storer(`broadcast_id`,`author_uuid`,`broadcast_body`,`lat`,`lon`,`lat_floor`,`lon_floor`,`ipv4`) values(?,?,?,?,?,?,?,?)";

	private PushMessage message;

	public MysqlStorer(final PushMessage message) {
		this.message = message;
	}

	@Override
	public void run() {
		if (message != null) {
			BroadCast broadCast = null;
			int res = 0;
			try {
				broadCast = (BroadCast) JsonConvertor.toObject(
						LOCAL_GSON.get(),
						StringUtil.convert(message), BroadCast.class);
				if (broadCast == null) {
					throw new InvalidDataException();
				}
				storeMessage(broadCast,
						message.getIpv4());
			} catch (Exception e) {
				res = -1;
				log.error("广播消息存储失败", e);
			}
			
			try {
				sendResponse(message.getUuidHexString(), broadCast.getBroadCastID(), res);
			} catch (Exception e) {
				log.error("回应消息发送失败");
			}
		}
		message = null;
	}
	
	private void sendResponse(final String fromUUIDHex, final String broadCastID, final int res) throws Exception {
		CommandResponse reponse = COMMAND_RESPONSE.get();
		reponse.setRes(res);
		reponse.setPacketID(broadCastID);

		
		ClientStatMachine csm = NodeStatus.getInstance().getInstance().getClientStat(fromUUIDHex);
		ServerMessage message = SERVER_MESSAGE_CREATOR.newServerMessage(csm.getLastAddr(), 
				JsonCreator.toJsonWithGson(reponse, CommandResponse.class, LOCAL_GSON.get()), Commander.CMD_STORE);
		IMServer.getInstance().pushInstanceMessage(message);
		
		
	}

	private void storeMessage(final BroadCast broadcast, long ipv4) throws SQLException {
		DbHelper.batchExec(
				__INSERT_SQL,
				new Object[][] { {
						UUID.randomUUID().toString(),
						broadcast.getAuthorUUID(),
						broadcast.getBody(),
						broadcast.getLat().doubleValue(),
						broadcast.getLon().doubleValue(),
						broadcast.getLat().multiply(LAT_MASK).intValue(),
						broadcast.getLon().multiply(LON_MASK).intValue(), 
						ipv4 } });
	}
	

}
