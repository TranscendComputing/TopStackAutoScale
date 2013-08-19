package com.transcend.autoscale.worker;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.TerminateInstanceInAutoScalingGroupMessage.TerminateInstanceInAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.TerminateInstanceInAutoScalingGroupMessage.TerminateInstanceInAutoScalingGroupResultMessage;

public class TerminateInstanceInAutoScalingGroupWorker extends
        AbstractWorker<TerminateInstanceInAutoScalingGroupRequestMessage,
        TerminateInstanceInAutoScalingGroupResultMessage> {
    private final Logger logger = Appctx.getLogger(TerminateInstanceInAutoScalingGroupWorker.class
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
    public TerminateInstanceInAutoScalingGroupResultMessage doWork(
           TerminateInstanceInAutoScalingGroupRequestMessage req) throws Exception {
        logger.debug("Performing work for TerminateInstanceInAutoScalingGroup.");
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
    protected TerminateInstanceInAutoScalingGroupResultMessage doWork0(TerminateInstanceInAutoScalingGroupRequestMessage req,
            ServiceRequestContext context) throws Exception {

		final AccountBean account = context.getAccountBean();
		Session session = getSession();
		final List<ASGroupBean> gs = ASUtil.readASGroup(session, account.getId());
		ASGroupBean g = null;
		for (final ASGroupBean g0 : gs) {
			final CommaObject insts = new CommaObject(g0.getInstances());
			for (final String i : insts.toList()) {
				if (req.getInstanceId().equals(i)) {
					g = g0;
					break;
				}
			}
		}

		if (g == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		if (g.getMinSz() == g.getCapacity()
				&& req.hasShouldDecrementDesiredCapacity()
				&& req.getShouldDecrementDesiredCapacity()) {
			throw AutoScaleQueryFaults.cannotReduceCapacity(g.getMinSz());
		}
		g.setReduceCapacity(req.getShouldDecrementDesiredCapacity());

		session.save(g);
		final TerminateInstanceInAutoScalingGroupResultMessage.Builder result =
		        TerminateInstanceInAutoScalingGroupResultMessage.newBuilder();

       return result.buildPartial();

	}

}