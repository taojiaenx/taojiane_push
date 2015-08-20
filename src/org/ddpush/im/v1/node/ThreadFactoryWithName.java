package org.ddpush.im.v1.node;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryWithName implements ThreadFactory{
	final Class workerClass;
	public ThreadFactoryWithName(Class wokerClass) {
		this.workerClass = wokerClass;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, workerClass.getName() + "-" + r.hashCode());
	}

}
