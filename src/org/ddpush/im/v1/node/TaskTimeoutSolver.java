package org.ddpush.im.v1.node;

import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTimeoutSolver implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(TaskTimeoutSolver.class);
	private final FutureTask<Integer> taskFuture;

	public TaskTimeoutSolver(final FutureTask<Integer> taskFuture) {
		this.taskFuture = taskFuture;
	}

	private void solveTimeout() {
		if (!taskFuture.isDone()) {
			logger.warn("超时任务！");
			taskFuture.cancel(false);
		}
	}

	@Override
	public void run() {
		solveTimeout();
	}
}