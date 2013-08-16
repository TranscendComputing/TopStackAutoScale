package com.msi.tough.query.autoscale.actions;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupLocalHelper;
import com.msi.tough.query.autoscale.helper.LaunchConfigLocalHelper;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupRequestMessage;
import com.transcend.autoscale.worker.CreateAutoScalingGroupWorker;

/**
 * Test create auto scaling group locally.
 *
 * @author jgardner
 *
 */
public class CreateAutoScaleGroupLocalTest extends AbstractBaseAutoscaleTest {

    private final static Logger logger = Appctx
            .getLogger(CreateAutoScaleGroupLocalTest.class.getName());

    private final String baseGroupName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "crLoc:1:" + baseGroupName;
    String name2 = "crLoc:2:" + baseGroupName;

    @Resource
    private final ActionTestHelper actionTestHelper = null;

    @Resource
    LaunchConfigLocalHelper launchConfigLocalHelper = null;

    @Resource
    AutoScaleGroupLocalHelper asGroupLocalHelper = null;

    @Resource
    CreateAutoScalingGroupWorker asGroupWorker = null;

    @After
    @Transactional
    public void cleanupCreated() throws Exception {
        asGroupLocalHelper.deleteAllCreatedASGroups();
    }

    @Test
    @Ignore
    public void createOneOffASGroup() throws Exception {
        final String groupName = "my-as-group";
        final String launchConfigName = "my-launch-config";
        final CreateAutoScalingGroupRequestMessage.Builder request =
                asGroupLocalHelper
                .createASGroupRequest(groupName);
        request.setAutoScalingGroupName(groupName);
        launchConfigLocalHelper.createLaunchConfig(launchConfigName);
        request.setLaunchConfigurationName(launchConfigName);
        asGroupWorker.doWork(request.build());
    }
 
    @Before
    @Transactional
    public void putAccounts() throws Exception {
        asGroupLocalHelper.createASGroup(name1);
    }
   
    @Test(expected = Exception.class) // Exception is actually from protobuf.
    public void testCreateASGroupMissingArgs() throws Exception {
        final CreateAutoScalingGroupRequestMessage.Builder request =
                asGroupLocalHelper
                .createASGroupRequest(name1+"-not");
        asGroupWorker.doWork(request.build());
    }
    
    @Test(expected = ErrorResponse.class)
    public void testCreateDupASGroup() throws Exception {
        final CreateAutoScalingGroupRequestMessage.Builder request =
                asGroupLocalHelper
                .createASGroupRequest(name1);
        launchConfigLocalHelper.createLaunchConfig(name2);
        request.setLaunchConfigurationName(name2);
        asGroupWorker.doWork(request.build());
    }
   
    @Test
    public void testCreateGoodASGroup() throws Exception {
        logger.debug("Creating group: "+name2);
        asGroupLocalHelper.createASGroup(name2);
    }

}
