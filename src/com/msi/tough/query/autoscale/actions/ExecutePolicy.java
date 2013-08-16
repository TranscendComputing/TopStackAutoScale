package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.ExecutePolicyRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class ExecutePolicy extends AbstractAction<Object> {
	private final static Logger logger = Appctx.getLogger(ExecutePolicy.class
			.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"ExecutePolicy");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("ExecutePolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "ExecutePolicyResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final ExecutePolicyRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean g = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (g == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final ASPolicyBean asp = ASUtil.readASPolicy(session, ac.getId(),
				r.getPolicyName());
		if (asp == null) {
			throw AutoScaleQueryFaults.policyDoesNotExist();
		}
		if (r.getHonorCooldown() != null && r.getHonorCooldown()
				&& g.getCooldownTime() != null
				&& g.getCooldownTime().getTime() > System.currentTimeMillis()) {
			throw AutoScaleQueryFaults.scalingActivityInProgress();
		}
		ASUtil.executeASPolicy(session, ac.getId(), r.getPolicyName());
		return null;
	}

	public ExecutePolicyRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final ExecutePolicyRequest req = new ExecutePolicyRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
		req.setHonorCooldown(QueryUtil.getBoolean(in, "HonorCooldown"));
		return req;
	}
}
