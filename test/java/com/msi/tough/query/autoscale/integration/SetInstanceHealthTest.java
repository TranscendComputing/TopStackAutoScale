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
import com.amazonaws.services.autoscaling.model.SetInstanceHealthRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.helper.RunningInstanceHelper;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class SetInstanceHealthTest extends AbstractBaseAutoscaleTest {

	@Resource
    RunningInstanceHelper runningInstanceHelper = null;
	
    private static Logger logger = Appctx.getLogger(SetInstanceHealthTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    
    private String defaultAvailabilityZone = null;

    String name1 = "cr-asg-1-" + baseName;
    String name2 = "cr-asg-2-" + baseName;
    String name3 = "cr-ins-1-" + baseName;


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
    public void testSetInstanceHealthMissingArgs() throws Exception {
        final SetInstanceHealthRequest request = new SetInstanceHealthRequest();
        getAutoScaleClientV2().setInstanceHealth(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testSetInstanceHealthInvalidHealthStatus() throws Exception {
        final SetInstanceHealthRequest request = new SetInstanceHealthRequest();
        request.withHealthStatus("");
        request.withShouldRespectGracePeriod(false);
        getAutoScaleClientV2().setInstanceHealth(request);
    }
    
    @Test
    public void testGoodSetInstanceHealth() throws Exception {
        logger.info("Setting Instance Health group "+name1);
    	String instanceId = runningInstanceHelper.runInstance(name3);
    	logger.info("Created Instance with Id = " + instanceId);
    	
        final SetInstanceHealthRequest request = new SetInstanceHealthRequest();
        request.withHealthStatus("Healthy");
        request.withInstanceId(instanceId);
        request.withShouldRespectGracePeriod(true);
        getAutoScaleClientV2().setInstanceHealth(request);
    }
    
}
