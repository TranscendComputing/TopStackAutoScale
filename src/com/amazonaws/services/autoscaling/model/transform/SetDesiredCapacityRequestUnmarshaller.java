package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.SetDesiredCapacityRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class SetDesiredCapacityRequestUnmarshaller implements
		Unmarshaller<SetDesiredCapacityRequest, Map<String, String[]>> {

	private static SetDesiredCapacityRequestUnmarshaller instance;

	public static SetDesiredCapacityRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new SetDesiredCapacityRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public SetDesiredCapacityRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final SetDesiredCapacityRequest req = new SetDesiredCapacityRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setDesiredCapacity(QueryUtil.requiredInt(in, "DesiredCapacity"));
		req.setHonorCooldown(QueryUtil.getBoolean(in, "HonorCooldown"));
		return req;
	}
}
