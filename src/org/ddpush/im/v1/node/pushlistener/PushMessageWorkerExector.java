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
public class PushMessageWorkerExector extends AbstractExecutorService {
	
	private AbstractExecutorService[] executorServices;
	private final int workerNum;
	private final int idmask;

	public PushMessageWorkerExector(final int workerNum) {
		executorServices = new ExcectorQueue[workerNum];
		this.workerNum = workerNum;
		this.idmask = workerNum - 1;
	}

	@Override
	public void shutdown() {
		for (final AbstractExecutorService executor : executorServices) {
			executor.shutdown();
		}
	}

	@Override
	public List<Runnable> shutdownNow() {
		final List ret = new ArrayList<Runnable>();
		for (final AbstractExecutorService executor : executorServices) {
			ret.addAll(executor.shutdownNow());
		}
		return ret;
	}

	@Override
	public boolean isShutdown() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return false;
	}

	@Override
	public void execute(Runnable command) {
		try {
			execute(command, getQueueId(command));
		} catch (Exception ignore) {
			// do something
		}
	}
	
	/**
	 * 
	 * @param command
	 * @param attr 配置队列ID的对象
	 */
	public void execute(Runnable command, Object attr) {
		try {
			execute(command, getQueueId(attr));
		} catch (Exception ignore) {
			// do something
		}
	}

	/**
	 * 队列ID生成器
	 * @param command
	 * @param queueId
	 * @throws Exception
	 */
	private void execute(Runnable command, final int queueId) throws Exception {
		if (workerNum <= queueId) {
			throw new Exception("illeagal id");
		}
		executorServices[queueId].execute(command);
	}

	private int getQueueId(Object command) {
		return command.hashCode() & idmask;
	}


}

/**
 * 队列
 * @author taojiaen
 *
 */
class ExcectorQueue extends ThreadPoolExecutor {
	private static ExcectorQueuePolicy defaultRejectionHandler = new ExcectorQueuePolicy();
	private static ExcectorQueueThreadFactory defaultThreadFactory = new ExcectorQueueThreadFactory();
	private static int MAX_QUEUE_LENGTH = PropertyUtil
			.getPropertyInt("EXECTOR_QUEUE_LIMIT");

	public ExcectorQueue() {
		super(1, 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(
				MAX_QUEUE_LENGTH), defaultThreadFactory, defaultRejectionHandler);
	}
}

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
