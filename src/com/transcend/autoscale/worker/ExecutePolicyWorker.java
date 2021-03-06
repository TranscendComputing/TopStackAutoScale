package com.transcend.autoscale.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.ExecutePolicyMessage.ExecutePolicyRequestMessage;
import com.transcend.autoscale.message.ExecutePolicyMessage.ExecutePolicyResultMessage;

public class ExecutePolicyWorker extends
        AbstractWorker<ExecutePolicyRequestMessage,
        ExecutePolicyResultMessage> {
    private final Logger logger = Appctx.getLogger(ExecutePolicyWorker.class
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
    public ExecutePolicyResultMessage doWork(
            ExecutePolicyRequestMessage req) throws Exception {
        logger.debug("Performing work for ExecutePolicy.");
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
    protected ExecutePolicyResultMessage doWork0(ExecutePolicyRequestMessage req,
            ServiceRequestContext context) throws Exception {
        final Session session = getSession();
        final AccountBean ac = context.getAccountBean();
        final ASGroupBean g = ASUtil.readASGroup(session, ac.getId(),
                req.getAutoScalingGroupName());
        if (g == null) {
            throw AutoScaleQueryFaults.groupDoesNotExist();
        }
        final ASPolicyBean asp = ASUtil.readASPolicy(session, ac.getId(),
                req.getPolicyName());
        if (asp == null) {
            throw AutoScaleQueryFaults.policyDoesNotExist();
        }
        if (req.hasHonorCooldown() && req.getHonorCooldown()
                && g.getCooldownTime() != null
                && g.getCooldownTime().getTime() > System.currentTimeMillis()) {
            throw AutoScaleQueryFaults.scalingActivityInProgress();
        }
        ASUtil.executeASPolicy(session, ac.getId(), req.getPolicyName());

      final ExecutePolicyResultMessage.Builder result = ExecutePolicyResultMessage.newBuilder();
      return result.buildPartial();

    }
}
