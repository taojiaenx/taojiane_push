package org.ddpush.im.util;

import io.netty.util.concurrent.Future;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultExcectorQueuePolicy implements RejectedExecutionHandler {
    /**
     * Creates an {@code AbortPolicy}.
     */
    public DefaultExcectorQueuePolicy() { }

    /**
     * Always throws RejectedExecutionException.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     * @throws RejectedExecutionException always.
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (r instanceof Future) {
        	((Future) r).cancel(false);
        }
    }
}
