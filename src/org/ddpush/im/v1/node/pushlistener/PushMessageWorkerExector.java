package org.ddpush.im.v1.node.pushlistener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ddpush.im.util.PropertyUtil;

/**
 * 一个队列组， 每个command可以根据自己获得的id,运行在自己对应的单线程队列中
 * @author taojiaen
 *
 */
public class PushMessageWorkerExector  extends ThreadPoolExecutor {
	private static ExcectorQueuePolicy defaultRejectionHandler = new ExcectorQueuePolicy();
	private static ExcectorQueueThreadFactory defaultThreadFactory = new ExcectorQueueThreadFactory();
	private static int MAX_QUEUE_LENGTH = PropertyUtil
			.getPropertyInt("EXECTOR_QUEUE_LIMIT");
	private static int minThread = PropertyUtil
			.getPropertyInt("EXECTOR_CORE_THREAD");
	private static int maxThread =PropertyUtil
			.getPropertyInt("EXECTOR_MAX_THREAD");

	public PushMessageWorkerExector () {
		super(minThread, maxThread, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(
				MAX_QUEUE_LENGTH), defaultThreadFactory, defaultRejectionHandler);
	}
}

/**
 * 队列
 * @author taojiaen
 *
 */
/*class ExcectorQueue extends ThreadPoolExecutor {
	private static ExcectorQueuePolicy defaultRejectionHandler = new ExcectorQueuePolicy();
	private static ExcectorQueueThreadFactory defaultThreadFactory = new ExcectorQueueThreadFactory();
	private static int MAX_QUEUE_LENGTH = PropertyUtil
			.getPropertyInt("EXECTOR_QUEUE_LIMIT");

	public ExcectorQueue() {
		super(1, 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(
				MAX_QUEUE_LENGTH), defaultThreadFactory, defaultRejectionHandler);
	}
}*/

class ExcectorQueueThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r,"ExcectorQueueThread-"+ r.hashCode());
	}
	
}
class  ExcectorQueuePolicy implements RejectedExecutionHandler {
    /**
     * Creates an {@code AbortPolicy}.
     */
    public ExcectorQueuePolicy() { }

    /**
     * Always throws RejectedExecutionException.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     * @throws RejectedExecutionException always.
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        // do somethig
    }
}
