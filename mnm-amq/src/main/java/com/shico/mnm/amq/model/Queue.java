package com.shico.mnm.amq.model;

import java.util.List;

public class Queue {	

	List<Object> queue;

	public Queue() {
		super();
	}

	public Queue(List<Object> queue) {
		super();
		this.queue = queue;
	}

	public String getName(){
		return (String)queue.get(QueueColumns.qNameIdx);
	}
	public int getSize(){
		return ((Double)queue.get(QueueColumns.qSizeIdx)).intValue();
	}
	public int getEnqCnt(){
		return ((Double)queue.get(QueueColumns.qEnqCntIdx)).intValue();
	}
	public int getDeqCnt(){
		return ((Double)queue.get(QueueColumns.qDeqCntIdx)).intValue();
	}
	public int getInfCnt(){
		return ((Double)queue.get(QueueColumns.qInfCntIdx)).intValue();
	}
	public int getExpCnt(){
		return ((Double)queue.get(QueueColumns.qExpCntIdx)).intValue();
	}
	public int getConsCnt(){
		return ((Double)queue.get(QueueColumns.qConsCntIdx)).intValue();
	}
	public int getProdCnt(){
		return ((Double)queue.get(QueueColumns.qProdCntIdx)).intValue();
	}
	public double getAvgEnqTime(){
		return Math.round((Double)queue.get(QueueColumns.qAvgEnqTimeIdx));
	}
	public double getMinEnqTime(){
		return Math.round((Double)queue.get(QueueColumns.qMinEnqTimeIdx));
	}
	public double getMaxEnqTime(){
		return Math.round((Double)queue.get(QueueColumns.qMaxEnqTimeIdx));
	}
	public float getMemUsePortion(){
		return Math.round((Float)queue.get(QueueColumns.qMemUsePortionIdx));
	}
	public int getMaxPageSize(){
		return (Integer)queue.get(QueueColumns.qMaxPageSizeIdx);
	}
	
	public List<Object> getQueue() {
		return queue;
	}

	public void setQueue(List<Object> queue) {
		this.queue = queue;
	}
}
