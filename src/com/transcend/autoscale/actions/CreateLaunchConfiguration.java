package com.transcend.autoscale.actions;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.transform.CreateLaunchConfigurationRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationRequestMessage;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateLaunchConfiguration

extends
AbstractQueuedAction<CreateLaunchConfigurationRequestMessage, CreateLaunchConfigurationResultMessage> {

	 private static Map<String, Meter> meters = initMeter("AutoScaling",
	            "CreateLaunchConfiguration");

	 @Override
	    protected void mark(Object ret, Exception e) {
	        markStandard(meters, e);
	    }

	 public String marshall(ServiceResponse resp,
	    		CreateLaunchConfigurationResultMessage message) {
	        final XMLNode xn = new XMLNode("CreateLaunchConfigurationResponse");
	        xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
	        // add metadata
	        final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
	        QueryUtil.addNode(meta, "RequestId", resp.getRequestId());
	        return xn.toString();
	    }

	@Override
	public ServiceResponse buildResponse(ServiceResponse resp,
			CreateLaunchConfigurationResultMessage message) {
		 resp.setPayload(marshall(resp, message));
	        return resp;
	}





	@Override
	public CreateLaunchConfigurationRequestMessage handleRequest(
			ServiceRequest req, ServiceRequestContext context)
			throws ErrorResponse {
		final CreateLaunchConfigurationRequestMessage requestMessage =
				CreateLaunchConfigurationRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());

        return requestMessage;
	}











}
