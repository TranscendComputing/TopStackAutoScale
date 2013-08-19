package com.amazonaws.services.autoscaling.model.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsRequestMessage;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DescribeLaunchConfigurationsRequestUnmarshaller
		implements
		Unmarshaller<DescribeLaunchConfigurationsRequestMessage, ServiceRequest> {

	private static DescribeLaunchConfigurationsRequestUnmarshaller instance;

	public static DescribeLaunchConfigurationsRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeLaunchConfigurationsRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DescribeLaunchConfigurationsRequestMessage unmarshall(
			final ServiceRequest req) throws Exception {
    	final Map<String, String[]> in = req.getParameterMap();
    	final DescribeLaunchConfigurationsRequestMessage.Builder tReq =
    			DescribeLaunchConfigurationsRequestMessage.newBuilder();
    	final List<String> launchConfigurationNames = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (in.get("LaunchConfigurationNames.member." + i) == null) {
				break;
			}
			launchConfigurationNames.add(in
					.get("LaunchConfigurationNames.member." + i)[0]);
		}
		tReq.addAllLaunchConfigurationNames(launchConfigurationNames);
		tReq.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		tReq.setNextToken(Strings.nullToEmpty(QueryUtil.getString(in, "NextToken")));
		return tReq.buildPartial();
	}
}