package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.query.QueryUtil;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationRequestMessage;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class CreateLaunchConfigurationRequestUnmarshaller implements
		Unmarshaller<CreateLaunchConfigurationRequestMessage, Map<String, String[]>> {
    //private static Logger logger = Appctx
    //.getLogger(CreateLaunchConfigurationRequestUnmarshaller.class
    //				.getName());

	private static CreateLaunchConfigurationRequestUnmarshaller instance;

	public static CreateLaunchConfigurationRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateLaunchConfigurationRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public CreateLaunchConfigurationRequestMessage unmarshall(
			final Map<String, String[]> in)  {
		final CreateLaunchConfigurationRequestMessage.Builder req = CreateLaunchConfigurationRequestMessage.newBuilder();
				//new CreateLaunchConfigurationRequest();
		// req.setBlockDeviceMappings(blockDeviceMappings);
		req.setImageId(QueryUtil.requiredString(in, "ImageId"));
		// req.setInstanceMonitoring(instanceMonitoring);
		req.setInstanceType(QueryUtil.requiredString(in, "InstanceType"));
		if(QueryUtil.getString(in, "KernelId")!=null){
			req.setKernelId(QueryUtil.getString(in, "KernelId"));
		}
		req.setKeyName(Strings.nullToEmpty(QueryUtil.getString(in, "KeyName")));
		req.setLaunchConfigurationName(QueryUtil.requiredString(in,
				"LaunchConfigurationName"));
		if(QueryUtil.getString(in, "RamdiskId")!=null){
			req.setRamdiskId(Strings.nullToEmpty(QueryUtil.getString(in, "RamdiskId")));
		}
		if(req.getSecurityGroupsCount() > 0){
			req.addAllSecurityGroups(QueryUtil.getStringArray(in,
					"SecurityGroups.member", Integer.MAX_VALUE));
		}
		if(QueryUtil.getString(in, "UserData")!=null){
			req.setUserData(QueryUtil.getString(in, "UserData"));
		}

		return req.buildPartial();
	}
}
