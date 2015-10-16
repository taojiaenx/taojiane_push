package org.ddpush.service.broadCast.exeutor;

import org.ddpush.im.util.SeverMessageCreator;

import io.netty.util.concurrent.FastThreadLocal;



import com.google.gson.Gson;

/**
 * 带Json的命令执行器
 * @author taojiaen
 *
 */
public interface  BaseExecutor{
	static FastThreadLocal<Gson> LOCAL_GSON = new  FastThreadLocal<Gson>(){
		@Override
        protected Gson initialValue() {
            return new Gson();
        }
	};
	static SeverMessageCreator SERVER_MESSAGE_CREATOR = new SeverMessageCreator();
}
