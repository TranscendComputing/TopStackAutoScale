package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.DeletePolicyRequest;
import com.amazonaws.services.autoscaling.model.DescribePoliciesRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DescribePoliciesTest extends AbstractBaseAutoscaleTest {

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
        
        final PutScalingPolicyRequest request = new PutScalingPolicyRequest();
        request.withAutoScalingGroupName(name1);
        request.withAdjustmentType("ExactCapacity");
        request.withScalingAdjustment(2);
        request.withPolicyName("myPolicy");
        getAutoScaleClientV2().putScalingPolicy(request);
        
    };
    
    @After
    public void teardown() throws Exception{   
        final DeletePolicyRequest request = new DeletePolicyRequest();
        request.withAutoScalingGroupName(name1);
        request.withPolicyName("myPolicy");
        getAutoScaleClientV2().deletePolicy(request);
        
        asGroupHelper.deleteAllCreatedASGroups();
    }

    @Test (expected = AmazonServiceException.class)
    public void testDescribePoliciesMissingArgs() throws Exception {
        final DescribePoliciesRequest request = new DescribePoliciesRequest();
        getAutoScaleClientV2().describePolicies(request);
       
    }
    
    @Test (expected = AmazonServiceException.class)
    public void testDescribePoliciesWrongArgs() throws Exception {
        final DescribePoliciesRequest request = new DescribePoliciesRequest();
        request.withAutoScalingGroupName(name2);
        getAutoScaleClientV2().describePolicies(request);
       
    }


    @Test
    public void testGoodDescribe() throws Exception {
        logger.info("DescribePolicies applied to group "+name1);
        final DescribePoliciesRequest request = new DescribePoliciesRequest();
        request.withAutoScalingGroupName(name1);
        getAutoScaleClientV2().describePolicies(request);
    }
    
}
