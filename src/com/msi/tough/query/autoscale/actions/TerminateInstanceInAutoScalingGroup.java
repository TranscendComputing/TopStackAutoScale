package com.msi.tough.query.autoscale.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class TerminateInstanceInAutoScalingGroup extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"TerminateInstanceInAutoScalingGroup");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode(
				"TerminateInstanceInAutoScalingGroupResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "TerminateInstanceInAutoScalingGroupResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final TerminateInstanceInAutoScalingGroupRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();

		final List<ASGroupBean> gs = ASUtil.readASGroup(session, ac.getId());
		ASGroupBean g = null;
		for (final ASGroupBean g0 : gs) {
			final CommaObject insts = new CommaObject(g0.getInstances());
			for (final String i : insts.toList()) {
				if (r.getInstanceId().equals(i)) {
					g = g0;
					break;
				}
			}
		}
		if (g == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		if (g.getMinSz() == g.getCapacity()
				&& r.getShouldDecrementDesiredCapacity() != null
				&& r.getShouldDecrementDesiredCapacity()) {
			throw AutoScaleQueryFaults.cannotReduceCapacity(g.getMinSz());
		}
		g.setTerminateInstance(r.getInstanceId());
		g.setReduceCapacity(r.getShouldDecrementDesiredCapacity());
		session.save(g);
		return null;
	}

	public TerminateInstanceInAutoScalingGroupRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final TerminateInstanceInAutoScalingGroupRequest req = new TerminateInstanceInAutoScalingGroupRequest();
		req.setInstanceId(QueryUtil.requiredString(in, "InstanceId"));
		req.setShouldDecrementDesiredCapacity(QueryUtil.requiredBoolean(in,
				"ShouldDecrementDesiredCapacity"));
		return req;
	}
}
