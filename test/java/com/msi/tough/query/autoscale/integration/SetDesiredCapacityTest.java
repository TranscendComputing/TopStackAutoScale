package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.SetDesiredCapacityRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class SetDesiredCapacityTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(SetDesiredCapacityTest.class
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
    public void testSetDesiredCapacityMissingArgs() throws Exception {
        final SetDesiredCapacityRequest request = new SetDesiredCapacityRequest();
        getAutoScaleClientV2().setDesiredCapacity(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testSetDesiredCapacityInvalidName() throws Exception {
        final SetDesiredCapacityRequest request = new SetDesiredCapacityRequest();
        request.withAutoScalingGroupName("");
        request.withDesiredCapacity(2);
        request.withHonorCooldown(true);
        getAutoScaleClientV2().setDesiredCapacity(request);
    }

    @Test
    public void testGoodSetDesiredCapacity() throws Exception {
        logger.info("SettingDesiredCapacity group "+name1);
        final SetDesiredCapacityRequest request = new SetDesiredCapacityRequest();
        request.withAutoScalingGroupName(name1);
        request.withDesiredCapacity(2);
        request.withHonorCooldown(true);
        getAutoScaleClientV2().setDesiredCapacity(request);
    }
}
