package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;
import com.msi.tough.query.autoscale.helper.LaunchConfigHelper;

public class DeleteLaunchConfigurationTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(CreateAutoScalingGroupTest.class
            .getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "del-lc-1-" + baseName;
    String name2 = "del-lc-2-" + baseName;

    @Autowired
    private AutoScaleGroupHelper asGroupHelper;
    @Autowired
    private LaunchConfigHelper lcHelper;
    @Autowired
    private String baseImageId;
    @Autowired
    private String testInstanceType;


    @Test(expected = AmazonServiceException.class)
    public void testDeleteLaunchConfigurationMissingArgs() throws Exception {
        final DeleteLaunchConfigurationRequest request = new DeleteLaunchConfigurationRequest();
        getAutoScaleClientV2().deleteLaunchConfiguration(request);
    }


    @Test(expected = AmazonServiceException.class)
    public void testDeleteLaunchConfigurationInvalidLaunchConfigurationName() throws Exception {
        final DeleteLaunchConfigurationRequest request = new DeleteLaunchConfigurationRequest();
        request.withLaunchConfigurationName(name1);
        getAutoScaleClientV2().deleteLaunchConfiguration(request);
    }

    @Test
    public void testGoodPutScaling() throws Exception {
        logger.info("DeleteLaunchConfiguration with name " + name1);
        final CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
        request.withImageId(baseImageId);
        request.withInstanceType(testInstanceType);
        request.withLaunchConfigurationName(name1);
        getAutoScaleClientV2().createLaunchConfiguration(request);

        final DeleteLaunchConfigurationRequest delRequest = new DeleteLaunchConfigurationRequest();
        delRequest.withLaunchConfigurationName(name1);
        getAutoScaleClientV2().deleteLaunchConfiguration(delRequest);
    }
}
