package com.msi.tough.query.autoscale.helper;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.utils.ConfigurationUtil;

/**
 * Account helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
@Scope("prototype")
public class LaunchConfigHelper {

    private static Set<String> configs =
            new HashSet<String>();

    @Autowired(required=true)
    private ActionTestHelper actionHelper = null;

    @Autowired(required=true)
    private String accessKey = null;

    @Autowired(required=true)
    private String defaultAvailabilityZone = null;

    @Autowired
    private AmazonAutoScalingClient autoScaleClient;
    
    @Autowired
    private AmazonAutoScalingClient autoScaleClientV2;

    private AccountBean account = null;

    /**
     * Construct a minimal valid launch config create request.
     *
     * @param groupName
     * @return
     */
    public CreateLaunchConfigurationRequest createLaunchConfigRequest(String groupName) {
        final CreateLaunchConfigurationRequest request =
                new CreateLaunchConfigurationRequest();
        if (account == null) {
            account = actionHelper.getAccountBeanByAccessKey(accessKey);
        }
        String imageId = (String) ConfigurationUtil.
                getConfiguration("ImageId", defaultAvailabilityZone);
        String instanceType = (String) ConfigurationUtil.
                getConfiguration("InstanceType", defaultAvailabilityZone);
        String kernelId = (String) ConfigurationUtil.
                getConfiguration("KernelId", defaultAvailabilityZone);
        request.withLaunchConfigurationName(groupName);
        request.withImageId(imageId);
        request.withInstanceType(instanceType);
      
        request.withKernelId(kernelId);
        request.withKeyName(account.getDefKeyName());
        return request;
    }

    /**
     * Create a launch configuration.
     *
     * @param name
     */
    public void createLaunchConfig(String name) {
        CreateLaunchConfigurationRequest request = createLaunchConfigRequest(name);
        autoScaleClientV2.createLaunchConfiguration(request);
        configs.add(name);
    }

    /**
     * Construct a delete request.
     *
     * @param account
     * @return request
     */
    public DeleteLaunchConfigurationRequest deleteLaunchConfigRequest(String name) {
        final DeleteLaunchConfigurationRequest request =
                new DeleteLaunchConfigurationRequest();
        request.withLaunchConfigurationName(name);
        return request;
    }

    /**
     * Delete a config with the given name.
     *
     * @param name
     */
    public void deleteLaunchConfig(String name) throws Exception {
        DeleteLaunchConfigurationRequest request = deleteLaunchConfigRequest(name);
        autoScaleClient.deleteLaunchConfiguration(request);
        configs.remove(name);
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
}
