package com.transcend.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.google.common.base.Strings;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DescribeAutoScalingInstancesMessage.DescribeAutoScalingInstancesRequestMessage;
import com.transcend.autoscale.message.DescribeAutoScalingInstancesMessage.DescribeAutoScalingInstancesResultMessage;
import com.transcend.autoscale.message.DescribeAutoScalingInstancesMessage.DescribeAutoScalingInstancesResultMessage.AutoScalingInstanceDetails;
import com.yammer.metrics.core.Meter;

public class DescribeAutoScalingInstances
        extends
        AbstractQueuedAction<DescribeAutoScalingInstancesRequestMessage, DescribeAutoScalingInstancesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeAutoScalingInstances");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeAutoScalingInstancesResultMessage message) {
		final XMLNode xn = new XMLNode("DescribeAutoScalingInstancesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeAutoScalingInstancesResult");
		QueryUtil.addNode(xn, "NextToken", message.getNextToken());
		final XMLNode ln = QueryUtil.addNode(xr, "AutoScalingInstances");
		for (final AutoScalingInstanceDetails en : message.getAutoScalingInstancesList()) {
			final XMLNode m = QueryUtil.addNode(ln, "member");
			marshallAutoScalingInstances(m, en);
		}

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());
		return xn.toString();
    }
    
	public static void marshallAutoScalingInstances(final XMLNode m,
			final AutoScalingInstanceDetails en) {
		QueryUtil.addNode(m, "AutoScalingGroupName",
				en.getAutoScalingGroupName());
		QueryUtil.addNode(m, "AvailabilityZone", en.getAvailabilityZone());
		QueryUtil.addNode(m, "HealthStatus", en.getHealthStatus());
		QueryUtil.addNode(m, "InstanceId", en.getInstanceId());
		QueryUtil.addNode(m, "LaunchConfigurationName",
				en.getLaunchConfigurationName());
		QueryUtil.addNode(m, "LifecycleState", en.getLifecycleState());
	}

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    
    @Override
    public DescribeAutoScalingInstancesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
    	
    	final DescribeAutoScalingInstancesRequestMessage.Builder tReq =
    			DescribeAutoScalingInstancesRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	
		tReq.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		tReq.setNextToken(Strings.nullToEmpty(QueryUtil.getString(in, "NextToken")));
		final Collection<String> ids = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("InstanceIds.member." + i)) {
				break;
			}
			ids.add(QueryUtil.getString(in, "InstanceIds.member." + i));
		}
		tReq.addAllInstanceIds(ids);
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
    		DescribeAutoScalingInstancesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
