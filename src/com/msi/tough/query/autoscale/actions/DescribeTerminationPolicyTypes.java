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

public class DescribeTerminationPolicyTypes extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeTerminationPolicyTypes");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DescribeTerminationPolicyTypesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = new XMLNode("DescribeTerminationPolicyTypesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final XMLNode types = QueryUtil.addNode(xr, "TerminationPolicyTypes");
		QueryUtil.addNode(types, "TerminationPolicyType", "OldestInstance");
		QueryUtil.addNode(types, "TerminationPolicyType",
				"OldestLaunchConfiguration");
		QueryUtil.addNode(types, "TerminationPolicyType", "NewestInstance");
		QueryUtil.addNode(types, "TerminationPolicyType",
				"ClosestToNextInstanceHour");
		QueryUtil.addNode(types, "TerminationPolicyType", "Default");
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		return null;
	}
}
