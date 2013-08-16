/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query.autoscale.helper;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.msi.tough.core.Appctx;
import com.msi.tough.helper.AbstractHelper;
import com.msi.tough.helper.RunningInstanceHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.workflow.WorkflowSubmitter;

/**
 * 
 */
public class RunningAutoScalingInstanceHelper extends AbstractHelper<String> {
    private final static Logger logger = Appctx
            .getLogger(RunningInstanceHelper.class.getName());
    
    private static final int MAX_WAIT_SECS = 90;
    private static final int WAIT_SECS = 3;
    
    @Autowired
    private AutoScaleGroupHelper asGroupHelper;

    @Resource(name = "accessKey")
    String accessKey = null;
    
    @Autowired
    private AmazonAutoScalingClient autoScaleClientV2;
    
    @Autowired
    private AmazonAutoScalingClient autoScaleClient;

    @Resource
    protected String defaultAvailabilityZone = null;

    @Resource
    private String defaultFlavor = null;

    @Resource
    protected String baseImageId = null;

    @Resource
    protected AmazonEC2Client computeClient = null;
    
    @Resource
    LaunchConfigHelper launchConfigHelper = null;

    /**
     *
     */
    public RunningAutoScalingInstanceHelper() {
        super();
    }

    
    /**
     * 
     * Create a minimal AutoScalingGroup request with one instance
     * 
     */
    public CreateAutoScalingGroupRequest createASGroupRequest(String groupName) {
        final CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest();
        request.withAutoScalingGroupName(groupName);
        request.withAvailabilityZones(defaultAvailabilityZone);
        request.withDesiredCapacity(1);
        request.withDefaultCooldown(1);
        request.withHealthCheckGracePeriod(1);
        request.withHealthCheckType("ELB");
        request.withMinSize(1);
        request.withMaxSize(1);
        return request;
    }
    
    /**
     * Create a minimal AutoScalingGroup group.
     *
     * @param groupName
     * the name of the AutoScalingGroup
     */
    public void createASGroup(String groupName) throws Exception{
    	
        final CreateLaunchConfigurationRequest lcRequest = new CreateLaunchConfigurationRequest();
        lcRequest.withImageId(baseImageId);
        lcRequest.withInstanceType(defaultFlavor);
        lcRequest.withLaunchConfigurationName(groupName);
        autoScaleClientV2.createLaunchConfiguration(lcRequest);
        
        CreateAutoScalingGroupRequest request = createASGroupRequest(groupName);
        request.withLaunchConfigurationName(groupName);
        try {
        	autoScaleClientV2.createAutoScalingGroup(request);
        }
    	catch (ErrorResponse e) {
    		logger.debug("AutoScalingGroup: " + groupName + " cannot be created.");
    	}
        addEntity(groupName);
    }
    
    /**
     * Describe first found instance.
     *
     * @throws InterruptedException 
     */
    public AutoScalingInstanceDetails describeASInstance() throws InterruptedException {
    	DescribeAutoScalingInstancesRequest request = new DescribeAutoScalingInstancesRequest();
        AutoScalingInstanceDetails firstInstance = null;  
    	DescribeAutoScalingInstancesResult result;

        for (int count = 0; count < MAX_WAIT_SECS; count += WAIT_SECS) {
            try {
            	result = autoScaleClient.describeAutoScalingInstances(request);
                firstInstance = result.getAutoScalingInstances().get(0);
                break;
            } catch (Exception e) {
                //No instances created, wait for instance
            	//logger.info("Waiting for instance to be initialized");
            }
            Thread.sleep(1000 * WAIT_SECS);
        }
        
        if (firstInstance!=null) {
            return firstInstance;
        }

        throw new IndexOutOfBoundsException("No instances created in ASGroup");

    }
    
    /**
     * @throws Exception 
     * 
     * 
     */
    public String getNewInstanceId(String groupName) throws Exception {
    	createASGroup(groupName);
    	AutoScalingInstanceDetails instanceInfo = describeASInstance();
    	return instanceInfo.getInstanceId();
    }
    
    /**
     * @throws Exception 
     * 
     * 
     * 
     */
    public String getInstanceId(String suggestedASGroupName) throws Exception {
        Collection<String> existing = getExistingEntities();
        if (! existing.isEmpty()) {
            String groupName = existing.iterator().next();
            logger.debug("Found existing existing AutoScalingGroup: " + groupName);
            try {
                //An AutoScalingGroup with one instance exists
            	AutoScalingInstanceDetails instanceInfo = describeASInstance();
            	return instanceInfo.getInstanceId();
            } catch (ErrorResponse e) {
                logger.debug("AutoScalingGroup: " + groupName + " not found, create new.");
            }
        }
        String instanceId = getNewInstanceId(suggestedASGroupName);
        return instanceId;    
        }    
    
    
    /**
     * Construct a Delete AutoScalingGroup request
     * 
     * @param ASGroupName
     * @return DeleteAutoScalingGroupRequest
     * 
     */
    private DeleteAutoScalingGroupRequest deleteASGroupRequest(String ASGroupName) {
    	final DeleteAutoScalingGroupRequest deleteRequest = new DeleteAutoScalingGroupRequest();
    	deleteRequest.withAutoScalingGroupName(ASGroupName);
    	return deleteRequest;    	
    }
 
    /**
     * Delete AutoScalingGroup given ASG name
     * @param ASGroupName
     */
    public void deleteASGroup(String ASGroupName) throws Exception {
    	final DeleteAutoScalingGroupRequest request = deleteASGroupRequest(ASGroupName);
        autoScaleClientV2.deleteAutoScalingGroup(request);
        removeEntity(ASGroupName);
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#entityName()
     */
    @Override
    public String entityName() {
        return "RunningAutoScalingInstance";
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#create(java.io.Serializable)
     */
    @Override
    public void create(String identifier) throws Exception {
    	getNewInstanceId(identifier);
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#delete(java.io.Serializable)
     */
    @Override
    public void delete(String identifier) throws Exception {
        deleteASGroup(identifier);
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#setWorkflowSubmitter(com.msi.tough.workflow.WorkflowSubmitter)
     */
    @Override
    public void setWorkflowSubmitter(WorkflowSubmitter submitter) {
    }
}