package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.Activity;
import com.amazonaws.services.autoscaling.model.DescribeScalingActivitiesRequest;
import com.amazonaws.services.autoscaling.model.DescribeScalingActivitiesResult;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASActivityLog;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DescribeScalingActivities extends
		AbstractAction<DescribeScalingActivitiesResult> {
	private final static Logger logger = Appctx
			.getLogger(DescribeScalingActivities.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeScalingActivities");

	@Override
	protected void mark(DescribeScalingActivitiesResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(
			final MarshallStruct<DescribeScalingActivitiesResult> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DescribeScalingActivitiesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeScalingActivitiesResult");
		final XMLNode na = QueryUtil.addNode(xr, "Activities");

		final DescribeScalingActivitiesResult o = input.getMainObject();
		final List<Activity> actl = o.getActivities();
		for (final Activity a : actl) {
			final XMLNode m = QueryUtil.addNode(na, "member");
			QueryUtil.addNode(m, "ActivityId", a.getActivityId());
			QueryUtil.addNode(m, "AutoScalingGroupName",
					a.getAutoScalingGroupName());
			QueryUtil.addNode(m, "Cause", a.getCause());
			QueryUtil.addNode(m, "Description", a.getDescription());
			QueryUtil.addNode(m, "Details", a.getDetails());
			QueryUtil.addNode(m, "EndTime", a.getEndTime());
			QueryUtil.addNode(m, "Progress", a.getProgress());
			QueryUtil.addNode(m, "StartTime", a.getStartTime());
			QueryUtil.addNode(m, "StatusCode", a.getStatusCode());
			QueryUtil.addNode(m, "StatusMessage", a.getStatusMessage());
		}

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public DescribeScalingActivitiesResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
		final DescribeScalingActivitiesRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final DescribeScalingActivitiesResult ret = new DescribeScalingActivitiesResult();

		final List<ASGroupBean> grpl = ASUtil.readASGroup(session, ac.getId());

		final Collection<Activity> vals = new ArrayList<Activity>();
		final String nextToken = null;
		boolean all = false;
		if (r.getActivityIds() == null || r.getActivityIds().size() == 0) {
			all = true;
		}
		final String grpName = r.getAutoScalingGroupName();
		if (grpl != null) {
			for (final ASGroupBean g : grpl) {
				if (grpName != null && !grpName.equals(g.getName())) {
					continue;
				}
				final List<ASActivityLog> actl = ASUtil.readASActivityLog(
						session, ac.getId(), g.getId());
				for (final ASActivityLog a : actl) {
					// if (r.getNextToken() != null
					// && a.getName().compareTo(r.getNextToken()) < 0) {
					// continue;
					// }
					// if (r.getMaxRecords() != 0 && r.getMaxRecords() <= cnt) {
					// nextToken = g.getName();
					// break;
					// }
					boolean select = false;
					if (!all) {
						for (final String s : r.getActivityIds()) {
							if (s.equals("" + a.getId())) {
								select = true;
								break;
							}
						}
					} else {
						select = true;
					}
					if (select) {
						final Activity activity = new Activity();
						activity.setActivityId("" + a.getId());
						activity.setAutoScalingGroupName(g.getName());
						activity.setCause(a.getCause());
						activity.setDescription(a.getDescription());
						activity.setDetails(a.getDetails());
						activity.setEndTime(a.getEndTime());
						activity.setProgress(a.getProgress());
						activity.setStartTime(a.getStartTime());
						activity.setStatusCode(a.getStatusCode());
						activity.setStatusMessage(a.getStatusMsg());
						vals.add(activity);
					}
				}
			}
		}

		ret.setActivities(vals);
		ret.setNextToken(nextToken);
		if (r.getNextToken() != null && vals.size() == 0) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		logger.debug("Response " + ret);
		return ret;
	}

	public DescribeScalingActivitiesRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final DescribeScalingActivitiesRequest req = new DescribeScalingActivitiesRequest();
		req.setAutoScalingGroupName(QueryUtil.getString(in,
				"AutoScalingGroupName"));
		req.setMaxRecords(QueryUtil.getInt(in, "MaxRecords"));
		req.setNextToken(QueryUtil.getString(in, "NextToken"));
		final Collection<String> ids = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("ActivityIds.member." + i)) {
				break;
			}
			ids.add(QueryUtil.getString(in, "ActivityIds.member." + i));
		}
		req.setActivityIds(ids);
		return req;
	}
}
