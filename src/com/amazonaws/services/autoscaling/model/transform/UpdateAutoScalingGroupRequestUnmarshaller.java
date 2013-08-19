package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;
import com.transcend.autoscale.message.UpdateAutoScalingGroupMessage.UpdateAutoScalingGroupRequestMessage;


/**
 * UpdateLoadBalancerRequestUnmarshaller
 */
public class UpdateAutoScalingGroupRequestUnmarshaller implements
		Unmarshaller<UpdateAutoScalingGroupRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(UpdateAutoScalingGroupRequestUnmarshaller.class
					.getName());

	private static UpdateAutoScalingGroupRequestUnmarshaller instance;

	public static UpdateAutoScalingGroupRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new UpdateAutoScalingGroupRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public UpdateAutoScalingGroupRequestMessage unmarshall(
			final Map<String, String[]> in) {

		final UpdateAutoScalingGroupRequestMessage.Builder req =
		        UpdateAutoScalingGroupRequestMessage.newBuilder();

		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));

		req.addAllAvailabilityZone(QueryUtil.getStringArray(in,
		        "AvailabilityZone.member", Integer.MAX_VALUE));

		req.setDefaultCooldown(QueryUtil.getInt(in, "DefaultCooldown"));
		req.setDesiredCapacity(QueryUtil.getInt(in, "DesiredCapacity"));
		req.setHealthCheckGracePeriod(QueryUtil.getInt(in,
				"HealthCheckGracePeriod"));
		req.setHealthCheckType(Strings.nullToEmpty(QueryUtil.getString(in, "HealthCheckType")));
		req.setLaunchConfigurationName(Strings.nullToEmpty(QueryUtil.getString(in,
				"LaunchConfigurationName")));
		req.setMaxSize(QueryUtil.getInt(in, "MaxSize"));
		req.setMinSize(QueryUtil.getInt(in, "MinSize"));
		// req.setPlacementGroup(placementGroup);
		// req.setVPCZoneIdentifier(vPCZoneIdentifier);
		return req.buildPartial();
	}
}
