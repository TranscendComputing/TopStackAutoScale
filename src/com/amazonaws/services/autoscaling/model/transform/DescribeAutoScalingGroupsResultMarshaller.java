package com.amazonaws.services.autoscaling.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage.AutoScalingGroup;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage.Instance;

/**
 * Create Load Balancer Request Marshaller
 */
public class DescribeAutoScalingGroupsResultMarshaller implements
		Marshaller<String, DescribeAutoScalingGroupsResultMessage> {

	@Override
	public String marshall(
			final DescribeAutoScalingGroupsResultMessage message)
			throws ErrorResponse {
		final XMLNode xn = new XMLNode("DescribeAutoScalingGroupsResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());

		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeAutoScalingGroupsResult");
		//final DescribeAutoScalingGroupsResult o = input.getMainObject();
		QueryUtil.addNode(xn, "NextToken", message.getNextToken());
		final XMLNode ln = QueryUtil.addNode(xr, "AutoScalingGroups");
		for (final AutoScalingGroup en : message.getAutoScalingGroupsList()) {
			final XMLNode m = QueryUtil.addNode(ln, "member");
			marshallAutoScalingGroup(m, en);
		}
		return xn.toString();
	}


	public static void marshallAutoScalingGroup(final XMLNode m,
			final AutoScalingGroup en) {

		QueryUtil
				.addNode(m, "AutoScalingGroupARN", en.getAutoScalingGroupARN());
		QueryUtil.addNode(m, "AutoScalingGroupName",
				en.getAutoScalingGroupName());
		final XMLNode az = QueryUtil.addNode(m, "AvailabilityZones");
		for (final String e : en.getAvailabilityZonesList()) {
			QueryUtil.addNode(az, "member", e);
		}
		QueryUtil.addNode(m, "CreatedTime", en.getCreatedTime());
		QueryUtil.addNode(m, "DefaultCooldown", en.getDefaultCooldown());
		QueryUtil.addNode(m, "DesiredCapacity", en.getDesiredCapacity());
		// QueryUtil.addNode(m, "EnabledMetrics", en.getEnabledMetrics());
		QueryUtil.addNode(m, "HealthCheckGracePeriod",
				en.getHealthCheckGracePeriod());
		QueryUtil.addNode(m, "HealthCheckType", en.getHealthCheckType());
		final XMLNode inst = QueryUtil.addNode(m, "Instances");
		for (final Instance e : en.getInstancesList()) {
			final XMLNode mem = QueryUtil.addNode(inst, "member");
			QueryUtil.addNode(mem, "AvailabilityZone", e.getAvailabilityZone());
			QueryUtil.addNode(mem, "HealthStatus", e.getHealthStatus());
			QueryUtil.addNode(mem, "InstanceId", e.getInstanceId());
			QueryUtil.addNode(mem, "LaunchConfigurationName",
					e.getLaunchConfigurationName());
			QueryUtil.addNode(mem, "LifecycleState", e.getLifecycleState());
		}
		QueryUtil.addNode(m, "LaunchConfigurationName",
				en.getLaunchConfigurationName());
		final XMLNode lb = QueryUtil.addNode(m, "LoadBalancerNames");
		for (final String e : en.getLoadBalancerNamesList()) {
			QueryUtil.addNode(lb, "member", e);
		}
		QueryUtil.addNode(m, "MaxSize", en.getMaxSize());
		QueryUtil.addNode(m, "MinSize", en.getMinSize());
		QueryUtil.addNode(m, "PlacementGroup", en.getPlacementGroup());
		// final XMLNode sp = QueryUtil.addNode(m, "SuspendedProcesses");
		// en.getSuspendedProcesses());
		QueryUtil.addNode(m, "VPCZoneIdentifier", en.getVpcZoneIdentifier());
	}
}
