package org.ddpush.im.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多个id对应一个执行队列
 * 
 * @author taojiaen
 *
 */
public class MultiQueueExecutor {
	/**
	 * 最大队列长度
	 */
	private static final int MAX_QUEUE_LENGTH = 1024;
	private int queueNum = 1;
	/**
	 * 用与运算取代区域操作时，所用到的掩码
	 */
	private int maskNum = 0;
	private boolean isPowerOftow = true;
	private final ThreadPoolExecutor[] queueArray;

	public MultiQueueExecutor() {
		this(1);
	}

	public MultiQueueExecutor(int queueNum) {
		this.queueNum = queueNum;
		this.maskNum = queueNum - 1;
		this.isPowerOftow = isPowerOfTwo(queueNum);
		this.queueArray = new ThreadPoolExecutor[queueNum];
		;

		initPoolArray();
	}

	/**
	 * 队列组初始化
	 */
	private void initPoolArray() {
		for (int i = 0; i < queueArray.length; ++i) {
			queueArray[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(MAX_QUEUE_LENGTH),
					new ThreadFactoryWithName(getClass().getName() + "{" + i
							+ "}"), new DefaultExcectorQueuePolicy());
		}
	}

	/**
	 * 传入一个整形的id选择队列
	 * 
	 * @param plainid
	 * @param command
	 */
	public void execute(final int plainid, final Runnable command) {
		executeByRealID(solvePlainid(plainid), command);
	}

	private void executeByRealID(final int runid, final Runnable command) {
		queueArray[runid].execute(command);
	}

	/**
	 * 传入一个整形的id选择队列
	 * 
	 * @param runid
	 * @param task
	 * @return
	 */
	public <T> Future<T> submit(final int runid, Callable<T> task) {
		if (task == null)
			throw new NullPointerException();
		RunnableFuture<T> ftask = newTaskFor(task);
		execute(runid, ftask);
		return ftask;
	}

	public void shutDowm() {
		for (ThreadPoolExecutor queue : queueArray) {
			queue.shutdown();
		}
	}

	public void shutDowNow() {
		for (ThreadPoolExecutor queue : queueArray) {
			queue.shutdownNow();
		}
	}

	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new FutureTask<T>(callable);
	}

	/**
	 * 
	 * @param plainid
	 * @return
	 */
	protected int solvePlainid(final int plainid) {
		if (isPowerOftow) {
			return plainid & maskNum;
		} else {
			return plainid % queueNum;
		}
	}

	/**
	 * 是否是2的n次方
	 * 
	 * @param val
	 * @return
	 */
	private static boolean isPowerOfTwo(int val) {
		return (val & -val) == val;
	}

}
