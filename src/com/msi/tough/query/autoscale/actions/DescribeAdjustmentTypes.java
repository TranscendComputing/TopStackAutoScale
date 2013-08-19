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

public class DescribeAdjustmentTypes extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeAdjustmentTypes");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DescribeAdjustmentTypesResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"DescribeAdjustmentTypesResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());

		final XMLNode types = QueryUtil.addNode(xr, "AdjustmentTypes");
		{
			final XMLNode m = QueryUtil.addNode(types, "member");
			QueryUtil.addNode(m, "AdjustmentType", "ChangeInCapacity");
		}
		{
			final XMLNode m = QueryUtil.addNode(types, "member");
			QueryUtil.addNode(m, "AdjustmentType", "ExactCapacity");
		}
		{
			final XMLNode m = QueryUtil.addNode(types, "member");
			QueryUtil.addNode(m, "AdjustmentType", "PercentChangeInCapacity");
		}
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		return null;
	}
}
