package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.SuspendProcessesRequest;
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

public class SuspendProcesses extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"SuspendProcesses");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("SuspendProcessesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final SuspendProcessesRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final CommaObject co = new CommaObject(r.getScalingProcesses());
		en.setSuspend(co.toString());
		session.save(en);
		return null;
	}

	public SuspendProcessesRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final SuspendProcessesRequest req = new SuspendProcessesRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		final Collection<String> scalingProcesses = new ArrayList<String>();
		for (int i = 1;; i++) {
			if (!in.containsKey("ScalingProcesses.member." + i)) {
				break;
			}
			final String process = in.get("ScalingProcesses.member." + i)[0];
			scalingProcesses.add(process);
		}
		req.setScalingProcesses(scalingProcesses);
		return req;
	}
}
