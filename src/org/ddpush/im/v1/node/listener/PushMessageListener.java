package org.ddpush.im.v1.node.listener;

import org.ddpush.im.v1.node.PushMessage;

/**
 * 推送消息监听器
 * @author taojiaen
 *
 */
public interface PushMessageListener {
	/**
	 * 默认状态下会与消息处理处于同一条线程执行，请注意执行消息
	 * 为了保证后续监听器的运行，请千万不要抛出任何异常
	 * @param message
	 */
	public void received(PushMessage message);
}
