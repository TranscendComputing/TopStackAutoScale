package com.msi.tough.query.autoscale.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.transform.DeleteLaunchConfigurationRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DeleteLaunchConfiguration extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DeleteLaunchConfiguration");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("DeleteLaunchConfigurationResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		QueryUtil.addNode(xn, "DeleteLaunchConfigurationResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final DeleteLaunchConfigurationRequest r = DeleteLaunchConfigurationRequestUnmarshaller
				.getInstance().unmarshall(map);
		final AccountBean ac = getAccountBean();

		final LaunchConfigBean en = ASUtil.readLaunchConfig(session,
				ac.getId(), r.getLaunchConfigurationName());
		if (en == null) {
			throw AutoScaleQueryFaults.launchConfigDoesNotExist();
		}
		final List<ASGroupBean> grps = ASUtil.readASGroup(session, ac.getId());
		if (grps != null) {
			for (final ASGroupBean g : grps) {
				if (g.getLaunchConfig().equals(r.getLaunchConfigurationName())) {
					throw AutoScaleQueryFaults.launchConfigInuse(g.getName());
				}
			}
		}
		session.delete(en);
		return null;
	}
}
