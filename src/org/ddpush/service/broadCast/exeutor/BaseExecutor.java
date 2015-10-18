package org.ddpush.service.broadCast.exeutor;

import java.math.BigDecimal;

import io.netty.util.concurrent.FastThreadLocal;

import org.ddpush.service.broadCast.BroadCastMessageCreator;
import org.ddpush.service.broadCast.CommandResponse;

import com.google.gson.Gson;

/**
 * 带Json的命令执行器
 * @author taojiaen
 *
 */
public interface  BaseExecutor{
	/**
	 * 为了简化计算，经纬度需要相乘的因子
	 */
	final static BigDecimal LAT_MASK = new BigDecimal(1053.0);
	final static BigDecimal LON_MASK = new BigDecimal(10000.0);
	static FastThreadLocal<Gson> LOCAL_GSON = new  FastThreadLocal<Gson>(){
		@Override
        protected Gson initialValue() {
            return new Gson();
        }
	};
	static BroadCastMessageCreator SERVER_MESSAGE_CREATOR = new BroadCastMessageCreator();
	final  static FastThreadLocal<CommandResponse> COMMAND_RESPONSE = new  FastThreadLocal<CommandResponse>(){
		@Override
        protected CommandResponse initialValue() {
            return new CommandResponse();
        }
	};
}
