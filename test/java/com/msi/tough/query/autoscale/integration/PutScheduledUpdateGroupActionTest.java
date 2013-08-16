package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.PutScheduledUpdateGroupActionRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;


public class PutScheduledUpdateGroupActionTest extends AbstractBaseAutoscaleTest {

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
    public void testPutScheduledUGA_MissingArgs() throws Exception {
        final PutScheduledUpdateGroupActionRequest request = new PutScheduledUpdateGroupActionRequest();
        getAutoScaleClientV2().putScheduledUpdateGroupAction(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testPutScheduledUGA_InvalidName() throws Exception {
        final PutScheduledUpdateGroupActionRequest request = new PutScheduledUpdateGroupActionRequest();
        request.withAutoScalingGroupName(name2);
        request.withScheduledActionName("invalidActionScheduled");
        getAutoScaleClientV2().putScheduledUpdateGroupAction(request);
    }

    @Test
    public void testGoodPutScheduledUGA() throws Exception {
        logger.info("PutScheduledUpdateGroupAction applied to "+name1);
        final PutScheduledUpdateGroupActionRequest request = new PutScheduledUpdateGroupActionRequest();
        request.withAutoScalingGroupName(name1);
        request.withScheduledActionName("validActionScheduled");
        request.withRecurrence("");
        getAutoScaleClientV2().putScheduledUpdateGroupAction(request);
    }
}
