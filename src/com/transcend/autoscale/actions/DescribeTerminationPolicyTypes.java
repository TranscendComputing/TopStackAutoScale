package com.transcend.autoscale.actions;


import org.slf4j.Logger;

import java.util.Map;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DescribeTerminationPolicyTypesMessage.DescribeTerminationPolicyTypesRequestMessage;
import com.transcend.autoscale.message.DescribeTerminationPolicyTypesMessage.DescribeTerminationPolicyTypesResultMessage;
import com.transcend.autoscale.worker.DescribeTerminationPolicyTypesWorker;
import com.yammer.metrics.core.Meter;

public class DescribeTerminationPolicyTypes
        extends
        AbstractQueuedAction<DescribeTerminationPolicyTypesRequestMessage, DescribeTerminationPolicyTypesResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeTerminationPolicyTypes");
    private final Logger logger = Appctx.getLogger(DescribeTerminationPolicyTypesWorker.class
            .getName());

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeTerminationPolicyTypesResultMessage message) {
		final XMLNode xn = new XMLNode("DescribeTerminationPolicyTypesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "DescribeTerminationPolicyTypesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", message.getRequestId());

		final XMLNode types = QueryUtil.addNode(xr, "TerminationPolicyTypes");
		QueryUtil.addNode(types, "TerminationPolicyType", "OldestInstance");
		QueryUtil.addNode(types, "TerminationPolicyType",
				"OldestLaunchConfiguration");
		QueryUtil.addNode(types, "TerminationPolicyType", "NewestInstance");
		QueryUtil.addNode(types, "TerminationPolicyType",
				"ClosestToNextInstanceHour");
		QueryUtil.addNode(types, "TerminationPolicyType", "Default");
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
    public DescribeTerminationPolicyTypesRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final DescribeTerminationPolicyTypesRequestMessage.Builder tReq =
    			DescribeTerminationPolicyTypesRequestMessage.newBuilder();

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
    		DescribeTerminationPolicyTypesResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
