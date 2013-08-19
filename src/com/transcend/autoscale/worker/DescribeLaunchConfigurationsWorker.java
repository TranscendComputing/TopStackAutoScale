package com.transcend.autoscale.worker;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.DateUtils;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsRequestMessage;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsResultMessage;
import com.transcend.autoscale.message.DescribeLaunchConfigurationsMessage.DescribeLaunchConfigurationsResultMessage.LaunchConfiguration;

public class DescribeLaunchConfigurationsWorker extends
        AbstractWorker<DescribeLaunchConfigurationsRequestMessage,
        DescribeLaunchConfigurationsResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeLaunchConfigurationsWorker.class
            .getName());

    /**
     * We need a local copy of this doWork to provide the transactional
     * annotation.  Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public DescribeLaunchConfigurationsResultMessage doWork(
            DescribeLaunchConfigurationsRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeLaunchConfigurations.");
        return super.doWork(req, getSession());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.workflow.core.AbstractWorker#doWork0(com.google.protobuf
     * .Message, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    @Transactional
    protected DescribeLaunchConfigurationsResultMessage doWork0(DescribeLaunchConfigurationsRequestMessage req,
            ServiceRequestContext context) throws Exception {

		final AccountBean ac = context.getAccountBean();
		final DescribeLaunchConfigurationsResultMessage.Builder result = DescribeLaunchConfigurationsResultMessage.newBuilder();
		result.setNextToken("");
		final Session session = getSession();
		final List<LaunchConfiguration> launchConfigurations = new ArrayList<LaunchConfiguration>();
		boolean all = false;
		int cnt = 0;
		if (req.getLaunchConfigurationNamesCount() <= 0) {
			all = true;
		}

		Integer maxRecords = req.getMaxRecords();

		//if (maxRecords != null && maxRecords <= 0) {
		if (maxRecords != null && maxRecords < 0) {
			throw QueryFaults
					.InvalidParameterValue("MaxRecords cannot be 0 or negative");
		}
		if (maxRecords == null) {
			maxRecords = 0;
		}

		final List<LaunchConfigBean> cfgs = ASUtil.readLaunchConfig(session,
				ac.getId());

		String nextToken = "";
		String firstToken = "";
		if (cfgs != null) {
			for (final LaunchConfigBean en : cfgs) {
				if (req.getNextToken() != ""
						&& en.getName().compareTo(req.getNextToken()) < 0) {
					continue;
				}
				if (maxRecords != 0 && maxRecords <= cnt) {
					nextToken = en.getName();
					break;
				}
				boolean select = false;
				if (!all) {
					for (final String s : req.getLaunchConfigurationNamesList()) {
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
					launchConfigurations.add(toLaunchConfiguration(en));
					if (firstToken == "") {
						firstToken = en.getName();
					}
				}
			}
		}
		result.addAllLaunchConfigurations(launchConfigurations);
		result.setNextToken(Strings.nullToEmpty(nextToken));
		if (req.getNextToken() != ""
				&& (firstToken == "" || !req.getNextToken().equals(firstToken))) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		logger.debug("Response " + result.buildPartial());
		return result.buildPartial();



	}


	public static LaunchConfiguration toLaunchConfiguration(
			final LaunchConfigBean b) {
		final LaunchConfiguration.Builder r = LaunchConfiguration.newBuilder();
		// r.setBlockDeviceMappings(b.getBlockDeviceMappings());
		r.setCreatedTime(new DateUtils().formatIso8601Date(b.getCreatedTime()));
		r.setImageId(b.getImageId());
		// r.setInstanceMonitoring(instanceMonitoring);
		r.setInstanceType(b.getInstType());
		r.setKernelId(Strings.nullToEmpty(b.getKernel()));
		r.setKeyName(Strings.nullToEmpty(b.getKey()));
		// r.setLaunchConfigurationARN(Strings.nullToEmpty(launchConfigurationARN));
		r.setLaunchConfigurationName(b.getName());
		r.setRamdiskId(Strings.nullToEmpty(b.getRamdisk()));
		// r.setSecurityGroups(securityGroups);
		r.setUserData(Strings.nullToEmpty(b.getUserData()));
		return r.buildPartial();
	}
}