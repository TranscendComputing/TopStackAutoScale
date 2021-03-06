package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DeleteAutoScalingGroupRequestUnmarshallerVO implements
		Unmarshaller<DeleteAutoScalingGroupRequest, Map<String, String[]>> {

	private static DeleteAutoScalingGroupRequestUnmarshallerVO instance;

	public static DeleteAutoScalingGroupRequestUnmarshallerVO getInstance() {
		if (instance == null) {
			instance = new DeleteAutoScalingGroupRequestUnmarshallerVO();
		}
		return instance;
	}

	@Override
	public DeleteAutoScalingGroupRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DeleteAutoScalingGroupRequest req = new DeleteAutoScalingGroupRequest();
		req.setAutoScalingGroupName(QueryUtil.getString(in,
				"AutoScalingGroupName"));
		return req;
	}
}
