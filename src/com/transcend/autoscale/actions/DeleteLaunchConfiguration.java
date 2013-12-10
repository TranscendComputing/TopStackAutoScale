package com.transcend.autoscale.actions;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.google.common.base.Strings;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DeleteLaunchConfigurationMessage.DeleteLaunchConfigurationRequestMessage;
import com.transcend.autoscale.message.DeleteLaunchConfigurationMessage.DeleteLaunchConfigurationResultMessage;
import com.yammer.metrics.core.Meter;

public class DeleteLaunchConfiguration
        extends
        AbstractQueuedAction<DeleteLaunchConfigurationRequestMessage, DeleteLaunchConfigurationResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DeleteLaunchConfiguration");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DeleteLaunchConfigurationResultMessage message) {

		final XMLNode xn = new XMLNode("DeleteLaunchConfigurationResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "DeleteLaunchConfigurationResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());
		return xn.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */

    @Override
    public DeleteLaunchConfigurationRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final DeleteLaunchConfigurationRequestMessage.Builder tReq =
    			DeleteLaunchConfigurationRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
		tReq.setLaunchConfigurationName(QueryUtil.requiredString(in, "LaunchConfigurationName"));
		return tReq.buildPartial();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.
     * query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
    		DeleteLaunchConfigurationResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
