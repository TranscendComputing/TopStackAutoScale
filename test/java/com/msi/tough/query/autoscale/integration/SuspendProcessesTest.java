package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.SuspendProcessesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class SuspendProcessesTest extends AbstractBaseAutoscaleTest {

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
    public void testSuspendProcessesMissingArgs() throws Exception {
        final SuspendProcessesRequest request = new SuspendProcessesRequest();
        getAutoScaleClientV2().suspendProcesses(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testSuspendInvalidASGroupName() throws Exception {
        final SuspendProcessesRequest request = new SuspendProcessesRequest();
        request.withAutoScalingGroupName(name2);
        getAutoScaleClientV2().suspendProcesses(request);
    }

    @Test
    public void testGoodSuspend() throws Exception {
        logger.info("Suspending AS group "+name1);
        final SuspendProcessesRequest request = new SuspendProcessesRequest();
        request.withAutoScalingGroupName(name1);
        getAutoScaleClientV2().suspendProcesses(request);
    }
}
