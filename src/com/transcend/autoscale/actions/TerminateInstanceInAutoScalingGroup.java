package com.transcend.autoscale.actions;

import java.util.Map;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.TerminateInstanceInAutoScalingGroupMessage.TerminateInstanceInAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.TerminateInstanceInAutoScalingGroupMessage.TerminateInstanceInAutoScalingGroupResultMessage;
import com.transcend.autoscale.message.TerminateInstanceInAutoScalingGroupMessage.TerminateInstanceInAutoScalingGroupResultMessage.Activity;
import com.yammer.metrics.core.Meter;

public class TerminateInstanceInAutoScalingGroup
        extends
        AbstractQueuedAction<TerminateInstanceInAutoScalingGroupRequestMessage, TerminateInstanceInAutoScalingGroupResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "TerminateInstanceInAutoScalingGroup");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		TerminateInstanceInAutoScalingGroupResultMessage message) {
        final XMLNode xn = new XMLNode("TerminateInstanceInAutoScalingGroupResponse");
        xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "TerminateInstanceInAutoScalingGroupResult");
        // add metadata
        final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
        QueryUtil.addNode(meta, "RequestId", resp.getRequestId());
        final XMLNode activityNode = QueryUtil.addNode(xr, "Activity");
        Activity act = message.getActivity();
        //Adding nodes to Activity
        QueryUtil.addNode(activityNode, "ActivityId", act.getActivityId());
        QueryUtil.addNode(activityNode, "AutoScalingGroupName", act.getAutoScalingGroupName());
        QueryUtil.addNode(activityNode, "Cause", act.getCause());
        QueryUtil.addNode(activityNode, "Description", act.getDescription());
        QueryUtil.addNode(activityNode, "Details", act.getDetails());
        QueryUtil.addNode(activityNode, "EndTime", act.getEndTime());
        QueryUtil.addNode(activityNode, "Progress", act.getProgress());
        QueryUtil.addNode(activityNode, "StartTime", act.getStartTime());
        QueryUtil.addNode(activityNode, "StatusCode", act.getStatusCode());
        QueryUtil.addNode(activityNode, "StatusMessage", act.getStatusMessage());
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
    public TerminateInstanceInAutoScalingGroupRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final TerminateInstanceInAutoScalingGroupRequestMessage.Builder tReq =
    			TerminateInstanceInAutoScalingGroupRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	tReq.setInstanceId(QueryUtil.requiredString(in, "InstanceId"));
		tReq.setShouldDecrementDesiredCapacity(QueryUtil.requiredBoolean(in,
				"ShouldDecrementDesiredCapacity"));
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
    		TerminateInstanceInAutoScalingGroupResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
