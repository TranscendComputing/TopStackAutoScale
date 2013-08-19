package com.amazonaws.services.autoscaling.model.transform;

import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsResult;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;

/**
 * Create Load Balancer Request Marshaller
 */
public class DescribeLaunchConfigurationsResultMarshallerVO implements
		Marshaller<String, MarshallStruct<DescribeLaunchConfigurationsResult>> {

	@Override
	public String marshall(
			final MarshallStruct<DescribeLaunchConfigurationsResult> input)
			throws Exception {
		final XMLNode xn = new XMLNode("DescribeLaunchConfigurationsResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeLaunchConfigurationsResult");
		final DescribeLaunchConfigurationsResult o = input.getMainObject();
		QueryUtil.addNode(xr, "NextToken", o.getNextToken());
		final XMLNode ln = QueryUtil.addNode(xr, "LaunchConfigurations");
		for (final LaunchConfiguration en : o.getLaunchConfigurations()) {
			final XMLNode m = QueryUtil.addNode(ln, "member");
			AutoScaleQueryUtil.marshallLaunchConfiguration(m, en);
		}
		return xn.toString();
	}
}
