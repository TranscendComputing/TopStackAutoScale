package com.msi.tough.query.autoscale.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.DeletePolicyRequest;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DeletePolicy extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DeletePolicy");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DeletePolicyResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "DeletePolicyResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final DeletePolicyRequest r = unmarshall(map);
		final AccountBean ac = getAccountBean();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				r.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final Query q = session.createQuery("from ASPolicyBean where userId="
				+ ac.getId() + " and grpName='" + r.getAutoScalingGroupName()
				+ "' and name='" + r.getPolicyName() + "'");
		final List<ASPolicyBean> l = q.list();
		if (l == null || l.size() == 0) {
			throw AutoScaleQueryFaults.policyDoesNotExist();
		}
		final ASPolicyBean asp = l.get(0);
		session.delete(asp);
		return null;
	}

	public DeletePolicyRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final DeletePolicyRequest req = new DeletePolicyRequest();
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		req.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
		return req;
	}
}
