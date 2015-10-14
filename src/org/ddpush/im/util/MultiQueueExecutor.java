package org.ddpush.im.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;


/**
 * 
 * @author taojiaen
 *
 */
public class MultiQueueExecutor{
	private int queueNum = 1;
	private Sin
	
	public MultiQueueExecutor(){
		this(1);
	}
	
	public MultiQueueExecutor(int queueNum) {
		this.queueNum = queueNum;
	}


	public void execute(final int plainid, final Runnable command) {
		
	}
	private void executeByRealID(final int runid, final Runnable command) {
		
	}
	public <T> Future<T> submit(final int runid, Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        executeByRealID(runid, ftask);
        return ftask;
    }
	
	public void shutDowm(){}
	public void shutDowNow(){}
	
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

}
