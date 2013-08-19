package com.transcend.autoscale.worker;
import java.util.List;

import org.hibernate.Query;
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
import com.transcend.autoscale.message.DeletePolicyMessage.DeletePolicyRequestMessage;
import com.transcend.autoscale.message.DeletePolicyMessage.DeletePolicyResultMessage;

public class DeletePolicyWorker extends
        AbstractWorker<DeletePolicyRequestMessage,
        DeletePolicyResultMessage> {
    private final Logger logger = Appctx.getLogger(DeletePolicyWorker.class
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
    public DeletePolicyResultMessage doWork(
    		DeletePolicyRequestMessage req) throws Exception {
        logger.debug("Performing work for DeletePolicy.");
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
    protected DeletePolicyResultMessage doWork0(DeletePolicyRequestMessage req,
            ServiceRequestContext context) throws Exception {

		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				req.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final Query q = session.createQuery("from ASPolicyBean where userId="
				+ ac.getId() + " and grpName='" + req.getAutoScalingGroupName()
				+ "' and name='" + req.getPolicyName() + "'");
		@SuppressWarnings("unchecked")
        final List<ASPolicyBean> l = q.list();
		if (l == null || l.size() == 0) {
			throw AutoScaleQueryFaults.policyDoesNotExist();
		}
		final ASPolicyBean asp = l.get(0);
		session.delete(asp);

	final DeletePolicyResultMessage.Builder result =
    		DeletePolicyResultMessage.newBuilder();

    return result.buildPartial();


	}
}