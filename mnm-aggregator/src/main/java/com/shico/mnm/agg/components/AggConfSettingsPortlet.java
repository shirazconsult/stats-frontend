package com.shico.mnm.agg.components;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shico.mnm.agg.client.AggSettingsController;
import com.shico.mnm.agg.model.AggConfSettingsDS;
import com.shico.mnm.agg.model.AggRemoteSettingsDS;
import com.shico.mnm.common.component.PortletWin;
import com.shico.mnm.common.event.DataLoadedEvent;
import com.shico.mnm.common.event.DataLoadedEventHandler;
import com.shico.mnm.common.event.EventBus;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditFailedEvent;
import com.smartgwt.client.widgets.grid.events.EditFailedHandler;
import com.smartgwt.client.widgets.grid.events.EditorExitEvent;
import com.smartgwt.client.widgets.grid.events.EditorExitHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class AggConfSettingsPortlet extends PortletWin implements DataLoadedEventHandler {
	private static Logger logger = Logger.getLogger("AggAdminSettingsPortlet");

	public final static String TITLE = "Configuration Settings";
	VLayout container;
	AggSettingsController settingsConroller;
	AggConfSettingsDS datasource;
	String aggregatorUrl;
	ListGrid listGrid;

	public AggConfSettingsPortlet(AggSettingsController settingsConroller) {
		super(TITLE);
		this.settingsConroller = settingsConroller;
		String aggregatorUrl = (String)settingsConroller.getSetting(AggRemoteSettingsDS.AGGREGATORURL);
		datasource = new AggConfSettingsDS(aggregatorUrl);

		
		container = new VLayout();
		container.setAlign(Alignment.CENTER);
		container.setAlign(VerticalAlignment.CENTER);
		container.setMembersMargin(10);
		container.setMargin(10);
		
		container.addMember(getListGrid());
		
		addItem(container);
		
		setHeight(355);

		EventBus.instance().addHandler(DataLoadedEvent.TYPE, this);
	}

	private ListGrid getListGrid(){
		if(listGrid == null){
			listGrid = new MyListGrid();
			listGrid.setAlign(Alignment.CENTER);
			listGrid.setWidth100();
			listGrid.setHeight(280);
			listGrid.setCellHeight(24);
			listGrid.setEditByCell(true);
			listGrid.setShowAllRecords(true);
			listGrid.setGroupStartOpen(GroupStartOpen.NONE);
			listGrid.setGroupByField("category");
			listGrid.setDataSource(datasource); 

			ListGridField catF = new ListGridField("category", "category");
			ListGridField nameF = new ListGridField("name", "Name");
			ListGridField valueF = new ListGridField("value", "Value");
			ListGridField readonlyF = new ListGridField("readonly", "Read Only");
			ListGridField requireRestartF = new ListGridField("requireRestart", "Requires Restart"); 

			nameF.setCanSort(true);
			nameF.setCanGroupBy(false);
			valueF.setCanSort(false);
			valueF.setCanEdit(true);
			valueF.setCanGroupBy(false);
			valueF.addEditorExitHandler(new EditorExitHandler() {				
				@Override
				public void onEditorExit(EditorExitEvent event) {
					Object newValue = event.getNewValue();
					if(newValue == null){
						listGrid.discardAllEdits();
					}else{
						Record record = event.getRecord();
						record.setAttribute(AggConfSettingsDS.AGG_NAME, "localhost");
					}
				}
			});
			readonlyF.setCanSort(false);
			readonlyF.setCanGroupBy(false);
			requireRestartF.setCanSort(false);
			requireRestartF.setCanGroupBy(false);
			readonlyF.setAlign(Alignment.CENTER);
			requireRestartF.setAlign(Alignment.CENTER);
			
			listGrid.setSelectionType(SelectionStyle.SINGLE);
			listGrid.setFields(catF, nameF, valueF, readonlyF, requireRestartF);
			listGrid.hideField("category");
			
			listGrid.sort("name", SortDirection.ASCENDING);
		}
		return listGrid;
	}
	
	public void update(){
		Criteria criteria = new Criteria(AggConfSettingsDS.AGG_NAME, "localhost");
		listGrid.fetchData(criteria);
		listGrid.groupBy("category");
	}
		
	@Override
	protected void handleRefresh() {
		listGrid.invalidateCache();
	}

	@Override
	protected void handleSettings() {
		SC.say("Not implemented yet.");
	}

	@Override
	protected void handleHelp() {
		SC.say("Not implemented yet.");
	}

	@Override
	protected void handleClose() {
		// do nothing. this portlet must not be closed
	}

	@Override
	public void onDataLoaded(DataLoadedEvent event) {
		switch(event.eventType){
		case AGG_ADMIN_SETTINGS_CHANGED_EVENT:
			String aurl = (String)event.info.get(AggRemoteSettingsDS.AGGREGATORURL);
			if(aurl != null && !aurl.trim().isEmpty()){
				if(aggregatorUrl != null && !aurl.equals(aggregatorUrl)){
					datasource.destroy();
					datasource = new AggConfSettingsDS(aurl);
					datasource.setAttribute(AggConfSettingsDS.AGG_NAME, "localhost", true);
					listGrid.setDataSource(datasource);					
				}
				update();
			}
			aggregatorUrl = aurl;
			break;
		}
	}

	private void printMap(Map map){
		if(map == null){
			return;
		}
		for (Object key : map.keySet()) {
			logger.log(Level.INFO, ":::: "+key+"="+map.get(key));
		}
	}
	
	class MyListGrid extends ListGrid {
		
		public MyListGrid() {
			super();
			
			addEditFailedHandler(new EditFailedHandler() {
				@Override
				public void onEditFailed(EditFailedEvent event) {
					listGrid.discardAllEdits();
				}
			});
		}

		@Override
		protected boolean canEditCell(int rowNum, int colNum) {
			ListGridRecord record = getRecord(rowNum);
			if(record != null){
				Boolean readonly = Boolean.valueOf(record.getAttribute("readonly"));
				return !readonly && colNum == 1;
			}
			return false;
		}
	}
	
}
