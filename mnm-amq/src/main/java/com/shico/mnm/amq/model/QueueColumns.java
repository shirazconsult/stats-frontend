package com.shico.mnm.amq.model;

import java.util.List;

public class QueueColumns {
	public final static int qNameIdx = 0;
	public final static int qSizeIdx = 1;
	public final static int qEnqCntIdx = 2;
	public final static int qDeqCntIdx = 3;
	public final static int qInfCntIdx = 4;
	public final static int qExpCntIdx = 5;
	public final static int qDisCntIdx = 6;
	public final static int qProdCntIdx = 7;
	public final static int qConsCntIdx = 8;
	public final static int qAvgEnqTimeIdx = 9;
	public final static int qMinEnqTimeIdx = 10;
	public final static int qMaxEnqTimeIdx = 11;
	public final static int qMemUsePortionIdx = 12;
	public final static int qMaxPageSizeIdx = 13;

	List<String> qColTypes;
	private List<String> qColNames;

	public QueueColumns() {
		super();
	}

	public QueueColumns(List<String> colNames, List<String> colTypes) {
		super();
		this.qColNames = colNames;
		this.qColTypes = colTypes;
	}
	public String getName(){
		return qColNames.get(qNameIdx);
	}
	public String getSize(){
		return qColNames.get(qSizeIdx);
	}
	public String getEnqCnt(){
		return qColNames.get(qEnqCntIdx);
	}

	public String getDeqCnt(){
		return qColNames.get(qDeqCntIdx);
	}
	public String getInfCnt(){
		return qColNames.get(qInfCntIdx);
	}
	public String getExpCnt(){
		return qColNames.get(qExpCntIdx);
	}
	public String getDisCnt(){
		return qColNames.get(qDisCntIdx);
	}
	public String getProdCnt(){
		return qColNames.get(qProdCntIdx);
	}
	public String getConsCnt(){
		return qColNames.get(qConsCntIdx);
	}
	public String getAvgEnqTime(){
		return qColNames.get(qAvgEnqTimeIdx);
	}
	public String getMinEnqTime(){
		return qColNames.get(qMinEnqTimeIdx);
	}
	public String getMaxEnqTime(){
		return qColNames.get(qMaxEnqTimeIdx);
	}
	public String getMemUsePortion(){
		return qColNames.get(qMemUsePortionIdx);
	}
	public String getMaxPageSize(){
		return qColNames.get(qMaxPageSizeIdx);
	}
}
