package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.ResumeProcessesRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class ResumeProcesses extends AbstractAction<Object> {
	private final static Logger logger = Appctx.getLogger(ResumeProcesses.class
			.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"ResumeProcesses");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("ResumeProcessesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "ResumeProcessesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final ResumeProcessesRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final CommaObject co = new CommaObject(en.getSuspend());
		final List<String> l = co.getList();
		for (final String rem : r.getScalingProcesses()) {
			l.remove(rem);
		}
		final CommaObject c = new CommaObject(l);
		en.setSuspend(c.toString());
		session.save(en);
		return null;
	}

	public ResumeProcessesRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final ResumeProcessesRequest req = new ResumeProcessesRequest();
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
