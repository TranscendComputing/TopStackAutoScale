package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;
import com.msi.tough.query.autoscale.helper.RunningAutoScalingInstanceHelper;


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
        request.withInstanceId(name1);
        getAutoScaleClientV2().terminateInstanceInAutoScalingGroup(request);
    }



    @Test
    public void testGoodTerminate() throws Exception {

        logger.info("Will terminate instance in autoScalingGroup: " + name1);
    	String instanceId = runningASInstanceHelper.getNewInstanceId(name1);
        logger.debug("instanceId is: " + instanceId);

        final TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest();
        request.withInstanceId(instanceId);
        request.withShouldDecrementDesiredCapacity(false);
        getAutoScaleClientV2().terminateInstanceInAutoScalingGroup(request);
        runningASInstanceHelper.finalDestroy();

    }
}
