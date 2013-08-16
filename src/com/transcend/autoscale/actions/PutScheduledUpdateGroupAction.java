package com.transcend.autoscale.actions;


import java.util.Map;

import com.google.common.base.Strings;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.autoscale.message.PutScheduledUpdateGroupActionMessage.PutScheduledUpdateGroupActionRequestMessage;
import com.transcend.autoscale.message.PutScheduledUpdateGroupActionMessage.PutScheduledUpdateGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class PutScheduledUpdateGroupAction
        extends
        AbstractQueuedAction<PutScheduledUpdateGroupActionRequestMessage, PutScheduledUpdateGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("AutoScaling",
            "PutScheduledUpdateGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		PutScheduledUpdateGroupActionResultMessage message) {
        final XMLNode xn = new XMLNode("PutScheduledUpdateGroupActionResponse");
        xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
        // add metadata
        final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
        QueryUtil.addNode(meta, "RequestId", resp.getRequestId());
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
    public PutScheduledUpdateGroupActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {

    	final PutScheduledUpdateGroupActionRequestMessage.Builder tReq =
    			PutScheduledUpdateGroupActionRequestMessage.newBuilder();
    	final Map<String, String[]> in = req.getParameterMap();
    	
    	tReq.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		tReq.setDesiredCapacity(QueryUtil.getInt(in, "DesiredCapacity"));
		tReq.setEndTime(Strings.nullToEmpty(QueryUtil.getString(in, "EndTime")));
		tReq.setMaxSize(QueryUtil.getInt(in, "MaxSize"));
		tReq.setMinSize(QueryUtil.getInt(in, "MinSize"));
		tReq.setRecurrence(Strings.nullToEmpty(QueryUtil.getString(in, "Recurrence")));
		tReq.setScheduledActionName(QueryUtil.requiredString(in,
				"ScheduledActionName"));
		tReq.setStartTime(Strings.nullToEmpty(QueryUtil.getString(in, "StartTime")));
		if (in.containsKey("Time")) {
			tReq.setTime(Strings.nullToEmpty(QueryUtil.getString(in, "Time")));
		}
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
    		PutScheduledUpdateGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}
