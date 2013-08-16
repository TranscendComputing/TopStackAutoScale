package com.msi.tough.query.autoscale.basic;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.tough.query.autoscale.AbstractBaseAutoscaleTest;

public class ConfigTest extends AbstractBaseAutoscaleTest {

    @Autowired
    public String targetServer;

	@Test
	public void testSuccessfulConfigure() {
	    // Executing this method means no stacktrace on config load.
	}
}
