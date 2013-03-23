package com.shico.mnm.common.client;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ParentRunnable implements Runnable {
	private static Logger logger = Logger.getLogger("ParentRunnable");
	
	int count;
	ChildRunnable[] childRunnables;
	
	public ParentRunnable(ChildRunnable...childRunnables) {
		super();
		this.childRunnables = childRunnables;
		count = childRunnables.length;
        for (ChildRunnable runnable : childRunnables) {
        	runnable.setParent(this);
        }
	}

	public abstract void doRun();

	@Override
	public final void run() {
		logger.log(Level.FINER, "Setting children to run.");
		for (ChildRunnable child : childRunnables) {
			child.run();
		}
	}

	public synchronized void done(){
		count--;
		logger.log(Level.FINER, "One child is done.");
		if(count <= 0){	
			logger.log(Level.FINER, "No more waiting for children. Parent starts.");
			doRun();
		}
	}
}
