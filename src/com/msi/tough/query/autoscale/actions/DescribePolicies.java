package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DescribePoliciesRequest;
import com.amazonaws.services.autoscaling.model.DescribePoliciesResult;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DescribePolicies extends AbstractAction<DescribePoliciesResult> {
	private final static Logger logger = Appctx
			.getLogger(DescribePolicies.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribePolicies");

	@Override
	protected void mark(DescribePoliciesResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<DescribePoliciesResult> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DescribePoliciesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn, "DescribePoliciesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		final XMLNode xpolicies = QueryUtil.addNode(xr, "ScalingPolicies");
		final List<ScalingPolicy> policies = input.getMainObject()
				.getScalingPolicies();
		if (policies != null) {
			for (final ScalingPolicy i : policies) {
				final XMLNode xpolicy = QueryUtil.addNode(xpolicies, "member");
				AutoScaleQueryUtil.marshallAutoPolicy(xpolicy, i);
			}
		}
		return xn.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DescribePoliciesResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
		final DescribePoliciesRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}

		String nextToken = null;
		String firstToken = null;
		int cnt = 0;

		final DescribePoliciesResult res = new DescribePoliciesResult();
		final Collection<ScalingPolicy> policies = new ArrayList<ScalingPolicy>();
		final Query q = session.createQuery("from ASPolicyBean where userId="
				+ ac.getId() + " and grpName='" + r.getAutoScalingGroupName()
				+ "'");
		final List<ASPolicyBean> l = q.list();
		for (final ASPolicyBean b : l) {
			if (r.getPolicyNames() != null && r.getPolicyNames().size() > 0
					&& !r.getPolicyNames().contains(b.getName())) {
				continue;
			}
			if (r.getNextToken() != null
					&& b.getName().compareTo(r.getNextToken()) < 0) {
				continue;
			}
			if (r.getMaxRecords() != 0 && r.getMaxRecords() <= cnt) {
				nextToken = b.getName();
				break;
			}
			if (firstToken == null) {
				firstToken = b.getName();
			}
			cnt++;
			final ScalingPolicy policy = new ScalingPolicy();
			policy.setPolicyARN(b.getArn());
			policy.setAdjustmentType(b.getAdjustmentType());
			// policy.setAlarms(alarms);
			policy.setAutoScalingGroupName(r.getAutoScalingGroupName());
			policy.setCooldown(b.getCooldown());
			policy.setMinAdjustmentStep(b.getMinAdjustmentStep());
			policy.setPolicyName(b.getName());
			policy.setScalingAdjustment(b.getScalingAdjustment());
			policies.add(policy);
		}
		res.setScalingPolicies(policies);
		res.setNextToken(nextToken);
		if (r.getNextToken() != null
				&& (firstToken == null || !r.getNextToken().equals(firstToken))) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		return res;
	}

	public DescribePoliciesRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final DescribePoliciesRequest req = new DescribePoliciesRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		req.setNextToken(QueryUtil.getString(in, "NextToken"));
		final Collection<String> policyNames = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("PolicyNames.member." + i)) {
				break;
			}
			policyNames.add(QueryUtil.getString(in, "PolicyNames.member." + i));
		}
		req.setPolicyNames(policyNames);
		return req;
	}
}
