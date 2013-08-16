package com.transcend.autoscale.actions;
import java.util.Map;

import com.amazonaws.services.autoscaling.model.transform.DescribeAutoScalingGroupsRequestUnmarshaller;
import com.amazonaws.services.autoscaling.model.transform.DescribeAutoScalingGroupsResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsRequestMessage;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeAutoScalingGroups
        extends
        AbstractQueuedAction<DescribeAutoScalingGroupsRequestMessage, DescribeAutoScalingGroupsResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeAutoScalingGroups");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeAutoScalingGroupsResultMessage message) throws ErrorResponse {
		return new DescribeAutoScalingGroupsResultMarshaller().marshall(message);
		
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    
    @Override
    public DescribeAutoScalingGroupsRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
    		try {
				return DescribeAutoScalingGroupsRequestUnmarshaller
						.getInstance().unmarshall(req);
			} catch (Exception e) {
				e.printStackTrace();
				throw ErrorResponse.InternalFailure();
			}
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
    		DescribeAutoScalingGroupsResultMessage message) throws ErrorResponse{
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
