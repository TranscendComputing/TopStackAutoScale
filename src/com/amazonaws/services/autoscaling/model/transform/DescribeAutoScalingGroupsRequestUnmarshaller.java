package com.amazonaws.services.autoscaling.model.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsRequestMessage;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DescribeAutoScalingGroupsRequestUnmarshaller implements
		Unmarshaller<DescribeAutoScalingGroupsRequestMessage, ServiceRequest> {

	private static DescribeAutoScalingGroupsRequestUnmarshaller instance;

	public static DescribeAutoScalingGroupsRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeAutoScalingGroupsRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DescribeAutoScalingGroupsRequestMessage unmarshall(
			ServiceRequest req) throws Exception {
		final List<String> autoScalingGroupNames = new ArrayList<String>();
    	final Map<String, String[]> in = req.getParameterMap();
    	final DescribeAutoScalingGroupsRequestMessage.Builder tReq =
    			DescribeAutoScalingGroupsRequestMessage.newBuilder();
		for (int i = 1;; i++) {
			if (in.get("AutoScalingGroupNames.member." + i) == null) {
				break;
			}
			autoScalingGroupNames.add(in.get("AutoScalingGroupNames.member."
					+ i)[0]);
		}
		tReq.addAllAutoScalingGroupNames(autoScalingGroupNames);
		tReq.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		tReq.setNextToken(Strings.nullToEmpty(QueryUtil.getString(in, "NextToken")));
		return tReq.buildPartial();
	}
}
