package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DeleteAutoScalingGroupTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(DeleteAutoScalingGroupTest.class
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
    }
    

    @Test(expected = AmazonServiceException.class)
    public void testDeleteASGroupMissingArgs() throws Exception {
        final DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();
        getAutoScaleClientV2().deleteAutoScalingGroup(request);
        asGroupHelper.deleteAllCreatedASGroups();
    }


    @Test(expected = AmazonServiceException.class)
    public void testDeleteInvalidName() throws Exception {
        final DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();
        request.withAutoScalingGroupName("");
        getAutoScaleClientV2().deleteAutoScalingGroup(request);
        asGroupHelper.deleteAllCreatedASGroups();
    }

    
    
    @Test
    public void testGoodDelete() throws Exception {
        logger.info("Updating AS group "+name1);
        final DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();
        request.withAutoScalingGroupName(name1);
        getAutoScaleClientV2().deleteAutoScalingGroup(request);
    }
}
