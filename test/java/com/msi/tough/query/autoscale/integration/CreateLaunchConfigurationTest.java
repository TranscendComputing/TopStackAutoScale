package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;
import com.msi.tough.query.autoscale.helper.LaunchConfigHelper;


public class CreateLaunchConfigurationTest extends AbstractBaseAutoscaleTest {


	private static Logger logger = Appctx.getLogger(CreateLaunchConfigurationTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "cr-asg-1-" + baseName;

    @Autowired
    private AutoScaleGroupHelper asGroupHelper;
    @Autowired
    private LaunchConfigHelper lcHelper;
    @Autowired
    private String baseImageId;
    @Autowired
    private String testInstanceType;

    @Before
    public void setup(){
       // logger.info("Creating AS group "+name1);
       // asGroupHelper.createASGroup(name1);
    }

    @After
    public void teardown() throws Exception{

        logger.info("Delete all Launch Configurations");
        lcHelper.deleteAllCreatedConfigs();

    }


    @Test(expected = AmazonServiceException.class)
    public void testCreateLaunchConfigurationMissingArgs() throws Exception {
        final CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
        getAutoScaleClientV2().createLaunchConfiguration(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testCreateLaunchInvalidPolicyName() throws Exception {
    	final CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
        request.withLaunchConfigurationName(name1);
        getAutoScaleClientV2().createLaunchConfiguration(request);
    }


    @Test
    public void testGoodCreate() throws Exception {
        logger.info("Creating Launch Configuration "+name1);

        final CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
        request.withImageId(baseImageId);
        request.withInstanceType(testInstanceType);
        request.withLaunchConfigurationName(name1);
        getAutoScaleClientV2().createLaunchConfiguration(request);

    }

}
