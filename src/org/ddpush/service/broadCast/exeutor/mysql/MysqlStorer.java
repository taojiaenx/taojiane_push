package org.ddpush.service.broadCast.exeutor.mysql;

import java.math.BigDecimal;
import java.util.UUID;

import org.ddpush.im.util.JsonConvertor;
import org.ddpush.im.util.StringUtil;
import org.ddpush.im.v1.node.PushMessage;
import org.ddpush.service.broadCast.BroadCast;
import org.ddpush.service.broadCast.Storer;
import org.ddpush.service.broadCast.exeutor.BaseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddpush.dao.DbHelper;

/**
 * mysql广播存储器
 * 
 * @author taojiaen
 *
 */
public class MysqlStorer implements Runnable, BaseExecutor, Storer {
	private final static Logger log = LoggerFactory
			.getLogger(MysqlStorer.class);
	/**
	 * 为了简化计算，经纬度需要相乘的因子
	 */
	private final static BigDecimal POSTION_MASK = new BigDecimal(8192.0);
	private final static String __INSERT_SQL = "insert into broadcast_storer(`broadcast_id`,`broadcast_body`,`lat`,`lon`,`lat_floor`,`lon_floor`,`ipv4`) values(?,?,?,?,?,?,?)";

	private PushMessage message;

	public MysqlStorer(final PushMessage message) {
		this.message = message;
	}

	@Override
	public void run() {
		if (message != null) {
			try {
				storeMessage((BroadCast) JsonConvertor.toObject(
						LOCAL_GSON.get(),
						StringUtil.convert(message), BroadCast.class),
						message.getIpv4());
			} catch (Exception e) {
				log.error("广播消息存储失败", e);
			}
		}
		message = null;
	}

	private void storeMessage(final BroadCast broadcast, long ipv4) {
		DbHelper.batchExec(
				__INSERT_SQL,
				new Object[][] { {
						UUID.randomUUID().toString(),
						broadcast.getLat().doubleValue(),
						broadcast.getLon().doubleValue(),
						broadcast.getLat().multiply(POSTION_MASK).intValue(),
						broadcast.getLon().multiply(POSTION_MASK).intValue(), 
						ipv4 } });
	}
	

}
