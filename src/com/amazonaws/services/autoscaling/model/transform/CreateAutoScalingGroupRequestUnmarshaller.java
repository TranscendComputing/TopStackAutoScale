package com.amazonaws.services.autoscaling.model.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.query.QueryUtil;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupRequestMessage;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class CreateAutoScalingGroupRequestUnmarshaller implements
		Unmarshaller<CreateAutoScalingGroupRequestMessage, Map<String, String[]>> {

	private static CreateAutoScalingGroupRequestUnmarshaller instance;

	public static CreateAutoScalingGroupRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateAutoScalingGroupRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public CreateAutoScalingGroupRequestMessage unmarshall(
			final Map<String, String[]> in) {
		final CreateAutoScalingGroupRequestMessage.Builder req =
		        CreateAutoScalingGroupRequestMessage.newBuilder();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.addAllAvailabilityZone(QueryUtil.requiredStringArray(in,
		        "AvailabilityZones.member", Integer.MAX_VALUE));
		req.setDefaultCooldown(QueryUtil.getInt(in, "DefaultCooldown"));
		req.setDesiredCapacity(QueryUtil.getInt(in, "DesiredCapacity"));
		req.setHealthCheckGracePeriod(QueryUtil.getInt(in,
				"HealthCheckGracePeriod"));
		req.setHealthCheckType(Strings.nullToEmpty(QueryUtil.getString(in, "HealthCheckType")));
		req.setLaunchConfigurationName(QueryUtil.requiredString(in,
				"LaunchConfigurationName"));
		req.addAllLoadBalancerName(QueryUtil.getStringArray(in,
		        "LoadBalancerNames.member", Integer.MAX_VALUE));
		req.setMaxSize(QueryUtil.requiredInt(in, "MaxSize"));
		req.setMinSize(QueryUtil.requiredInt(in, "MinSize"));
		// req.setPlacementGroup(placementGroup);
		final Collection<String> terminationPolicies = new ArrayList<String>();
		for (int i = 0;; i++) {
			if (in.get("TerminationPolicies.member." + i) == null) {
				break;
			}
			terminationPolicies
					.add(in.get("TerminationPolicies.member." + i)[0]);
		}
		req.addAllTerminationPolicy(terminationPolicies);
		// req.setVPCZoneIdentifier(vPCZoneIdentifier);
		return req.buildPartial();
	}
}
