package com.msi.tough.query.autoscale.integration;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.autoscaling.model.DescribeAdjustmentTypesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.autoscale.helper.AutoScaleGroupHelper;

public class DescribeAdjustmentTypesTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(DescribeAdjustmentTypesTest.class
            .getName());


    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "cr-asg-1-" + baseName;
    String name2 = "cr-asg-2-" + baseName;

    @Autowired
    private AutoScaleGroupHelper asGroupHelper;


    @Before
    public void setUp() throws Exception {
        logger.info("Creating AS group "+name1);
        asGroupHelper.createASGroup(name1);
    }

    @After
    public void tearDown() throws Exception {
        asGroupHelper.deleteAllCreatedASGroups();
    }

    @Test
    public void testGoodDescribeAdjustmentTypes() throws Exception {
        logger.info("Describing adjustment types");

        final DescribeAdjustmentTypesRequest request = new DescribeAdjustmentTypesRequest();
        getAutoScaleClientV2().describeAdjustmentTypes(request);
    }


}
