package com.shico.mnm.stats.client.comp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.shico.mnm.stats.client.StatsChartDataProvider;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationAcceleration;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ChartFormWin extends Window {
	
	private Callback<Map<String, Object>, String> callback;
	private VLayout container;

	public ChartFormWin(Callback<Map<String, Object>, String> callback) {
		super();
		this.callback = callback;
	
		setWidth(275);  
		setHeight(150);
		setHeaderControls();
		setIsModal(true);
		setAnimateShowTime(1000);
		setAnimateAcceleration(AnimationAcceleration.SMOOTH_END);

		container = new VLayout();
		container.setWidth("*");
		container.setMembersMargin(20);
		container.setMargin(10);
		container.setLayoutAlign(VerticalAlignment.CENTER);

		setupForm();	
		
		addMember(container);
	}

	private void setupForm(){
		DynamicForm chartForm = new DynamicForm();  
        chartForm.setWidth100();  
        chartForm.setIsGroup(true);  
        chartForm.setGroupTitle("Chart Specification");  
  
        final ComboBoxItem typeItem = new ComboBoxItem();  
        typeItem.setTitle("Event");  
        typeItem.setType("comboBox");  
        typeItem.setValueMap(StatsChartDataProvider.statsEvents);
        typeItem.setDefaultValue(StatsChartDataProvider.statsEvents[0]);
        
        final DateItem fromDateItem = new DateItem();  
        fromDateItem.setTitle("From");  
  
        final DateItem toDateItem = new DateItem();  
        toDateItem.setTitle("To");  
  
        Button goBtn = new Button("Go");
        goBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Map<String, Object> input = new HashMap<String, Object>();
								
				Date from = (Date)fromDateItem.getValue();
				Date to = (Date)toDateItem.getValue();
				if(to.before(from)){
					callback.onFailure("'from' date must not be after 'to' date");
					animateHide(AnimationEffect.SLIDE);
					return;
				}
				Date date = new Date();
				if(to.after(date)){
					callback.onFailure("'to' date must not be a date in future.");
					animateHide(AnimationEffect.SLIDE);
					return;					
				}
//				CalendarUtil.setToFirstDayOfMonth(date);
//				CalendarUtil.addMonthsToDate(date, -3);
//				if(from.before(date)){
//					callback.onFailure("'from' date must not be earlier than three months ago.");
//					animateHide(AnimationEffect.SLIDE);
//					return;					
//				}


				input.put("from", getISO8601Date(fromDateItem.getDisplayValue()));
				input.put("to", getISO8601Date(toDateItem.getDisplayValue()));
				input.put("type", typeItem.getValue());
				callback.onSuccess(input);
				animateHide(AnimationEffect.SLIDE);
			}
		});
        
        Button noGoBtn = new Button("Cancel");
        noGoBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				animateHide(AnimationEffect.SLIDE);
			}
        });
        
//        RelativeDateItem relativeDateItem = new RelativeDateItem("rdi", "Relative Date");   
        chartForm.setItems(fromDateItem, toDateItem, typeItem);  
        
        container.addMember(chartForm);
        HLayout hl = new HLayout();
        hl.setLayoutAlign(Alignment.CENTER);
        hl.setLayoutAlign(VerticalAlignment.CENTER);
        hl.setWidth100();
        hl.setHeight(24);
        hl.addMember(goBtn);
        hl.addMember(noGoBtn);
        container.addMember(hl);
	}

	private String getISO8601Date(String displayValue){
		String[] split = displayValue.split(" ");
		return split[2] + "-" + getMonth(split[0]) + "-" + split[1];		
	}
	
	private int getMonth(String month){
		if(month.equals("Jan")){
			return 1;
		}
		if(month.equals("Feb")){
			return 2;
		}
		if(month.equals("Mar")){
			return 3;
		}
		if(month.equals("Apr")){
			return 4;
		}
		if(month.equals("May")){
			return 5;
		}
		if(month.equals("Jun")){
			return 6;
		}
		if(month.equals("Jul")){
			return 7;
		}
		if(month.equals("Aug")){
			return 8;
		}
		if(month.equals("Sep")){
			return 9;
		}
		if(month.equals("Oct")){
			return 10;
		}
		if(month.equals("Nov")){
			return 11;
		}
		return 12;  // "Dec"
	}
}
