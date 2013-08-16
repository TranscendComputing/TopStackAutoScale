package com.msi.tough.query.autoscale.helper;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.msi.tough.query.ActionRequest;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.autoscale.actions.DeletePolicy;
import com.msi.tough.query.autoscale.actions.PutScalingPolicy;

/**
 * Account helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
public class ScalingPolicyLocalHelper {

    private Set<String> policies =
            new HashSet<String>();

    @Resource
    private ActionTestHelper actionTestHelper = null;

    @Resource
    private String defaultAvailabilityZone = null;

    @Resource
    AutoScaleGroupLocalHelper asGroupHelper = null;

    /**
     * Construct a minimal valid account request.
     *
     * @param groupName
     * @return
     */
    public PutScalingPolicyRequest putPolicyRequest(String name) {
        final PutScalingPolicyRequest request = new PutScalingPolicyRequest();
        request.put("AdjustmentType", "ChangeInCapacity");
        request.put("PolicyName", name);
        request.withAvZone(defaultAvailabilityZone);
        request.put("ScalingAdjustment", 1);
        return request;
    }

    /**
     * Create a user account.
     *
     * @param userName
     */
    public void putPolicy(String name) throws Exception {
        PutScalingPolicyRequest request = putPolicyRequest(name);
        asGroupHelper.createASGroup(name);
        request.put("AutoScalingGroupName", name);
        PutScalingPolicy putPolicy = new PutScalingPolicy();
        actionTestHelper.invokeProcess(putPolicy,
                request.getRequest(), request.getResponse(),
                request.getMap());
        policies.add(name);
    }

    /**
     * Construct a delete account request.
     *
     * @param userName
     * @return
     */
    public DeletePolicyRequest deletePolicyRequest(String name) {
        final DeletePolicyRequest request = new DeletePolicyRequest();
        request.put("PolicyName", name);
        // Generally, we create a group with same name as the policy for test.
        request.put("AutoScalingGroupName", name);
        return request;
    }

    /**
     * Delete an account with the given name.
     *
     * @param name
     */
    public void deletePolicy(String name) throws Exception {
        DeletePolicyRequest request = deletePolicyRequest(name);
        DeletePolicy deletePolicy = new DeletePolicy();
        actionTestHelper.invokeProcess(deletePolicy,
                request.getRequest(), request.getResponse(),
                request.getMap());
    }

    /**
     * Delete all accounts created by tests (for test-end cleanup).
     */
    public void deleteAllCreatedPolicies() throws Exception {
        for (String name : policies) {
            deletePolicy(name);
        }
        policies.clear();
        asGroupHelper.deleteAllCreatedASGroups();
    }

    public static class PutScalingPolicyRequest extends ActionRequest {
        private int currentAvZone = 0;

       public PutScalingPolicyRequest withAvZone(String name) {
           currentAvZone++;
           put("AvailabilityZones.member." + currentAvZone, name);
           return this;
       }
    }

    public static class DeletePolicyRequest extends ActionRequest {
    }
}
