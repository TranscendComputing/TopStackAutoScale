package com.transcend.autoscale.actions;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DescribeAdjustmentTypesMessage.DescribeAdjustmentTypesRequestMessage;
import com.transcend.autoscale.message.DescribeAdjustmentTypesMessage.DescribeAdjustmentTypesResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeAdjustmentTypes
        extends
        AbstractQueuedAction<DescribeAdjustmentTypesRequestMessage, DescribeAdjustmentTypesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeAdjustmentTypes");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeAdjustmentTypesResultMessage message) {
		final XMLNode xn = new XMLNode("DescribeAdjustmentTypesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeAdjustmentTypesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());

		final XMLNode types = QueryUtil.addNode(xr, "AdjustmentTypes");
		{
			final XMLNode m = QueryUtil.addNode(types, "member");
			QueryUtil.addNode(m, "AdjustmentType", "ChangeInCapacity");
		}
		{
			final XMLNode m = QueryUtil.addNode(types, "member");
			QueryUtil.addNode(m, "AdjustmentType", "ExactCapacity");
		}
		{
			final XMLNode m = QueryUtil.addNode(types, "member");
			QueryUtil.addNode(m, "AdjustmentType", "PercentChangeInCapacity");
		}
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
    public DescribeAdjustmentTypesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final DescribeAdjustmentTypesRequestMessage.Builder tReq =
    			DescribeAdjustmentTypesRequestMessage.newBuilder();
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
    		DescribeAdjustmentTypesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
