package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.DeletePolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DeletePolicyTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(CreateAutoScalingGroupTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "cr-asg-1-" + baseName;
    String name2 = "cr-asg-2-" + baseName;

    @Autowired
    private AutoScaleGroupHelper asGroupHelper;


    @Before
    public void setup(){
        logger.info("Creating AS group "+name1);
        asGroupHelper.createASGroup(name1);
    };
    
    @After
    public void teardown() throws Exception{
        asGroupHelper.deleteAllCreatedASGroups();
    }
    
    
    @Test(expected = AmazonServiceException.class)
    public void testDeletePolicyMissingArgs() throws Exception {
        final DeletePolicyRequest request = new DeletePolicyRequest();
        getAutoScaleClientV2().deletePolicy(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testDeletePolicyInvalidPolicyName() throws Exception {
        final DeletePolicyRequest request = new DeletePolicyRequest();
        request.withPolicyName(name1);
        getAutoScaleClientV2().deletePolicy(request);
    }

    @Test
    public void testGoodPutScaling() throws Exception {        
        logger.info("DeletePolicy applied to group "+name1);
        final PutScalingPolicyRequest putRequest = new PutScalingPolicyRequest();
        putRequest.withAutoScalingGroupName(name1);
        putRequest.withAdjustmentType("ExactCapacity");
        putRequest.withScalingAdjustment(2);
        putRequest.withPolicyName("NewWorkingPolicy2");
        getAutoScaleClientV2().putScalingPolicy(putRequest);
        
        final DeletePolicyRequest request = new DeletePolicyRequest();
        request.withAutoScalingGroupName(name1);
        request.withPolicyName("NewWorkingPolicy2");
        getAutoScaleClientV2().deletePolicy(request);
    }
}
