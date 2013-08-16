package com.transcend.autoscale.actions;

import java.util.Map;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.SuspendProcessesMessage.SuspendProcessesRequestMessage;
import com.transcend.autoscale.message.SuspendProcessesMessage.SuspendProcessesResultMessage;
import com.yammer.metrics.core.Meter;

public class SuspendProcesses
        extends
        AbstractQueuedAction<SuspendProcessesRequestMessage, SuspendProcessesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "SuspendProcesses");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		SuspendProcessesResultMessage message) {
        final XMLNode xn = new XMLNode("SuspendProcessesResponse");
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
    public SuspendProcessesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final SuspendProcessesRequestMessage.Builder tReq =
    			SuspendProcessesRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	
    	tReq.setAutoScalingGroupName(QueryUtil.requiredString(in, "AutoScalingGroupName"));		
		tReq.addAllScalingProcesses(QueryUtil.getStringArray(in, "ScalingProcesses.member", Integer.MAX_VALUE));
    	
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
    		SuspendProcessesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
