package org.ddpush.im.util;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryWithName implements ThreadFactory{
	final String workerName;
	public ThreadFactoryWithName(Class wokerClass) {
		this.workerName = wokerClass.getName();
	}
	public ThreadFactoryWithName(final String name) {
		this.workerName = name;
	} 

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, workerName + "-" + r.hashCode());
	}

}
