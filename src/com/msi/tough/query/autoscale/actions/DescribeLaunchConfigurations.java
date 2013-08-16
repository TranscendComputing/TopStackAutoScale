package com.msi.tough.query.autoscale.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsRequest;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsResult;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.transform.DescribeLaunchConfigurationsRequestUnmarshallerVO;
import com.amazonaws.services.autoscaling.model.transform.DescribeLaunchConfigurationsResultMarshallerVO;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.yammer.metrics.core.Meter;

public class DescribeLaunchConfigurations extends
		AbstractAction<DescribeLaunchConfigurationsResult> {
	private final static Logger logger = Appctx
			.getLogger(DescribeLaunchConfigurations.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"DescribeLaunchConfigurations");

	@Override
	protected void mark(DescribeLaunchConfigurationsResult ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(
			final MarshallStruct<DescribeLaunchConfigurationsResult> input,
			final HttpServletResponse resp) throws Exception {
		return new DescribeLaunchConfigurationsResultMarshallerVO()
				.marshall(input);
	}

	@Override
	public DescribeLaunchConfigurationsResult process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
		final DescribeLaunchConfigurationsRequest r = DescribeLaunchConfigurationsRequestUnmarshallerVO
				.getInstance().unmarshall(map);
		final AccountBean ac = getAccountBean();
		final DescribeLaunchConfigurationsResult ret = new DescribeLaunchConfigurationsResult();
		ret.setNextToken(null);
		final List<LaunchConfiguration> launchConfigurations = new ArrayList<LaunchConfiguration>();
		boolean all = false;
		int cnt = 0;
		if (r.getLaunchConfigurationNames() == null
				|| r.getLaunchConfigurationNames().size() == 0) {
			all = true;
		}

		Integer maxRecords = r.getMaxRecords();

		if (maxRecords != null && maxRecords <= 0) {
			throw QueryFaults
					.InvalidParameterValue("MaxRecords cannot be 0 or negative");
		}
		if (maxRecords == null) {
			maxRecords = 0;
		}

		final List<LaunchConfigBean> cfgs = ASUtil.readLaunchConfig(session,
				ac.getId());

		String nextToken = null;
		String firstToken = null;
		if (cfgs != null) {
			for (final LaunchConfigBean en : cfgs) {
				if (r.getNextToken() != null
						&& en.getName().compareTo(r.getNextToken()) < 0) {
					continue;
				}
				if (maxRecords != 0 && maxRecords <= cnt) {
					nextToken = en.getName();
					break;
				}
				boolean select = false;
				if (!all) {
					for (final String s : r.getLaunchConfigurationNames()) {
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
					launchConfigurations.add(AutoScaleQueryUtil
							.toLaunchConfiguration(en));
					if (firstToken == null) {
						firstToken = en.getName();
					}
				}
			}
		}
		ret.setLaunchConfigurations(launchConfigurations);
		ret.setNextToken(nextToken);
		if (r.getNextToken() != null
				&& (firstToken == null || !r.getNextToken().equals(firstToken))) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		logger.debug("Response " + ret);
		return ret;
	}
}
