package com.msi.tough.query.autoscale.helper;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.ActionTestHelper;

@Component
@Scope("prototype")
public class AutoScaleGroupHelper {

    private static Set<String> asGroups =
            new HashSet<String>();

    @Resource
    private ActionTestHelper actionTestHelper = null;

    @Resource
    private String defaultAvailabilityZone = null;

    @Resource
    LaunchConfigHelper launchConfigHelper = null;

    @Autowired
    private AmazonAutoScalingClient autoScaleClient;

    @Autowired
    private AmazonAutoScalingClient autoScaleClientV2;

    private static final int MAX_WAIT_MILLIS = 5000;
    private static final int DELAY = 500;

    /**
     * Construct a minimal valid autoscale request.
     *
     * @param groupName
     * @return
     */
    public CreateAutoScalingGroupRequest createASGroupRequest(String groupName) {
        final CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest();
        request.withAutoScalingGroupName(groupName);
        request.withAvailabilityZones(defaultAvailabilityZone);
        request.withMinSize(0);
        request.withMaxSize(1);
        return request;
    }

    /**
     * Create a group.
     *
     * @param groupName
     */
    public void createASGroup(String groupName) {
        CreateAutoScalingGroupRequest request = createASGroupRequest(groupName);
        launchConfigHelper.createLaunchConfig(groupName);
        request.withLaunchConfigurationName(groupName);
        autoScaleClientV2.createAutoScalingGroup(request);
        asGroups.add(groupName);
    }

    /**
     * Find a group (via describe).
     *
     * @param groupName
     */
    public AutoScalingGroup findASGroup(String groupName) {
        final DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest();
        request.withAutoScalingGroupNames(groupName);
        DescribeAutoScalingGroupsResult result =
                autoScaleClient.describeAutoScalingGroups(request);
        if (result.getAutoScalingGroups().size() > 0) {
            return result.getAutoScalingGroups().get(0);
        }
        return null;
    }
    /**
     * Construct a delete account request.
     *
     * @param userName
     * @return
     */
    public DeleteAutoScalingGroupRequest deleteASGroupRequest(String name) {
        final DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();
        request.withAutoScalingGroupName(name);
        return request;
    }

    /**
     * Delete an account with the given name.
     *
     * @param name
     */
    public void deleteASGroup(String name) throws Exception {
        DeleteAutoScalingGroupRequest request = deleteASGroupRequest(name);
        autoScaleClient.deleteAutoScalingGroup(request);
    }

    /**
     * Delete all accounts created by tests (for test-end cleanup).
     */
    public void deleteAllCreatedASGroups() throws Exception {
        for (String name : asGroups) {
            deleteASGroup(name);
        }
        // Delete is asynchronous.  We need to wait for delete to finish.
        int count = asGroups.size();
        int wait = 0;
        while (count > 0 && wait < MAX_WAIT_MILLIS) {
            Thread.sleep(DELAY);
            wait += DELAY;
            for (String name : asGroups) {
                if (findASGroup(name) == null) {
                    count--;
                }
            }
        }

        asGroups.clear();
        launchConfigHelper.deleteAllCreatedConfigs();
    }
}
