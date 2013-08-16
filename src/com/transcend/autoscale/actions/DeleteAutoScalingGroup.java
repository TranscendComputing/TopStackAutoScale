package com.transcend.autoscale.actions;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.transform.DeleteAutoScalingGroupRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DeleteAutoScalingGroupMessage.DeleteAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.DeleteAutoScalingGroupMessage.DeleteAutoScalingGroupResultMessage;
import com.yammer.metrics.core.Meter;

public class DeleteAutoScalingGroup
        extends
        AbstractQueuedAction<DeleteAutoScalingGroupRequestMessage, DeleteAutoScalingGroupResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DeleteAutoScalingGroup");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DeleteAutoScalingGroupResultMessage message) {
        final XMLNode xn = new XMLNode("DeleteAutoScalingGroupResponse");
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
    public DeleteAutoScalingGroupRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        DeleteAutoScalingGroupRequestMessage requestMessage;
		try {
			requestMessage = DeleteAutoScalingGroupRequestUnmarshaller
			.getInstance().unmarshall(req.getParameterMap());
		} catch (Exception e) {
			throw ErrorResponse.InternalFailure();
		}

        return requestMessage;
        
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
    		DeleteAutoScalingGroupResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
