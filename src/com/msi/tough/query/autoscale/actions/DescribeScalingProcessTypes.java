package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.yammer.metrics.core.Meter;

public class DescribeScalingProcessTypes extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeScalingProcessTypes");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DescribeScalingProcessTypesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeScalingProcessTypesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final XMLNode types = QueryUtil.addNode(xr, "Processes");
		QueryUtil.addNode(types, "ProcessType", "Launch");
		QueryUtil.addNode(types, "ProcessType", "Terminate");
		QueryUtil.addNode(types, "ProcessType", "AddToLoadBalancer");
		QueryUtil.addNode(types, "ProcessType", "HealthCheck");
		QueryUtil.addNode(types, "ProcessType", "ReplaceUnhealthy");
		QueryUtil.addNode(types, "ProcessType", "ScheduledActions");
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		return null;
	}
}
