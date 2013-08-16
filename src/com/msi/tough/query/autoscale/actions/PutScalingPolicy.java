package com.msi.tough.query.autoscale.actions;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class PutScalingPolicy extends AbstractAction<PutScalingPolicyResult> {
	private final static Logger logger = Appctx
			.getLogger(PutScalingPolicy.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"PutScalingPolicy");

	@Override
	protected void mark(PutScalingPolicyResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<PutScalingPolicyResult> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("PutScalingPolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "PutScalingPolicyResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final PutScalingPolicyResult o = input.getMainObject();
		QueryUtil.addNode(xr, "PolicyARN", o.getPolicyARN());
		return xn.toString();
	}

	@Override
	public PutScalingPolicyResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
		final PutScalingPolicyRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.policyDoesNotExist();
		}
		if (r.getMinAdjustmentStep() != null && r.getMinAdjustmentStep() > 0
				&& !r.getAdjustmentType().equals("PercentChangeInCapacity")) {
			throw QueryFaults
					.InvalidParameterCombination("MinAdjustmentStep is valid only with an AdjustmentType PercentChangeInCapacity");
		}
		{
			final ASPolicyBean b = ASUtil.readASPolicy(session, ac.getId(),
					r.getPolicyName());
			if (b != null) {
				throw AutoScaleQueryFaults.alreadyExists();
			}
		}

		final ASPolicyBean asp = new ASPolicyBean();
		asp.setAdjustmentType(r.getAdjustmentType());
		asp.setCooldown(r.getCooldown());
		asp.setCreatedDate(new Date());
		asp.setGrpName(r.getAutoScalingGroupName());
		asp.setName(r.getPolicyName());
		asp.setScalingAdjustment(r.getScalingAdjustment());
		asp.setUserId(ac.getId());
		asp.setMinAdjustmentStep(r.getMinAdjustmentStep());
		asp.setArn("arn:autoscaling:policy:" + ac.getId() + ":"
				+ r.getPolicyName());
		session.save(asp);
		final PutScalingPolicyResult res = new PutScalingPolicyResult();
		res.setPolicyARN(asp.getArn());
		return res;
	}

	public PutScalingPolicyRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final PutScalingPolicyRequest req = new PutScalingPolicyRequest();
		req.setAdjustmentType(QueryUtil.requiredString(in, "AdjustmentType"));
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setCooldown(QueryUtil.getInt(in, "Cooldown"));
		req.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
		req.setScalingAdjustment(QueryUtil.requiredInt(in, "ScalingAdjustment"));
		req.setMinAdjustmentStep(QueryUtil.getInt(in, "MinAdjustmentStep"));
		return req;
	}
}
