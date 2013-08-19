package com.transcend.autoscale.worker;


import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.DateUtils;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.PutScheduledUpdateGroupActionMessage.PutScheduledUpdateGroupActionRequestMessage;
import com.transcend.autoscale.message.PutScheduledUpdateGroupActionMessage.PutScheduledUpdateGroupActionResultMessage;

public class PutScheduledUpdateGroupActionWorker extends
        AbstractWorker<PutScheduledUpdateGroupActionRequestMessage,
        PutScheduledUpdateGroupActionResultMessage> {
    private final Logger logger = Appctx.getLogger(PutScheduledUpdateGroupActionWorker.class
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
    public PutScheduledUpdateGroupActionResultMessage doWork(
    		PutScheduledUpdateGroupActionRequestMessage req) throws Exception {
        logger.debug("Performing work for PutScheduledUpdateGroupAction.");
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
    protected PutScheduledUpdateGroupActionResultMessage doWork0(PutScheduledUpdateGroupActionRequestMessage req,
            ServiceRequestContext context) throws Exception {

		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				req.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		ASScheduledBean b = ASUtil.readScheduled(session, ac.getId(),
				req.getAutoScalingGroupName(), req.getScheduledActionName());
		if (b == null) {
			b = new ASScheduledBean();
		}
		b.setCapacity(req.getDesiredCapacity());
		if(req.getEndTime() != "")
			b.setEndTime(new DateUtils().parseIso8601Date(req.getEndTime()));
		b.setGrpName(req.getAutoScalingGroupName());
		b.setName(req.getScheduledActionName());
		b.setRecurrence(req.getRecurrence());
		if(req.getStartTime() != "")
			b.setStartTime(new DateUtils().parseIso8601Date(req.getStartTime()));
		b.setMaxSize(req.getMaxSize());
		b.setMinSize(req.getMinSize());
		b.setUserId(ac.getId());
		session.save(b);

      final PutScheduledUpdateGroupActionResultMessage.Builder result =
    		  PutScheduledUpdateGroupActionResultMessage.newBuilder();

      return result.buildPartial();

	}
}