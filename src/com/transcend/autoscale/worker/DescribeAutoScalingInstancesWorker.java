package com.transcend.autoscale.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeAutoScalingInstancesMessage.DescribeAutoScalingInstancesRequestMessage;
import com.transcend.autoscale.message.DescribeAutoScalingInstancesMessage.DescribeAutoScalingInstancesResultMessage;
import com.transcend.autoscale.message.DescribeAutoScalingInstancesMessage.DescribeAutoScalingInstancesResultMessage.AutoScalingInstanceDetails;

public class DescribeAutoScalingInstancesWorker extends
        AbstractWorker<DescribeAutoScalingInstancesRequestMessage,
        DescribeAutoScalingInstancesResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeAutoScalingInstancesWorker.class
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
    public DescribeAutoScalingInstancesResultMessage doWork(
            DescribeAutoScalingInstancesRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeAutoScalingInstances.");
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
    protected DescribeAutoScalingInstancesResultMessage doWork0(DescribeAutoScalingInstancesRequestMessage req,
            ServiceRequestContext context) throws Exception {

		final DescribeAutoScalingInstancesResultMessage.Builder result = DescribeAutoScalingInstancesResultMessage.newBuilder();
		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final Collection<AutoScalingInstanceDetails> autoScalingInstances = new ArrayList<AutoScalingInstanceDetails>();
		final List<ASGroupBean> asgl = ASUtil.readASGroup(session, ac.getId());
		String nextToken = null;
		int cnt = 0;
		boolean all = false;
		if (req.getInstanceIdsList() == null || req.getInstanceIdsCount() == 0) {
			all = true;
		}
		if (asgl != null) {
			for (final ASGroupBean asg : asgl) {
				final CommaObject il = new CommaObject(asg.getInstances());
				for (final String i : il.toList()) {
					if (req.getNextToken() != null
							&& i.compareTo(req.getNextToken()) < 0) {
						continue;
					}
					if (req.getMaxRecords() != 0 && req.getMaxRecords() <= cnt) {
						nextToken = i;
						break;
					}
					boolean select = false;
					if (!all) {
						for (final String s : req.getInstanceIdsList()) {
							if (s.equals(i)) {
								select = true;
								break;
							}
						}
					} else {
						select = true;
					}
					if (select) {
						cnt++;
						final AutoScalingInstanceDetails.Builder b = AutoScalingInstanceDetails.newBuilder();
						b.setAutoScalingGroupName(asg.getName());
						// b.setAvailabilityZone(availabilityZone);
						// b.setHealthStatus(healthStatus);
						b.setInstanceId(i);
						b.setLaunchConfigurationName(asg.getLaunchConfig());
						// b.setLifecycleState(lifecycleState);
						autoScalingInstances.add(b.build());
					}

				}
			}
		}
	 	result.addAllAutoScalingInstances(new ArrayList<AutoScalingInstanceDetails>(autoScalingInstances));
		result.setNextToken(Strings.nullToEmpty(nextToken));
		if (req.getNextToken() != "" && autoScalingInstances.size() == 0) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}
		logger.debug("Response " + result.buildPartial());


      return result.buildPartial();

	}
}