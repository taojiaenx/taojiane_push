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

import io.netty.channel.Channel;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.ddpush.im.v1.node.ClientStatMachine;
import org.ddpush.im.v1.node.NodeStatus;
import org.ddpush.im.v1.node.PushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 修改过的pushTask
 * 
 * @author taojiaen
 *
 */

public class PushTask extends FutureTask<Integer> {
	private static Logger logger = LoggerFactory.getLogger(PushTask.class);
			

	public PushTask(final Channel channel, final PushMessage data) {
		super(new ProcessDataCallable(channel, data));
	}

	@Override
	protected void done() {
	}
}

class ProcessDataCallable implements Callable<Integer> {
	private static Logger logger = LoggerFactory
			.getLogger(ProcessDataCallable.class);
	private PushMessage data;
	private Channel channel;

	public ProcessDataCallable(final Channel channel, final PushMessage data) {
		this.channel = channel;
		this.data = data;
	}

	@Override
	public Integer call() throws Exception {

		Byte res = 0;
		try {
			processReq();
		} catch (Exception e) {
			logger.error("处理消息返回1{}", e.getMessage());
			res = 1;
		} catch (Throwable e) {
			logger.error("处理消息返回-1{}", e.getMessage());
			res = -1;
		}
		if (channel != null && channel.isActive()) {
			channel.writeAndFlush(res);
		}
		channel = null;
		return 0;
	}

	private void processReq() throws Exception {
		if (data == null)
			return;
		try {
			NodeStatus nodeStat = NodeStatus.getInstance();
			String uuid = data.getUuidHexString();
			ClientStatMachine csm = nodeStat.getClientStat(uuid);
			if (csm == null) {//
				csm = ClientStatMachine.newByPushReq(data);
				if (csm == null) {
					throw new Exception("can not new state machine");
				}
				nodeStat.putClientStat(uuid, csm);
			} else {
				csm.onPushMessage(data);
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			data = null;
		}

	}

}
