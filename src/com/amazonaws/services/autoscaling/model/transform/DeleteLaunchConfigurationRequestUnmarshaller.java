package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * DeleteLaunchConfigurationRequestUnmarshaller
 */
public class DeleteLaunchConfigurationRequestUnmarshaller implements
		Unmarshaller<DeleteLaunchConfigurationRequest, Map<String, String[]>> {
    //private static Logger logger = Appctx
    //	.getLogger(DeleteLaunchConfigurationRequestUnmarshaller.class
    //			.getName());

	private static DeleteLaunchConfigurationRequestUnmarshaller instance;

	public static DeleteLaunchConfigurationRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteLaunchConfigurationRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DeleteLaunchConfigurationRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DeleteLaunchConfigurationRequest req = new DeleteLaunchConfigurationRequest();
		req.setLaunchConfigurationName(QueryUtil.requiredString(in,
				"LaunchConfigurationName"));
		return req;
	}
}
