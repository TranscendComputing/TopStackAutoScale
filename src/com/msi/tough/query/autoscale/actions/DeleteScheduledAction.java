package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DeleteScheduledActionRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DeleteScheduledAction extends AbstractAction<Object> {
	private final static Logger logger = Appctx
			.getLogger(DeleteScheduledAction.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DeleteScheduledAction");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("PutScalingPolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "PutScalingPolicyResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final DeleteScheduledActionRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final ASScheduledBean b = ASUtil.readScheduled(session, ac.getId(),
				r.getAutoScalingGroupName(), r.getScheduledActionName());
		if (b == null) {
			throw AutoScaleQueryFaults.scheduledActionDoesNotExist();
		}
		session.delete(b);
		return null;
	}

	public DeleteScheduledActionRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DeleteScheduledActionRequest req = new DeleteScheduledActionRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setScheduledActionName(QueryUtil.requiredString(in,
				"ScheduledActionName"));
		return req;
	}
}
