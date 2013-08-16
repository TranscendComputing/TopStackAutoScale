package com.msi.tough.query.autoscale.integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesResult;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupResult;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;
import com.msi.tough.query.autoscale.helper.RunningAutoScalingInstanceHelper;
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesResultMessage.ScalingPolicy;


public class TerminateInstanceInAutoScalingGroupTest extends AbstractBaseAutoscaleTest {

	@Resource
    RunningAutoScalingInstanceHelper runningASInstanceHelper = null;
	

    @Resource
    private String defaultFlavor = null;

    @Resource
    protected String baseImageId = null;

	
    private static Logger logger = Appctx.getLogger(TerminateInstanceInAutoScalingGroupTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "trmIn-asg-1-" + baseName;
    String name2 = "trmIn-asg-2-" + baseName;
    String name3 = "trmIn-asg-2-" + baseName;

    @Autowired
    private AutoScaleGroupHelper asGroupHelper;
 
    
    @Test(expected = AmazonServiceException.class)
    public void testTerminateASGroupMissingArgs() throws Exception {
        final TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest();
        getAutoScaleClientV2().terminateInstanceInAutoScalingGroup(request);
    }
    
    @Test(expected = AmazonServiceException.class)
    public void testTerminateInvalidId() throws Exception {
        final TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest();
        request.withInstanceId(name2);
        getAutoScaleClientV2().terminateInstanceInAutoScalingGroup(request);
    }
   
   
    
    @Test
    public void testGoodTerminate() throws Exception {
        
        logger.info("Will terminate instance in autoScalingGroup: " + name3);
    	String instanceId = runningASInstanceHelper.getNewInstanceId(name3);
        logger.debug("instanceId is: " + instanceId);
        
        final TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest();
        request.withInstanceId(instanceId);
        request.withShouldDecrementDesiredCapacity(false);
        getAutoScaleClientV2().terminateInstanceInAutoScalingGroup(request);
        runningASInstanceHelper.finalDestroy();

    }
}
