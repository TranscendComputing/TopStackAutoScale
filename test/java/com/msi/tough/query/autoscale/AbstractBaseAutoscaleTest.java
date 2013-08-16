package com.msi.tough.query.autoscale;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-autoScaleQueryContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public abstract class AbstractBaseAutoscaleTest {

    @Resource(name = "basicAWSCredentials")
    private AWSCredentials creds;

    @Autowired
    private AmazonAutoScalingClient autoScaleClient;

    @Autowired
    private AmazonAutoScalingClient autoScaleClientV2;

    @Autowired
    private String defaultAvailabilityZone;

    public AWSCredentials getCreds() {
        return creds;
    }

    public void setCreds(AWSCredentials creds) {
        this.creds = creds;
    }

    public String getDefaultAvailabilityZone() {
        return defaultAvailabilityZone;
    }

    public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
        this.defaultAvailabilityZone = defaultAvailabilityZone;
    }

    public AmazonAutoScalingClient getAutoScaleClient() {
        return autoScaleClient;
    }

    public void setAutoScaleClient(AmazonAutoScalingClient autoScaleClient) {
        this.autoScaleClient = autoScaleClient;
    }

    public AmazonAutoScalingClient getAutoScaleClientV2() {
        return autoScaleClientV2;
    }

    public void setAutoScaleClientV2(AmazonAutoScalingClient autoScaleClient) {
        this.autoScaleClientV2 = autoScaleClient;
    }
}
