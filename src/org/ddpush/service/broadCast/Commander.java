package org.ddpush.service.broadCast;

import java.lang.reflect.InvocationTargetException;

import org.ddpush.im.util.MultiQueueExecutor;
import org.ddpush.im.util.ObjectFactory;
import org.ddpush.im.v1.node.PushMessage;
import org.ddpush.im.v1.node.listener.PushMessageListener;
import org.ddpush.service.broadCast.exeutor.mysql.MysqlQueryer;
import org.ddpush.service.broadCast.exeutor.mysql.MysqlStorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 广播推送处理器
 * @author taojiaen
 *
 */
public  class Commander extends MultiQueueExecutor implements PushMessageListener {
	static Commander signleObject = null;
	static Logger log = LoggerFactory.getLogger(Commander.class);
	/**
	 * 查询当前范围内的广播
	 */
	public static final int CMD_QUERY = 18;
	public static final int CMD_STORE = 19;
	
	/**
	 * 存储器class
	 */
	final Class<? extends Storer> sorterClass;
	final Class<? extends Queryer> queryerClass;


	public static Commander getInstance() {
		if (signleObject == null) {
			synchronized (Commander.class) {
				if (signleObject == null) {
					signleObject = new Commander();
				}
			}
		}
		return signleObject;
	}
	private Commander() {
		this.sorterClass = MysqlStorer.class;
		this.queryerClass = MysqlQueryer.class;
	}
	@Override
	public void received(PushMessage message) {
		try {
		  final int cmd = message.getCmd();
		  switch(cmd) {
		  case CMD_QUERY:
			  queryBroadCast(message);
			  break;
		  case CMD_STORE:
			  storeBrodadCast(message);
			  break;
		  default:;
		  }
		} catch(Exception e) {
			log.error("广播信息信息解析异常",e);
		}
	}
	/**
	 * 广播查询
	 * @param message
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws NumberFormatException 
	 */
	private void queryBroadCast(PushMessage message) throws Exception{
		final String sign = message.getUuidHexString().substring(message.getUuidHexString().length() - 4).toUpperCase();
		execute((int)(Long.parseLong(sign, 16)), 
				(Runnable) ObjectFactory.instantiate(queryerClass, message));
	};
	/**
	 * 广播存储
	 * @param message
	 * @throws Exception
	 */
	private void storeBrodadCast(PushMessage message) throws Exception{
		final String sign = message.getUuidHexString().substring(message.getUuidHexString().length() - 4).toUpperCase();
		execute((int)(Long.parseLong(sign, 16)), 
				(Runnable) ObjectFactory.instantiate(sorterClass, message));

	};
}
