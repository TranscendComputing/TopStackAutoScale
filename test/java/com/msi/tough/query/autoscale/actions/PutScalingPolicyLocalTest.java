package com.msi.tough.query.autoscale.actions;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupLocalHelper;
import com.msi.tough.query.autoscale.helper.ScalingPolicyLocalHelper;
import com.msi.tough.query.autoscale.helper.ScalingPolicyLocalHelper.PutScalingPolicyRequest;

/**
 * Test put scaling locally.
 *
 * @author jgardner
 *
 */
@Ignore
public class PutScalingPolicyLocalTest extends AbstractBaseAutoscaleTest {

	private final String baseName = UUID.randomUUID().toString()
			.substring(0, 8);

	String existingPolicy = "crPolLoc:1:" + baseName;
	String newPolicy = "crPolLoc:2:" + baseName;

	@Resource
	private final ActionTestHelper actionTestHelper = null;

	@Resource
	ScalingPolicyLocalHelper policyHelper = null;

	@Resource
	AutoScaleGroupLocalHelper asGroupHelper = null;

	@After
	@Transactional
	public void cleanupCreated() throws Exception {
		policyHelper.deleteAllCreatedPolicies();
		asGroupHelper.deleteAllCreatedASGroups();
	}

	@Test
	@Ignore
	public void createOneOffPolicy() throws Exception {
		final String groupName = "my-policy-group";
		final String policyName = "my-policy";
		final PutScalingPolicyRequest request = policyHelper
				.putPolicyRequest(policyName);
		try {
			asGroupHelper.createASGroup(groupName);
		} catch (final ErrorResponse e) {
			if (!"AlreadyExists".equals(e.getCode())) {
				throw e;
			}
		}
		request.put("AutoScalingGroupName", groupName);
		final PutScalingPolicy putPolicy = new PutScalingPolicy();
		actionTestHelper.invokeProcess(putPolicy, request.getRequest(),
				request.getResponse(), request.getMap());
	}

	@Before
	@Transactional
	public void putAccounts() throws Exception {
		policyHelper.putPolicy(existingPolicy);
	}

	@Test(expected = ErrorResponse.class)
	public void testCreateDupPolicy() throws Exception {
		final PutScalingPolicyRequest request = policyHelper
				.putPolicyRequest(existingPolicy);
		final String goodGroup = "policyGrp:" + baseName;
		asGroupHelper.createASGroup(goodGroup);
		request.put("AutoScalingGroupName", goodGroup);
		final PutScalingPolicy putPolicy = new PutScalingPolicy();
		actionTestHelper.invokeProcess(putPolicy, request.getRequest(),
				request.getResponse(), request.getMap());
	}

	@Test
	public void testCreateGoodPolicy() throws Exception {
		policyHelper.putPolicy(newPolicy);
	}

	@Test(expected = ErrorResponse.class)
	public void testCreatePolicyMissingArgs() throws Exception {
		final PutScalingPolicyRequest request = new PutScalingPolicyRequest();
		final PutScalingPolicy putScalingPolicy = new PutScalingPolicy();
		actionTestHelper.invokeProcess(putScalingPolicy, request.getRequest(),
				request.getResponse(), request.getMap());
	}

}
