package com.transcend.autoscale.actions;

import java.util.Map;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.SetDesiredCapacityMessage.SetDesiredCapacityRequestMessage;
import com.transcend.autoscale.message.SetDesiredCapacityMessage.SetDesiredCapacityResultMessage;
import com.yammer.metrics.core.Meter;

public class SetDesiredCapacity
        extends
        AbstractQueuedAction<SetDesiredCapacityRequestMessage, SetDesiredCapacityResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "SetDesiredCapacity");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		SetDesiredCapacityResultMessage message) {
        final XMLNode xn = new XMLNode("SetDesiredCapacityResponse");
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
    public SetDesiredCapacityRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final SetDesiredCapacityRequestMessage.Builder tReq =
    			SetDesiredCapacityRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	
    	tReq.setAutoScalingGroupName(QueryUtil.requiredString(in, "AutoScalingGroupName"));		
		tReq.setDesiredCapacity(QueryUtil.requiredInt(in, "DesiredCapacity"));
		tReq.setHonorCooldown(QueryUtil.getBoolean(in, "HonorCooldown"));
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
    		SetDesiredCapacityResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
