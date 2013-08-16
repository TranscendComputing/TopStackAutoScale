package com.transcend.autoscale.worker;


import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.StringHelper;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.UpdateAutoScalingGroupMessage.UpdateAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.UpdateAutoScalingGroupMessage.UpdateAutoScalingGroupResultMessage;

public class UpdateAutoScalingGroupWorker extends
        AbstractWorker<UpdateAutoScalingGroupRequestMessage,
        UpdateAutoScalingGroupResultMessage> {
    private final Logger logger = Appctx.getLogger(UpdateAutoScalingGroupWorker.class
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
    public UpdateAutoScalingGroupResultMessage doWork(
            UpdateAutoScalingGroupRequestMessage req) throws Exception {
        logger.debug("Performing work for UpdateAutoScalingGroup.");
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
    protected UpdateAutoScalingGroupResultMessage doWork0(UpdateAutoScalingGroupRequestMessage req,
            ServiceRequestContext context) throws Exception {

        final AccountBean account = context.getAccountBean();
        Session session = getSession();
		final ASGroupBean g = ASUtil.readASGroup(session, account.getId(),
                req.getAutoScalingGroupName());
		
		
		if (g == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		if (req.hasDefaultCooldown()) {
			g.setCooldown(req.getDefaultCooldown());
		}
		if (req.hasLaunchConfigurationName()) {
			g.setLaunchConfig(req.getLaunchConfigurationName());
		}
		if (req.hasHealthCheckGracePeriod()) {
			if (req.getHealthCheckGracePeriod() < 0) {
				throw QueryFaults
						.InvalidParameterValue("HealthCheckGracePeriod cannot be < 0");
			}
			g.setHealthCheckGracePeriod(req.getHealthCheckGracePeriod());
		}
		if (req.hasHealthCheckType()){
			g.setHealthCheckType(req.getHealthCheckType());
		}
		if (req.hasMinSize()) {
			if (req.getMinSize() < 0) {
				throw QueryFaults
						.InvalidParameterValue("MinSize cannot be < 0");
			}
			g.setMinSz(req.getMinSize());
		}
		if (req.hasMaxSize()) {
			if (req.getMaxSize() < 0) {
				throw QueryFaults
						.InvalidParameterValue("MaxSize cannot be < 0");
			}
			if (req.getMaxSize() < g.getMinSz()) {
				throw QueryFaults
						.InvalidParameterValue("MaxSize cannot be < MinSize");
			}
			g.setMaxSz(req.getMaxSize());
		}
		if (req.hasDesiredCapacity()) {
			g.setCapacity(req.getDesiredCapacity());
		}
		if (g.getCapacity() < g.getMinSz()) {
			g.setCapacity(g.getMinSz());
		}
		if (g.getCapacity() > g.getMaxSz()) {
			g.setCapacity(g.getMaxSz());
		}
		if (req.getAvailabilityZoneCount() > 0) {
			g.setAvzones(StringHelper.concat(
					req.getAvailabilityZoneList().toArray(new String[1]), ","));
		}	
		
		session.save(g);
		
        final UpdateAutoScalingGroupResultMessage.Builder result =
        		UpdateAutoScalingGroupResultMessage.newBuilder();

        return result.buildPartial();
     
	}
}