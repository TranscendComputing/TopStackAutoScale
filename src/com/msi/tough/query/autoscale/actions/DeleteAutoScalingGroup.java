package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.transform.DeleteAutoScalingGroupRequestUnmarshallerVO;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.yammer.metrics.core.Meter;

public class DeleteAutoScalingGroup extends AbstractAction<Object> {
	// private final static Logger logger = Appctx
	// .getLogger(DeleteAutoScalingGroup.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DeleteAutoScalingGroup");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DeleteAutoScalingGroupResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil
				.addNode(xn, "DeleteAutoScalingGroupResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final DeleteAutoScalingGroupRequest r = DeleteAutoScalingGroupRequestUnmarshallerVO
		.getInstance().unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}

		CFUtil.deleteAsyncStackResources(AccountUtil.toAccount(ac), "__as_"
				+ ac.getId() + "_" + r.getAutoScalingGroupName(), null,
				r.getAutoScalingGroupName());
		return null;
	}
}
