package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;
import com.transcend.autoscale.message.DeleteAutoScalingGroupMessage.DeleteAutoScalingGroupRequestMessage;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DeleteAutoScalingGroupRequestUnmarshaller implements
		Unmarshaller<DeleteAutoScalingGroupRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DeleteAutoScalingGroupRequestUnmarshaller.class
					.getName());

	private static DeleteAutoScalingGroupRequestUnmarshaller instance;

	public static DeleteAutoScalingGroupRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteAutoScalingGroupRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DeleteAutoScalingGroupRequestMessage unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DeleteAutoScalingGroupRequestMessage.Builder req = DeleteAutoScalingGroupRequestMessage.newBuilder();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setForceDelete(QueryUtil.getBoolean(in, "ForceDelete"));
		return req.buildPartial();
	}
}
