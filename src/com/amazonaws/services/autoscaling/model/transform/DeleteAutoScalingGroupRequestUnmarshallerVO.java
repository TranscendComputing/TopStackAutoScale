package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DeleteAutoScalingGroupRequestUnmarshallerVO implements
		Unmarshaller<DeleteAutoScalingGroupRequest, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DeleteAutoScalingGroupRequestUnmarshallerVO.class
					.getName());

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
