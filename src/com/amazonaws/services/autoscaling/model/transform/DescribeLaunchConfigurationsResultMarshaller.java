package com.amazonaws.services.autoscaling.model.transform;

import org.slf4j.Logger;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsResultMessage;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsResultMessage.LaunchConfiguration;

/**
 * Create Load Balancer Request Marshaller
 */
public class DescribeLaunchConfigurationsResultMarshaller implements
		Marshaller<String, DescribeLaunchConfigurationsResultMessage> {
	private static Logger logger = Appctx
			.getLogger(DescribeLaunchConfigurationsResultMarshaller.class
					.getName());

	@Override
	public String marshall(
			final DescribeLaunchConfigurationsResultMessage message)
			throws ErrorResponse {
		final XMLNode xn = new XMLNode("DescribeLaunchConfigurationsResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());

		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeLaunchConfigurationsResult");
		QueryUtil.addNode(xr, "NextToken", message.getNextToken());
		final XMLNode ln = QueryUtil.addNode(xr, "LaunchConfigurations");
		for (final LaunchConfiguration en : message.getLaunchConfigurationsList()) {
			final XMLNode m = QueryUtil.addNode(ln, "member");
			marshallLaunchConfiguration(m, en);
		}
		return xn.toString();
	}
	
	public static void marshallLaunchConfiguration(final XMLNode n,
			final LaunchConfiguration r) {
		QueryUtil.addNode(n, "BlockDeviceMappings");
		// r.getBlockDeviceMappings();
		QueryUtil.addNode(n, "CreatedTime", r.getCreatedTime());
		QueryUtil.addNode(n, "ImageId", r.getImageId());
		QueryUtil.addNode(n, "InstanceMonitoring");
		// r.getInstanceMonitoring());
		QueryUtil.addNode(n, "InstanceType", r.getInstanceType());
		QueryUtil.addNode(n, "KernelId", r.getKernelId());
		QueryUtil.addNode(n, "KeyName", r.getKeyName());
		QueryUtil.addNode(n, "LaunchConfigurationARN",
				r.getLaunchConfigurationARN());
		QueryUtil.addNode(n, "LaunchConfigurationName",
				r.getLaunchConfigurationName());
		QueryUtil.addNode(n, "RamdiskId", r.getRamdiskId());
		final XMLNode sg = QueryUtil.addNode(n, "SecurityGroups");
		for (final String e : r.getSecurityGroupsList()) {
			QueryUtil.addNode(sg, "member", e);
		}
		QueryUtil.addNode(n, "UserData", r.getUserData());
	}
}
