package com.amazonaws.services.autoscaling.model.transform;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;

/**
 * Create Load Balancer Request Marshaller
 */
public class DescribeAutoScalingGroupsResultMarshallerVO implements
		Marshaller<String, MarshallStruct<DescribeAutoScalingGroupsResult>> {

	@Override
	public String marshall(
			final MarshallStruct<DescribeAutoScalingGroupsResult> input)
			throws Exception {
		final XMLNode xn = new XMLNode("DescribeAutoScalingGroupsResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeAutoScalingGroupsResult");
		final DescribeAutoScalingGroupsResult o = input.getMainObject();
		QueryUtil.addNode(xn, "NextToken", o.getNextToken());
		final XMLNode ln = QueryUtil.addNode(xr, "AutoScalingGroups");
		for (final AutoScalingGroup en : o.getAutoScalingGroups()) {
			final XMLNode m = QueryUtil.addNode(ln, "member");
			AutoScaleQueryUtil.marshallAutoScalingGroup(m, en);
		}
		return xn.toString();
	}
}
