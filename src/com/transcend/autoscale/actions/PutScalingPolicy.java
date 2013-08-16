package com.transcend.autoscale.actions;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.PutScalingPolicyMessage.PutScalingPolicyRequestMessage;
import com.transcend.autoscale.message.PutScalingPolicyMessage.PutScalingPolicyResultMessage;
import com.yammer.metrics.core.Meter;

public class PutScalingPolicy
        extends
        AbstractQueuedAction<PutScalingPolicyRequestMessage, PutScalingPolicyResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "PutScalingPolicy");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		PutScalingPolicyResultMessage message) {
    	
		final XMLNode xn = new XMLNode("PutScalingPolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "PutScalingPolicyResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());
		QueryUtil.addNode(xr, "PolicyARN", message.getPolicyARN());
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
    public PutScalingPolicyRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final PutScalingPolicyRequestMessage.Builder tReq =
    			PutScalingPolicyRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();

		tReq.setAdjustmentType(QueryUtil.requiredString(in, "AdjustmentType"));
		tReq.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		tReq.setCooldown(QueryUtil.getInt(in, "Cooldown"));
		tReq.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
		tReq.setScalingAdjustment(QueryUtil.requiredInt(in, "ScalingAdjustment"));
		tReq.setMinAdjustmentStep(QueryUtil.getInt(in, "MinAdjustmentStep"));
		
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
    		PutScalingPolicyResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
