package com.shico.mnm.common.client;

public abstract class ParentRunnable implements Runnable {
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
		System.out.println("################# Setting children to run.");
		for (ChildRunnable child : childRunnables) {
			child.run();
		}
	}

	public synchronized void done(){
		count--;
		System.out.println("################# One child is done.");
		if(count <= 0){	
			System.out.println("################# No more waiting for children. Parent starts.");
			doRun();
		}
	}
}
