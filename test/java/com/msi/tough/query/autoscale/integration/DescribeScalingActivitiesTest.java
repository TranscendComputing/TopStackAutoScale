package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.autoscaling.model.DescribeScalingActivitiesRequest;
import com.amazonaws.services.autoscaling.model.ExecutePolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DescribeScalingActivitiesTest extends AbstractBaseAutoscaleTest {

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


    @Test
    public void testDescribeScalingActivitiesMissingArgs() throws Exception {
        final DescribeScalingActivitiesRequest request = new DescribeScalingActivitiesRequest();
        getAutoScaleClientV2().describeScalingActivities(request);
    }



    @Test
    public void testDescribeScalingActivitiesInvalidASGroupName() throws Exception {
        final DescribeScalingActivitiesRequest request = new DescribeScalingActivitiesRequest();
        request.withAutoScalingGroupName(name2);
        getAutoScaleClientV2().describeScalingActivities(request);
    }


    @Test
    public void testGoodDescribeScalingActivities() throws Exception {
        logger.info("DescribeScalingActivities applied to group "+name1);


        logger.info("PutScalingPolicyRequest applied to group "+name1);
        final PutScalingPolicyRequest putRequest = new PutScalingPolicyRequest();
        putRequest.withAutoScalingGroupName(name1);
        putRequest.withAdjustmentType("ExactCapacity");
        putRequest.withScalingAdjustment(2);
        putRequest.withPolicyName("NewWorkingPolicy" + name1);
        getAutoScaleClientV2().putScalingPolicy(putRequest);

        final ExecutePolicyRequest request = new ExecutePolicyRequest();
        request.withAutoScalingGroupName(name1);
        request.withPolicyName("NewWorkingPolicy" + name1);
        request.withHonorCooldown(false);
        getAutoScaleClientV2().executePolicy(request);

        final DescribeScalingActivitiesRequest DescribeRequest = new DescribeScalingActivitiesRequest();
        DescribeRequest.withAutoScalingGroupName(name1);
        getAutoScaleClientV2().describeScalingActivities(DescribeRequest);
    }

}
