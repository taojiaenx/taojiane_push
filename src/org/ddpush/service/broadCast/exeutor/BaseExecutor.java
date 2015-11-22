package org.ddpush.service.broadCast.exeutor;

import io.netty.util.concurrent.FastThreadLocal;

import java.math.BigDecimal;
import java.sql.Date;

import org.ddpush.im.util.json.DateTimeDeserializer;
import org.ddpush.im.util.json.DateTimeSerializer;
import org.ddpush.service.broadCast.BroadCastMessageCreator;
import org.ddpush.service.broadCast.CommandResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 带Json的命令执行器
 * 
 * @author taojiaen
 *
 */
public interface BaseExecutor {
	/**
	 * 为了简化计算，经纬度需要相乘的因子
	 */
	final static BigDecimal LAT_MASK = new BigDecimal(1053.0);
	final static BigDecimal LON_MASK = new BigDecimal(10000.0);
	static FastThreadLocal<Gson> LOCAL_GSON = new FastThreadLocal<Gson>() {
		@Override
		protected Gson initialValue() {
			return new Gson();
		}
	};
	static BroadCastMessageCreator SERVER_MESSAGE_CREATOR = new BroadCastMessageCreator();
	final static FastThreadLocal<CommandResponse> COMMAND_RESPONSE = new FastThreadLocal<CommandResponse>() {
		@Override
		protected CommandResponse initialValue() {
			return new CommandResponse();
		}
	};
}
