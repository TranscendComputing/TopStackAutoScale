package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.DescribeScheduledActionsRequest;
import com.amazonaws.services.autoscaling.model.DescribeScheduledActionsResult;
import com.amazonaws.services.autoscaling.model.ScheduledUpdateGroupAction;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DescribeScheduledActions extends
		AbstractAction<DescribeScheduledActionsResult> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeScheduledActions");

	@Override
	protected void mark(DescribeScheduledActionsResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(
			final MarshallStruct<DescribeScheduledActionsResult> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DescribeScheduledActionsResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeScheduledActionsResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final XMLNode xmain = QueryUtil.addNode(xr,
				"ScheduledUpdateGroupAction");
		final List<ScheduledUpdateGroupAction> o = input.getMainObject()
				.getScheduledUpdateGroupActions();
		if (o != null) {
			for (final ScheduledUpdateGroupAction i : o) {
				final XMLNode en = QueryUtil.addNode(xmain, "member");
				AutoScaleQueryUtil.marshallScheduledAction(en, i);
			}
		}

		return xn.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DescribeScheduledActionsResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
		final DescribeScheduledActionsRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final DescribeScheduledActionsResult res = new DescribeScheduledActionsResult();
		final Collection<ScheduledUpdateGroupAction> actions = new ArrayList<ScheduledUpdateGroupAction>();
		final Query q = session
				.createQuery("from ASScheduledBean where userId=" + ac.getId()
						+ " and grpName='" + r.getAutoScalingGroupName() + "'");
		final List<ASScheduledBean> l = q.list();
		for (final ASScheduledBean b : l) {
			if (r.getScheduledActionNames() != null
					&& r.getScheduledActionNames().size() > 0
					&& !r.getScheduledActionNames().contains(b.getName())) {
				continue;
			}
			final ScheduledUpdateGroupAction action = new ScheduledUpdateGroupAction();
			action.setAutoScalingGroupName(r.getAutoScalingGroupName());
			action.setDesiredCapacity(b.getCapacity());
			action.setEndTime(b.getEndTime());
			action.setMaxSize(b.getMaxSize());
			action.setMinSize(b.getMinSize());
			action.setRecurrence(b.getRecurrence());
			action.setScheduledActionName(b.getName());
			action.setStartTime(b.getStartTime());
			actions.add(action);
		}
		res.setScheduledUpdateGroupActions(actions);
		return res;
	}

	public DescribeScheduledActionsRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DescribeScheduledActionsRequest req = new DescribeScheduledActionsRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		req.setNextToken(QueryUtil.getString(in, "NextToken"));
		final Collection<String> names = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("ScheduledActionNames.member." + i)) {
				break;
			}
			names.add(QueryUtil.getString(in, "ScheduledActionNames.member."
					+ i));
		}
		req.setScheduledActionNames(names);
		// req.setEndTime(endTime);
		// req.setStartTime(startTime);
		return req;
	}
}
