package com.transcend.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.autoscaling.model.DescribeScalingActivitiesRequest;
import com.generationjava.io.xml.XMLNode;
import com.google.common.base.Strings;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DescribeScalingActivitiesMessage.DescribeScalingActivitiesRequestMessage;
import com.transcend.autoscale.message.DescribeScalingActivitiesMessage.DescribeScalingActivitiesResultMessage;
import com.transcend.autoscale.message.DescribeScalingActivitiesMessage.DescribeScalingActivitiesResultMessage.Activity;
import com.transcend.autoscale.message.ExecutePolicyMessage.ExecutePolicyRequestMessage;
import com.yammer.metrics.core.Meter;

public class DescribeScalingActivities
        extends
        AbstractQueuedAction<DescribeScalingActivitiesRequestMessage, DescribeScalingActivitiesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeScalingActivities");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeScalingActivitiesResultMessage message) {
    	final XMLNode xn = new XMLNode("DescribeScalingActivitiesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeScalingActivitiesResult");
		final XMLNode na = QueryUtil.addNode(xr, "Activities");

		final List<Activity> actl = message.getActivitiesList();
		for (final Activity a : actl) {
			final XMLNode m = QueryUtil.addNode(na, "member");
			QueryUtil.addNode(m, "ActivityId", a.getActivityId());
			QueryUtil.addNode(m, "AutoScalingGroupName",
					a.getAutoScalingGroupName());
			QueryUtil.addNode(m, "Cause", a.getCause());
			QueryUtil.addNode(m, "Description", a.getDescription());
			QueryUtil.addNode(m, "Details", a.getDetails());
			QueryUtil.addNode(m, "EndTime", a.getEndTime());
			QueryUtil.addNode(m, "Progress", a.getProgress());
			QueryUtil.addNode(m, "StartTime", a.getStartTime());
			QueryUtil.addNode(m, "StatusCode", a.getStatusCode());
			QueryUtil.addNode(m, "StatusMessage", a.getStatusMessage());
		}

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
    public DescribeScalingActivitiesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
    	
		final DescribeScalingActivitiesRequestMessage.Builder tReq = DescribeScalingActivitiesRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
		tReq.setAutoScalingGroupName(Strings.nullToEmpty(QueryUtil.getString(in,
				"AutoScalingGroupName")));
		tReq.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		tReq.setNextToken(Strings.nullToEmpty(QueryUtil.getString(in, "NextToken")));
		final Collection<String> ids = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("ActivityIds.member." + i)) {
				break;
			}
			ids.add(QueryUtil.getString(in, "ActivityIds.member." + i));
		}
		tReq.addAllActivityIds(ids);
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
    		DescribeScalingActivitiesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
