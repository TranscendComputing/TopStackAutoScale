package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class CreateAutoScalingGroupTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(CreateAutoScalingGroupTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "cr-asg-1-" + baseName;
    String name2 = "cr-asg-2-" + baseName;

    @Autowired
    private AutoScaleGroupHelper asGroupHelper;


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        asGroupHelper.deleteAllCreatedASGroups();
    }
    
    @Test(expected = AmazonServiceException.class)
    public void testCreateASGroupMissingArgs() throws Exception {
        final CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest();
        getAutoScaleClientV2().createAutoScalingGroup(request);
    }
    
    @Test(expected = AmazonServiceException.class)
    public void testCreateDupASGroup() throws Exception {
        final CreateAutoScalingGroupRequest request = asGroupHelper
                .createASGroupRequest(name1);
        getAutoScaleClientV2().createAutoScalingGroup(request);
    }
   
    @Test
    public void testGoodCreate() throws Exception {
        logger.info("Creating AS group "+name1);
        asGroupHelper.createASGroup(name1);
    }
}
