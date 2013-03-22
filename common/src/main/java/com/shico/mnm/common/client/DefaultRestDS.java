package com.shico.mnm.common.client;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DefaultRestDS extends RestDataSource{
	String datasourceID;

	public DefaultRestDS() {
		super();

		OperationBinding fetch = new OperationBinding();  
        fetch.setOperationType(DSOperationType.FETCH);
        fetch.setDataProtocol(DSProtocol.POSTMESSAGE);
        OperationBinding add = new OperationBinding();  
        add.setOperationType(DSOperationType.ADD);  
        add.setDataProtocol(DSProtocol.POSTMESSAGE);  
        OperationBinding update = new OperationBinding();  
        update.setOperationType(DSOperationType.UPDATE);  
        update.setDataProtocol(DSProtocol.POSTMESSAGE);  
        OperationBinding remove = new OperationBinding();  
        remove.setOperationType(DSOperationType.REMOVE);  
        remove.setDataProtocol(DSProtocol.POSTMESSAGE);  
        
        setOperationBindings(fetch, add, update, remove);     
        
        RPCManager.setAllowCrossDomainCalls(true);
	}

	public DefaultRestDS(String datasourceID) {
		this();
		this.datasourceID = datasourceID;
		setSendMetaData(false);
	}
	
	public DefaultRestDS(String datasourceID, String restUrl) {
		this(datasourceID);
		setDataURL(restUrl);
	}
}
