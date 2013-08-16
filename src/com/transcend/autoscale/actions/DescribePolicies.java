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
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesRequestMessage;
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesResultMessage;
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesResultMessage.ScalingPolicy;
import com.yammer.metrics.core.Meter;

public class DescribePolicies
        extends
        AbstractQueuedAction<DescribePoliciesRequestMessage, DescribePoliciesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribePolicies");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribePoliciesResultMessage message) {
		final XMLNode xn = new XMLNode("DescribePoliciesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "DescribePoliciesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());
		final XMLNode xpolicies = QueryUtil.addNode(xr, "ScalingPolicies");
		final List<ScalingPolicy> policies = message.getScalingPoliciesList();
											
		if (policies != null) {
			for (final ScalingPolicy i : policies) {
				final XMLNode xpolicy = QueryUtil.addNode(xpolicies, "member");
				marshallAutoPolicy(xpolicy, i);
			}
		}
		return xn.toString();
    }
    
	public static void marshallAutoPolicy(final XMLNode xpolicy,
			final ScalingPolicy i) {
		QueryUtil.addNode(xpolicy, "PolicyARN", i.getPolicyARN());
		QueryUtil.addNode(xpolicy, "AdjustmentType", i.getAdjustmentType());
		QueryUtil.addNode(xpolicy, "AutoScalingGroupName",
				i.getAutoScalingGroupName());
		QueryUtil.addNode(xpolicy, "Cooldown", i.getCooldown());
		QueryUtil.addNode(xpolicy, "MinAdjustmentStep",
				i.getMinAdjustmentStep());
		QueryUtil.addNode(xpolicy, "PolicyName", i.getPolicyName());
		QueryUtil.addNode(xpolicy, "ScalingAdjustment",
				i.getScalingAdjustment());
	}

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    
    @Override
    public DescribePoliciesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final DescribePoliciesRequestMessage.Builder tReq =
    			DescribePoliciesRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	
		tReq.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		tReq.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		tReq.setNextToken(Strings.nullToEmpty(QueryUtil.getString(in, "NextToken")));
		final Collection<String> policyNames = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("PolicyNames.member." + i)) {
				break;
			}
			policyNames.add(QueryUtil.getString(in, "PolicyNames.member." + i));
		}
		tReq.addAllPolicyNames(policyNames);
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
    		DescribePoliciesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
