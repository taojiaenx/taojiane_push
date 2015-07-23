/*
 *Copyright 2014 DDPush
 *Author: AndyKwok(in English) GuoZhengzhu(in Chinese)
 *Email: ddpush@126.com
 *

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package org.ddpush.im.v1.node.pushlistener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.ddpush.im.v1.node.ClientStatMachine;
import org.ddpush.im.v1.node.NodeStatus;
import org.ddpush.im.v1.node.PushMessage;

/**
 * 修改过的pushTask
 * 
 * @author taojiaen
 *
 */

public class PushTask extends FutureTask<Integer> {
	private final ChannelHandlerContext ctx;
	private final ByteBuf data;
	public  PushTask(ChannelHandlerContext ctx, ByteBuf data) {
		super(new ProcessDataCallable(data));
		this.ctx = ctx;
		this.data = data;
	}


	@Override
	protected void done() {
		byte res = 0;
		try {
			get();
		} catch (Exception e) {
			res = 1;
		} catch (Throwable t) {
			res = -1;
		}finally {
			if (data != null) {
				data.release();
			}
		}
		ctx.writeAndFlush(res);
	}
}

class ProcessDataCallable implements Callable<Integer>{
	
	private final ByteBuf data;
	public ProcessDataCallable(final ByteBuf data){
		this.data = data;
	}

	@Override
	public Integer call() throws Exception {
		processReq();
		return 0;
	}
	
	private void processReq() throws Exception {
		PushMessage pm = null;
		try {
			byte[] byteData = new byte[data.readableBytes()];
			final int readerIndex = data.readerIndex();
		    data.getBytes(readerIndex, byteData);
			pm = new PushMessage(byteData);
			NodeStatus nodeStat = NodeStatus.getInstance();
			String uuid = pm.getUuidHexString();
			ClientStatMachine csm = nodeStat.getClientStat(uuid);
			if (csm == null) {//
				csm = ClientStatMachine.newByPushReq(pm);
				if (csm == null) {
					throw new Exception("can not new state machine");
				}
				nodeStat.putClientStat(uuid, csm);
			} else {
				try {
					csm.onPushMessage(pm);
				} catch (Exception e) {
				}
				;
			}
		} catch(Throwable e){
			throw e;
		}finally {
			pm = null;
		}

	}
	
}
