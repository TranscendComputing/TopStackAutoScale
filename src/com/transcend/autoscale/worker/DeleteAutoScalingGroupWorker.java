package com.transcend.autoscale.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DeleteAutoScalingGroupMessage.DeleteAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.DeleteAutoScalingGroupMessage.DeleteAutoScalingGroupResultMessage;

public class DeleteAutoScalingGroupWorker extends
        AbstractWorker<DeleteAutoScalingGroupRequestMessage,
        DeleteAutoScalingGroupResultMessage> {
    private final Logger logger = Appctx.getLogger(DeleteAutoScalingGroupWorker.class
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
    public DeleteAutoScalingGroupResultMessage doWork(
            DeleteAutoScalingGroupRequestMessage req) throws Exception {
        logger.debug("Performing work for DeleteAutoScalingGroup.");
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
    protected DeleteAutoScalingGroupResultMessage doWork0(DeleteAutoScalingGroupRequestMessage req,
            ServiceRequestContext context) throws Exception {

		final AccountBean ac = context.getAccountBean();
        Session session = getSession();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				req.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}

		CFUtil.deleteAsyncStackResources(AccountUtil.toAccount(ac), "__as_"
				+ ac.getId() + "_" + req.getAutoScalingGroupName(), null,
				req.getAutoScalingGroupName());

	final DeleteAutoScalingGroupResultMessage.Builder result =
    		DeleteAutoScalingGroupResultMessage.newBuilder();

    return result.buildPartial();

    }
}