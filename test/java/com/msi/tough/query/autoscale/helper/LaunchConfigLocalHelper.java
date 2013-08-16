package com.msi.tough.query.autoscale.helper;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ActionRequest;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.autoscale.actions.DeleteLaunchConfiguration;
import com.msi.tough.utils.ConfigurationUtil;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationRequestMessage;
import com.transcend.autoscale.worker.CreateLaunchConfigurationWorker;

/**
 * Account helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Scope("prototype")
@Component
public class LaunchConfigLocalHelper {

    private static Set<String> configs =
            new HashSet<String>();

    @Autowired(required=true)
    private ActionTestHelper actionHelper = null;

    @Resource
    CreateLaunchConfigurationWorker createLaunchConfigWorker = null;

    @Autowired(required=true)
    private String accessKey = null;

    private String defaultAvailabilityZone = null;

    public String getDefaultAvailabilityZone() {
		return defaultAvailabilityZone;
	}

    @Autowired(required=true)
	public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
		this.defaultAvailabilityZone = defaultAvailabilityZone;
	}

	private AccountBean account = null;

    /**
     * Construct a minimal valid launch config create request.
     *
     * @param groupName
     * @return
     */
    public CreateLaunchConfigurationRequestMessage createLaunchConfigRequest(String groupName) {
        final CreateLaunchConfigurationRequestMessage.Builder request =
                CreateLaunchConfigurationRequestMessage.newBuilder();
        if (account == null) {
            account = actionHelper.getAccountBeanByAccessKey(accessKey);
        }
        String imageId = (String) ConfigurationUtil.
                getConfiguration("ImageId", defaultAvailabilityZone);
        String instanceType = (String) ConfigurationUtil.
                getConfiguration("InstanceType", defaultAvailabilityZone);
        String kernelId = (String) ConfigurationUtil.
                getConfiguration("KernelId", defaultAvailabilityZone);
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setRequestId(groupName);
        request.setLaunchConfigurationName(groupName);
        request.setImageId(imageId);
        request.setInstanceType(instanceType);
    //   request.setKernelId("123456");
        if(kernelId != null){
        request.setKernelId(kernelId);
        }
        request.setKeyName(account.getDefKeyName());
        return request.build();
    }

    /**
     * Create a launch configuration.
     *
     * @param name
     */
    public void createLaunchConfig(String name) throws Exception {
        CreateLaunchConfigurationRequestMessage request =
                createLaunchConfigRequest(name);
        createLaunchConfigWorker.doWork(request);
        configs.add(name);
    }

    /**
     * Construct a delete request.
     *
     * @param account
     * @return request
     */
    public DeleteLaunchConfigRequest deleteLaunchConfigRequest(String name) {
        final DeleteLaunchConfigRequest request = new DeleteLaunchConfigRequest();
        request.put("LaunchConfigurationName", name);
        return request;
    }

    /**
     * Delete a config with the given name.
     *
     * @param name
     */
    public void deleteLaunchConfig(String name) throws Exception {
        DeleteLaunchConfigRequest request = deleteLaunchConfigRequest(name);
        DeleteLaunchConfiguration deleteGroup = new DeleteLaunchConfiguration();
        actionHelper.invokeProcess(deleteGroup,
                request.getRequest(), request.getResponse(),
                request.getMap());
        configs.remove(accessKey);
    }

    /**
     * Delete all configs created by tests (for test-end cleanup).
     */
    public void deleteAllCreatedConfigs() throws Exception {
        for (String name : configs) {
            deleteLaunchConfig(name);
        }
        configs.clear();
    }

    public static class CreateLaunchConfigRequest extends ActionRequest {
    }

    public static class DeleteLaunchConfigRequest extends ActionRequest {
    }

    public static class DescribeLaunchConfigsRequest extends ActionRequest {
        private int currentLaunchConfig = 0;
        public DescribeLaunchConfigsRequest withLaunchConfig(String name) {
            currentLaunchConfig++;
            put("LaunchConfigurationNames.member." + currentLaunchConfig, name);
            return this;
        }
    }
}
