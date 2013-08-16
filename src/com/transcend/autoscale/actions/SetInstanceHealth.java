package com.transcend.autoscale.actions;

import java.util.Map;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.SetInstanceHealthMessage.SetInstanceHealthRequestMessage;
import com.transcend.autoscale.message.SetInstanceHealthMessage.SetInstanceHealthResultMessage;
import com.yammer.metrics.core.Meter;

public class SetInstanceHealth
        extends
        AbstractQueuedAction<SetInstanceHealthRequestMessage, SetInstanceHealthResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "SetInstanceHealth");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		SetInstanceHealthResultMessage message) {
        final XMLNode xn = new XMLNode("SetInstanceHealthResponse");
        xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
        // add metadata
        final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
        QueryUtil.addNode(meta, "RequestId", resp.getRequestId());
        return xn.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    
    @Override
    public SetInstanceHealthRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final SetInstanceHealthRequestMessage.Builder tReq =
    			SetInstanceHealthRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	
    	tReq.setHealthStatus(QueryUtil.requiredString(in, "HealthStatus"));
    	tReq.setInstanceId(QueryUtil.requiredString(in, "InstanceId"));
    	tReq.setShouldRespectGracePeriod(QueryUtil.getBoolean(in, "ShouldRespectGracePeriod"));    	
		return tReq.buildPartial();
    }
    
    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.
     * query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
    		SetInstanceHealthResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
