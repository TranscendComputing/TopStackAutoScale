package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.transform.DescribeAutoScalingGroupsRequestUnmarshallerVO;
import com.amazonaws.services.autoscaling.model.transform.DescribeAutoScalingGroupsResultMarshallerVO;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DescribeAutoScalingGroups extends
		AbstractAction<DescribeAutoScalingGroupsResult> {
	private final static Logger logger = Appctx
			.getLogger(DescribeAutoScalingGroups.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeAutoScalingGroups");

	@Override
	protected void mark(DescribeAutoScalingGroupsResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(
			final MarshallStruct<DescribeAutoScalingGroupsResult> input,
			final HttpServletResponse resp) throws Exception {
		return new DescribeAutoScalingGroupsResultMarshallerVO().marshall(input);
	}

	@Override
	public DescribeAutoScalingGroupsResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {

		final DescribeAutoScalingGroupsRequest r = DescribeAutoScalingGroupsRequestUnmarshallerVO.getInstance().unmarshall(map);
		
		final AccountBean ac = getAccountBean();
		final DescribeAutoScalingGroupsResult ret = new DescribeAutoScalingGroupsResult();

		final List<ASGroupBean> grpl = ASUtil.readASGroup(session, ac.getId());

		final List<AutoScalingGroup> autoScalingGroups = new ArrayList<AutoScalingGroup>();
		String nextToken = null;
		String firstToken = null;
		int cnt = 0;
		boolean all = false;
		if (r.getAutoScalingGroupNames() == null
				|| r.getAutoScalingGroupNames().size() == 0) {
			all = true;
		}
		if (grpl != null) {
			for (final ASGroupBean en : grpl) {
				if (r.getNextToken() != null
						&& en.getName().compareTo(r.getNextToken()) < 0) {
					continue;
				}
				if (r.getMaxRecords() != 0 && r.getMaxRecords() <= cnt) {
					nextToken = en.getName();
					break;
				}
				boolean select = false;
				if (!all) {
					for (final String s : r.getAutoScalingGroupNames()) {
						if (s.equals(en.getName())) {
							select = true;
							break;
						}
					}
				} else {
					select = true;
				}
				if (select) {
					cnt++;
					autoScalingGroups.add(AutoScaleQueryUtil
							.toAutoScalingGroup(session, ac.getId(), en));
					if (firstToken == null) {
						firstToken = en.getName();
					}
				}
			}
		}

		ret.setAutoScalingGroups(autoScalingGroups);
		ret.setNextToken(nextToken);
		if (r.getNextToken() != null
				&& (firstToken == null || !r.getNextToken().equals(firstToken))) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		return ret;
	}
}
