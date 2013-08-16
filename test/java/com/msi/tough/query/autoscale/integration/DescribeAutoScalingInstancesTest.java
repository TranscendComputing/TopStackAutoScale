package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DescribeAutoScalingInstancesTest extends AbstractBaseAutoscaleTest {

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
    public void testDescribeAutoScalingInstancesNoArgs() throws Exception {
        final DescribeAutoScalingInstancesRequest request = new DescribeAutoScalingInstancesRequest();
        getAutoScaleClientV2().describeAutoScalingInstances(request);
    }
  
    
    @Test (expected = AmazonServiceException.class)
    public void testDescribeAutoScalingInstancesInvalidParameters() throws Exception {
        final DescribeAutoScalingInstancesRequest request = new DescribeAutoScalingInstancesRequest();
		request.withNextToken("invalidToken");
        getAutoScaleClientV2().describeAutoScalingInstances(request);
    }


    @Test
    public void testGoodDescribeAutoScalingInstances() throws Exception {
        logger.info("Describing AutoScalingInstances");
        final DescribeAutoScalingInstancesRequest DescribeRequest = new DescribeAutoScalingInstancesRequest();
        DescribeRequest.withMaxRecords(5);
        getAutoScaleClientV2().describeAutoScalingInstances(DescribeRequest);
    }

}
