package com.msi.tough.query.autoscale.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DescribeLaunchConfigurationsTest extends AbstractBaseAutoscaleTest {

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
    public void testDescribeLaunchConfigurationsNoArgs() throws Exception {
        final DescribeLaunchConfigurationsRequest request = new DescribeLaunchConfigurationsRequest();
        getAutoScaleClientV2().describeLaunchConfigurations(request);
    }



    @Test (expected = AmazonServiceException.class)
    public void testDescribeLaunchConfigurationsInvalidParameters() throws Exception {
        final DescribeLaunchConfigurationsRequest request = new DescribeLaunchConfigurationsRequest();
		request.withNextToken("invalidToken");
        getAutoScaleClientV2().describeLaunchConfigurations(request);
    }


    @Test
    public void testGoodDescribeLaunchConfigurations() throws Exception {
        logger.info("Describing LaunchConfigurations");
        final DescribeLaunchConfigurationsRequest DescribeRequest = new DescribeLaunchConfigurationsRequest();
		DescribeRequest.withMaxRecords(5);
        getAutoScaleClientV2().describeLaunchConfigurations(DescribeRequest);
    }
 

}
