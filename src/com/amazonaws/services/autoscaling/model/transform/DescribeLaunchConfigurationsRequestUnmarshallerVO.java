package com.amazonaws.services.autoscaling.model.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DescribeLaunchConfigurationsRequestUnmarshallerVO
		implements
		Unmarshaller<DescribeLaunchConfigurationsRequest, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DescribeLaunchConfigurationsRequestUnmarshallerVO.class
					.getName());

	private static DescribeLaunchConfigurationsRequestUnmarshallerVO instance;

	public static DescribeLaunchConfigurationsRequestUnmarshallerVO getInstance() {
		if (instance == null) {
			instance = new DescribeLaunchConfigurationsRequestUnmarshallerVO();
		}
		return instance;
	}

	@Override
	public DescribeLaunchConfigurationsRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DescribeLaunchConfigurationsRequest req = new DescribeLaunchConfigurationsRequest();
		final List<String> launchConfigurationNames = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (in.get("LaunchConfigurationNames.member." + i) == null) {
				break;
			}
			launchConfigurationNames.add(in
					.get("LaunchConfigurationNames.member." + i)[0]);
		}
		req.setLaunchConfigurationNames(launchConfigurationNames);
		req.setMaxRecords(QueryUtil.getIntObject(in, "MaxRecords"));
		req.setNextToken(QueryUtil.getString(in, "NextToken"));
		return req;
	}
}
