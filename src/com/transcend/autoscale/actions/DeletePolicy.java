package com.transcend.autoscale.actions;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.DeletePolicyRequest;
import com.generationjava.io.xml.XMLNode;
import com.google.common.base.Strings;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DeletePolicyMessage.DeletePolicyRequestMessage;
import com.transcend.autoscale.message.DeletePolicyMessage.DeletePolicyResultMessage;
import com.transcend.autoscale.message.PutScalingPolicyMessage.PutScalingPolicyRequestMessage;
import com.yammer.metrics.core.Meter;

public class DeletePolicy
        extends
        AbstractQueuedAction<DeletePolicyRequestMessage, DeletePolicyResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DeletePolicy");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DeletePolicyResultMessage message) {

		final XMLNode xn = new XMLNode("DeletePolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "DeletePolicyResult");

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
    public DeletePolicyRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
    	
    	final DeletePolicyRequestMessage.Builder tReq =
    			DeletePolicyRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	tReq.setAutoScalingGroupName(Strings.nullToEmpty(QueryUtil.getString(in,
				"AutoScalingGroupName")));
		tReq.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
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
    		DeletePolicyResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
