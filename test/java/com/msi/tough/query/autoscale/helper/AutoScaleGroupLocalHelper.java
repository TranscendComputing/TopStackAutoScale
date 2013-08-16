package com.msi.tough.query.autoscale.helper;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.msi.tough.query.ActionRequest;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.autoscale.actions.DeleteAutoScalingGroup;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupRequestMessage;
import com.transcend.autoscale.worker.CreateAutoScalingGroupWorker;

/**
 * Account helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
public class AutoScaleGroupLocalHelper {

    private Set<String> asGroups =
            new HashSet<String>();

    @Resource
    private ActionTestHelper actionTestHelper = null;

    @Resource
    private String defaultAvailabilityZone = null;

    @Resource
    LaunchConfigLocalHelper launchConfigLocalHelper = null;

    @Resource
    CreateAutoScalingGroupWorker asGroupWorker = null;

    /**
     * Construct a minimal valid account request.
     *
     * @param groupName
     * @return
     */
    public CreateAutoScalingGroupRequestMessage.Builder createASGroupRequest(String groupName) {
        final CreateAutoScalingGroupRequestMessage.Builder request =
                CreateAutoScalingGroupRequestMessage.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionTestHelper.getAccessKey());
        request.setRequestId(groupName);
        request.setAutoScalingGroupName(groupName);
        request.addAvailabilityZone(defaultAvailabilityZone);
        request.setMinSize(0);
        request.setMaxSize(1);
        return request;
    }

    /**
     * Create a group.
     *
     * @param groupName
     */
    public void createASGroup(String groupName) throws Exception {
        CreateAutoScalingGroupRequestMessage.Builder request =
                createASGroupRequest(groupName);
        launchConfigLocalHelper.createLaunchConfig(groupName);
        request.setLaunchConfigurationName(groupName);
        asGroupWorker.doWork(request.build());
        asGroups.add(groupName);
    }

    /**
     * Construct a delete account request.
     *
     * @param userName
     * @return
     */
    public DeleteAutoScalingGroupRequest deleteASGroupRequest(String name) {
        final DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();
        request.put("AutoScalingGroupName", name);
        return request;
    }

    /**
     * Delete an account with the given name.
     *
     * @param name
     */
    public void deleteASGroup(String name) throws Exception {
        DeleteAutoScalingGroupRequest request = deleteASGroupRequest(name);
        DeleteAutoScalingGroup deleteGroup = new DeleteAutoScalingGroup();
        actionTestHelper.invokeProcess(deleteGroup,
                request.getRequest(), request.getResponse(),
                request.getMap());
    }

    /**
     * Delete all accounts created by tests (for test-end cleanup).
     */
    public void deleteAllCreatedASGroups() throws Exception {
        for (String name : asGroups) {
            deleteASGroup(name);
        }
        asGroups.clear();
        launchConfigLocalHelper.deleteAllCreatedConfigs();
    }

    public static class DeleteAutoScalingGroupRequest extends ActionRequest {
    }
}
