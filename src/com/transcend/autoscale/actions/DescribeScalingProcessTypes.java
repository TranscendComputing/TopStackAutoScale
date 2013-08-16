package com.transcend.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.google.common.base.Strings;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.transcend.autoscale.message.DescribeScalingProcessTypesMessage.DescribeScalingProcessTypesRequestMessage;
import com.transcend.autoscale.message.DescribeScalingProcessTypesMessage.DescribeScalingProcessTypesResultMessage;
import com.transcend.autoscale.message.DescribeScalingProcessTypesMessage.DescribeScalingProcessTypesRequestMessage;
import com.yammer.metrics.core.Meter;

public class DescribeScalingProcessTypes
        extends
        AbstractQueuedAction<DescribeScalingProcessTypesRequestMessage, DescribeScalingProcessTypesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeScalingProcessTypes");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeScalingProcessTypesResultMessage message) {
		final XMLNode xn = new XMLNode("DescribeScalingProcessTypesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeScalingProcessTypesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());

		final XMLNode types = QueryUtil.addNode(xr, "Processes");
		QueryUtil.addNode(types, "ProcessType", "AddToLoadBalancer");
		QueryUtil.addNode(types, "ProcessType", "AlarmNotification");
		QueryUtil.addNode(types, "ProcessType", "AZRebalance");
		QueryUtil.addNode(types, "ProcessType", "HealthCheck");
		QueryUtil.addNode(types, "ProcessType", "ReplaceUnhealthy");
		QueryUtil.addNode(types, "ProcessType", "ScheduledActions");
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
    public DescribeScalingProcessTypesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final DescribeScalingProcessTypesRequestMessage.Builder tReq =
    			DescribeScalingProcessTypesRequestMessage.newBuilder();
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
    		DescribeScalingProcessTypesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
