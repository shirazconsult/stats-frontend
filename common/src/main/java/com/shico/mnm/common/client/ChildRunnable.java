package com.shico.mnm.common.client;

public abstract class ChildRunnable implements Runnable {
	ParentRunnable parent;
	
	public ChildRunnable() {
		super();
	}

	public abstract void doRun();
	
	@Override
	public void run() {
		System.out.println("################# Child starts running.");
		doRun();
	}

	public void setParent(ParentRunnable parent) {
		this.parent = parent;
	}

	public ParentRunnable getParent() {
		return parent;
	}
}
