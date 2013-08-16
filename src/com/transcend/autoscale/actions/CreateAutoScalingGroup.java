package com.transcend.autoscale.actions;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.transform.CreateAutoScalingGroupRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateAutoScalingGroup
        extends
        AbstractQueuedAction<CreateAutoScalingGroupRequestMessage, CreateAutoScalingGroupResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "CreateAutoScalingGroup");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		CreateAutoScalingGroupResultMessage message) {
        final XMLNode xn = new XMLNode("CreateAutoScalingGroupResponse");
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
    public CreateAutoScalingGroupRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final CreateAutoScalingGroupRequestMessage requestMessage =
                CreateAutoScalingGroupRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());

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
    		CreateAutoScalingGroupResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
