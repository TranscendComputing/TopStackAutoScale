package com.msi.tough.query.autoscale.integration;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import com.amazonaws.services.autoscaling.model.DescribeScalingProcessTypesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;

public class DescribeScalingProcessTypesTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(CreateAutoScalingGroupTest.class
            .getName());

    @Test
    public void testGoodDescribeScalingProcessTypes() throws Exception {
        logger.info("Describing scaling process types");
        final DescribeScalingProcessTypesRequest request = new DescribeScalingProcessTypesRequest();
        getAutoScaleClientV2().describeScalingProcessTypes(request);
    }

    
}
