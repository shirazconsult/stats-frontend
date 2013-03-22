package com.shico.mnm.common.client;

import com.google.gwt.visualization.client.AbstractDataTable;

public interface ChartDataProvider {
	void getColumnNames();
	void getRows(final long from, final long to);
	void getLastRow();
	AbstractDataTable getDataTable();
	void schedule(int millis, int viewWindowMinutes);
}
