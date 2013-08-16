package com.msi.tough.query.autoscale.integration;


import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DescribeTerminationPolicyTypesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;

public class DescribeTerminationPolicyTypesTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(DescribeTerminationPolicyTypesTest.class
            .getName());

    @Test
    public void testGoodDescribeTerminationPolicyTypes() throws Exception {
        logger.info("Describing termination policy types");
        final DescribeTerminationPolicyTypesRequest request = new DescribeTerminationPolicyTypesRequest();
        getAutoScaleClientV2().describeTerminationPolicyTypes(request);
        }
}
