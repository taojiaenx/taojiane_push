package org.ddpush.service.broadCast.exeutor;

import io.netty.util.concurrent.FastThreadLocal;


import com.google.gson.Gson;

/**
 * 带Json的命令执行器
 * @author taojiaen
 *
 */
public interface  JsonExecutor{
	static FastThreadLocal<Gson> LOCAL_GSON = new  FastThreadLocal<Gson>(){
		@Override
        protected Gson initialValue() {
            return new Gson();
        }
	};
}
