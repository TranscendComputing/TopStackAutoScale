package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesResult;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DescribeAutoScalingInstances extends
		AbstractAction<DescribeAutoScalingInstancesResult> {
	private final static Logger logger = Appctx
			.getLogger(DescribeAutoScalingInstances.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeAutoScalingInstances");

	@Override
	protected void mark(DescribeAutoScalingInstancesResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(
			final MarshallStruct<DescribeAutoScalingInstancesResult> input,
			final HttpServletResponse resp) throws Exception {
		final DescribeAutoScalingInstancesResult o = input.getMainObject();
		final XMLNode xn = new XMLNode("DescribeAutoScalingInstancesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeAutoScalingInstancesResult");
		QueryUtil.addNode(xn, "NextToken", o.getNextToken());
		final XMLNode ln = QueryUtil.addNode(xr, "AutoScalingInstances");
		for (final AutoScalingInstanceDetails en : o.getAutoScalingInstances()) {
			final XMLNode m = QueryUtil.addNode(ln, "member");
			AutoScaleQueryUtil.marshallAutoScalingInstances(m, en);
		}

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public DescribeAutoScalingInstancesResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
		final DescribeAutoScalingInstancesRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final DescribeAutoScalingInstancesResult ret = new DescribeAutoScalingInstancesResult();

		final Collection<AutoScalingInstanceDetails> autoScalingInstances = new ArrayList<AutoScalingInstanceDetails>();
		final List<ASGroupBean> asgl = ASUtil.readASGroup(session, ac.getId());
		String nextToken = null;
		int cnt = 0;
		boolean all = false;
		if (r.getInstanceIds() == null || r.getInstanceIds().size() == 0) {
			all = true;
		}
		if (asgl != null) {
			for (final ASGroupBean asg : asgl) {
				final CommaObject il = new CommaObject(asg.getInstances());
				for (final String i : il.toList()) {
					if (r.getNextToken() != null
							&& i.compareTo(r.getNextToken()) < 0) {
						continue;
					}
					if (r.getMaxRecords() != 0 && r.getMaxRecords() <= cnt) {
						nextToken = i;
						break;
					}
					boolean select = false;
					if (!all) {
						for (final String s : r.getInstanceIds()) {
							if (s.equals(i)) {
								select = true;
								break;
							}
						}
					} else {
						select = true;
					}
					if (select) {
						cnt++;
						final AutoScalingInstanceDetails b = new AutoScalingInstanceDetails();
						b.setAutoScalingGroupName(asg.getName());
						// b.setAvailabilityZone(availabilityZone);
						// b.setHealthStatus(healthStatus);
						b.setInstanceId(i);
						b.setLaunchConfigurationName(asg.getLaunchConfig());
						// b.setLifecycleState(lifecycleState);
						autoScalingInstances.add(b);
					}

				}
			}
		}
		ret.setAutoScalingInstances(autoScalingInstances);
		ret.setNextToken(nextToken);
		if (r.getNextToken() != null && autoScalingInstances.size() == 0) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		logger.debug("Response " + ret);

		return ret;
	}

	public DescribeAutoScalingInstancesRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DescribeAutoScalingInstancesRequest req = new DescribeAutoScalingInstancesRequest();
		req.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		req.setNextToken(QueryUtil.getString(in, "NextToken"));
		final Collection<String> ids = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("InstanceIds.member." + i)) {
				break;
			}
			ids.add(QueryUtil.getString(in, "InstanceIds.member." + i));
		}
		req.setInstanceIds(ids);
		return req;
	}
}
