package com.amazonaws.services.autoscaling.model.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DescribeAutoScalingGroupsRequestUnmarshallerVO implements
		Unmarshaller<DescribeAutoScalingGroupsRequest, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DescribeAutoScalingGroupsRequestUnmarshallerVO.class
					.getName());

	private static DescribeAutoScalingGroupsRequestUnmarshallerVO instance;

	public static DescribeAutoScalingGroupsRequestUnmarshallerVO getInstance() {
		if (instance == null) {
			instance = new DescribeAutoScalingGroupsRequestUnmarshallerVO();
		}
		return instance;
	}

	@Override
	public DescribeAutoScalingGroupsRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DescribeAutoScalingGroupsRequest req = new DescribeAutoScalingGroupsRequest();
		final List<String> autoScalingGroupNames = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (in.get("AutoScalingGroupNames.member." + i) == null) {
				break;
			}
			autoScalingGroupNames.add(in.get("AutoScalingGroupNames.member."
					+ i)[0]);
		}
		req.setAutoScalingGroupNames(autoScalingGroupNames);
		req.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		req.setNextToken(QueryUtil.getString(in, "NextToken"));
		return req;
	}
}
