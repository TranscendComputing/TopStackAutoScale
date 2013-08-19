package com.transcend.autoscale.actions;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.ExecutePolicyMessage.ExecutePolicyRequestMessage;
import com.transcend.autoscale.message.ExecutePolicyMessage.ExecutePolicyResultMessage;
import com.yammer.metrics.core.Meter;

public class ExecutePolicy
        extends
        AbstractQueuedAction<ExecutePolicyRequestMessage, ExecutePolicyResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "ExecutePolicy");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		ExecutePolicyResultMessage message) {
    	final XMLNode xn = new XMLNode("ExecutePolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "ExecutePolicyResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());
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
    public ExecutePolicyRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final ExecutePolicyRequestMessage.Builder tReq =
    			ExecutePolicyRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();

		tReq.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		tReq.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
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
    		ExecutePolicyResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
