package com.transcend.autoscale.worker;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.PutScalingPolicyMessage.PutScalingPolicyRequestMessage;
import com.transcend.autoscale.message.PutScalingPolicyMessage.PutScalingPolicyResultMessage;

public class PutScalingPolicyWorker extends
        AbstractWorker<PutScalingPolicyRequestMessage,
        PutScalingPolicyResultMessage> {
    private final Logger logger = Appctx.getLogger(PutScalingPolicyWorker.class
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
    public PutScalingPolicyResultMessage doWork(
    		PutScalingPolicyRequestMessage req) throws Exception {
        logger.debug("Performing work for PutScalingPolicy.");
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
    protected PutScalingPolicyResultMessage doWork0(PutScalingPolicyRequestMessage req,
            ServiceRequestContext context) throws Exception {
    	
		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				req.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.policyDoesNotExist();
		}
		if (req.hasMinAdjustmentStep() && req.getMinAdjustmentStep() > 0
				&& !req.getAdjustmentType().equals("PercentChangeInCapacity")) {
			throw QueryFaults
					.InvalidParameterCombination("MinAdjustmentStep is valid only with an AdjustmentType PercentChangeInCapacity");
		}
		{
			final ASPolicyBean b = ASUtil.readASPolicy(session, ac.getId(),
					req.getPolicyName());
			if (b != null) {
				throw AutoScaleQueryFaults.alreadyExists();
			}
		}

		final ASPolicyBean asp = new ASPolicyBean();
		asp.setAdjustmentType(req.getAdjustmentType());
		asp.setCooldown(req.getCooldown());
		asp.setCreatedDate(new Date());
		asp.setGrpName(req.getAutoScalingGroupName());
		asp.setName(req.getPolicyName());
		asp.setScalingAdjustment(req.getScalingAdjustment());
		asp.setUserId(ac.getId());
		asp.setMinAdjustmentStep(req.getMinAdjustmentStep());
		asp.setArn("arn:autoscaling:policy:" + ac.getId() + ":"
				+ req.getPolicyName());
		session.save(asp);
		
      final PutScalingPolicyResultMessage.Builder result =
    		  PutScalingPolicyResultMessage.newBuilder();

      result.setPolicyARN(asp.getArn());
      return result.buildPartial();
      
	}
}