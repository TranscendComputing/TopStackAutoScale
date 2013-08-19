package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.SetInstanceHealthRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.yammer.metrics.core.Meter;

public class SetInstanceHealth extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"SetInstanceHealth");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("SetInstanceHealthResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "SetInstanceHealthResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		return null;
	}

	public SetInstanceHealthRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final SetInstanceHealthRequest req = new SetInstanceHealthRequest();
		req.setHealthStatus(QueryUtil.requiredString(in, "HealthStatus"));
		req.setInstanceId(QueryUtil.requiredString(in, "InstanceId"));
		req.setShouldRespectGracePeriod(QueryUtil.requiredBoolean(in,
				"ShouldRespectGracePeriod"));
		return req;
	}
}
