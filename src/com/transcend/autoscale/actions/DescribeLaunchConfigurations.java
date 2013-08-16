package com.transcend.autoscale.actions;
import java.util.Map;

import com.amazonaws.services.autoscaling.model.transform.DescribeLaunchConfigurationsRequestUnmarshaller;
import com.amazonaws.services.autoscaling.model.transform.DescribeLaunchConfigurationsResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsRequestMessage;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeLaunchConfigurations
        extends
        AbstractQueuedAction<DescribeLaunchConfigurationsRequestMessage, DescribeLaunchConfigurationsResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "DescribeLaunchConfigurations");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeLaunchConfigurationsResultMessage message) throws ErrorResponse {
		return new DescribeLaunchConfigurationsResultMarshaller().marshall(message);
		
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    
    @Override
    public DescribeLaunchConfigurationsRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
    		try {
				return DescribeLaunchConfigurationsRequestUnmarshaller
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
    		DescribeLaunchConfigurationsResultMessage message) throws ErrorResponse{
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
