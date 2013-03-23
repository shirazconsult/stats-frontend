package com.shico.mnm.common.client;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ChildRunnable implements Runnable {
	private static Logger logger = Logger.getLogger("ChildRunnable");
	
	ParentRunnable parent;
	
	public ChildRunnable() {
		super();
	}

	public abstract void doRun();
	
	@Override
	public void run() {
		logger.log(Level.FINER, "Child starts running.");
		doRun();
	}

	public void setParent(ParentRunnable parent) {
		this.parent = parent;
	}

	public ParentRunnable getParent() {
		return parent;
	}
}
