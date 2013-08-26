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
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.SetDesiredCapacityMessage.SetDesiredCapacityRequestMessage;
import com.transcend.autoscale.message.SetDesiredCapacityMessage.SetDesiredCapacityResultMessage;

public class SetDesiredCapacityWorker extends
        AbstractWorker<SetDesiredCapacityRequestMessage,
        SetDesiredCapacityResultMessage> {
    private final Logger logger = Appctx.getLogger(SetDesiredCapacityWorker.class
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
    public SetDesiredCapacityResultMessage doWork(
            SetDesiredCapacityRequestMessage req) throws Exception {
        logger.debug("Performing work for SetDesiredCapacity.");
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
    protected SetDesiredCapacityResultMessage doWork0(SetDesiredCapacityRequestMessage req,
            ServiceRequestContext context) throws Exception {

        final Session session = getSession();
        final AccountBean ac = context.getAccountBean();
        final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
                req.getAutoScalingGroupName());
        if (en == null) {
            throw AutoScaleQueryFaults.groupDoesNotExist();
        }
        if (req.getDesiredCapacity() < 0) {
            throw AutoScaleQueryFaults.desiredCapacityNegative(req.getDesiredCapacity());
        }
        en.setCapacity(req.getDesiredCapacity());
        session.save(en);

      final SetDesiredCapacityResultMessage.Builder result =
              SetDesiredCapacityResultMessage.newBuilder();

      return result.buildPartial();

    }
}
