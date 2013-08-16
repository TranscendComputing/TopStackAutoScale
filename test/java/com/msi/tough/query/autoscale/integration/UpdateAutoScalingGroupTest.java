package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class UpdateAutoScalingGroupTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(UpdateAutoScalingGroupTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    
    private String defaultAvailabilityZone = null;

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
    public void testUpdateASGroupMissingArgs() throws Exception {
        final UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        getAutoScaleClientV2().updateAutoScalingGroup(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testUpdateInvalidName() throws Exception {
        final UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.withAutoScalingGroupName("");
        request.withAvailabilityZones(defaultAvailabilityZone);
        request.withMinSize(0);
        request.withMaxSize(1);
        getAutoScaleClientV2().updateAutoScalingGroup(request);
    }

    @Test
    public void testGoodUpdate() throws Exception {
        logger.info("Updating AS group "+name1);
        final UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.withAutoScalingGroupName(name1);
        request.withAvailabilityZones(defaultAvailabilityZone);
        request.withMinSize(0);
        request.withMaxSize(1);
        getAutoScaleClientV2().updateAutoScalingGroup(request);
    }
}
