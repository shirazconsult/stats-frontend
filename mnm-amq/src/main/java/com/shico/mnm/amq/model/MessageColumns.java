package com.shico.mnm.amq.model;

import java.util.List;

public class MessageColumns {
	public final static int mIdIdx = 0;
	public final static int mTimestampIdx = 1;
	public final static int mTypeIdx = 2;
	public final static int mDestIdx = 3;
	public final static int mCorIdIdx = 4;
	public final static int mDelModeIdx = 5;
	public final static int mExpIdx = 6;
	public final static int mPrioIdx = 7;
	public final static int mRedelIdx = 8;
	public final static int mReplyToIdx = 9;
	public final static int mXMimeTypeIdx = 10;

	private List<String> colTypes;
	private List<String> colNames;

	public MessageColumns() {
		super();
	}

	public MessageColumns(List<String> colNames, List<String> colTypes) {
		super();
		this.colNames = colNames;
		this.colTypes = colTypes;
	}

	public String getId() {
		return colNames.get(mIdIdx);
	}

	public String getTimestamp() {
		return colNames.get(mTimestampIdx);
	}

	public String getType() {
		return colNames.get(mTypeIdx);
	}

	public String getDestination() {
		return colNames.get(mDestIdx);
	}

	public String getCorrelationId() {
		return colNames.get(mCorIdIdx);
	}

	public String getDelMode() {
		return colNames.get(mDelModeIdx);
	}

	public String getExpiration() {
		return colNames.get(mExpIdx);
	}

	public String getPriority() {
		return colNames.get(mPrioIdx);
	}

	public String getRedelivery() {
		return colNames.get(mRedelIdx);
	}

	public String getReplyTo() {
		return colNames.get(mReplyToIdx);
	}

	public String getXMimeType() {
		return colNames.get(mXMimeTypeIdx);
	}
}
