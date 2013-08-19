package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.PutScheduledUpdateGroupActionRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class PutScheduledUpdateGroupAction extends AbstractAction<Object> {
	private final static Logger logger = Appctx
			.getLogger(PutScheduledUpdateGroupAction.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"PutScheduledUpdateGroupAction");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("PutScheduledUpdateGroupActionResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn,
				"PutScheduledUpdateGroupActionResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final PutScheduledUpdateGroupActionRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		ASScheduledBean b = ASUtil.readScheduled(session, ac.getId(),
				r.getAutoScalingGroupName(), r.getScheduledActionName());
		if (b == null) {
			b = new ASScheduledBean();
		}
		b.setCapacity(r.getDesiredCapacity());
		b.setEndTime(r.getEndTime());
		b.setGrpName(r.getAutoScalingGroupName());
		b.setName(r.getScheduledActionName());
		b.setRecurrence(r.getRecurrence());
		b.setStartTime(r.getStartTime());
		b.setMaxSize(r.getMaxSize());
		b.setMinSize(r.getMinSize());
		b.setUserId(ac.getId());
		session.save(b);
		return null;
	}

	public PutScheduledUpdateGroupActionRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final PutScheduledUpdateGroupActionRequest req = new PutScheduledUpdateGroupActionRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setDesiredCapacity(QueryUtil.getInt(in, "DesiredCapacity"));
		// req.setEndTime(QueryUtil.getString(in, "EndTime"));
		req.setMaxSize(QueryUtil.getInt(in, "MaxSize"));
		req.setMinSize(QueryUtil.getInt(in, "MinSize"));
		req.setRecurrence(QueryUtil.requiredString(in, "Recurrence"));
		req.setScheduledActionName(QueryUtil.requiredString(in,
				"ScheduledActionName"));
		// req.setStartTime(QueryUtil.requiredString(in, "StartTime"));
		if (in.containsKey("Time")) {
			// req.setTime(QueryUtil.requiredString(in, "Time"));
		}
		return req;
	}
}
