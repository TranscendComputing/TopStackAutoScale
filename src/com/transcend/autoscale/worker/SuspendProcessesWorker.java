package com.transcend.autoscale.worker;

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
import com.transcend.autoscale.message.SuspendProcessesMessage.SuspendProcessesRequestMessage;
import com.transcend.autoscale.message.SuspendProcessesMessage.SuspendProcessesResultMessage;

public class SuspendProcessesWorker extends
        AbstractWorker<SuspendProcessesRequestMessage,
        SuspendProcessesResultMessage> {
    private final Logger logger = Appctx.getLogger(SuspendProcessesWorker.class
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
    public SuspendProcessesResultMessage doWork(
            SuspendProcessesRequestMessage req) throws Exception {
        logger.debug("Performing work for SuspendProcesses.");
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
    protected SuspendProcessesResultMessage doWork0(SuspendProcessesRequestMessage req,
            ServiceRequestContext context) throws Exception {

        final AccountBean ac = context.getAccountBean();
        Session session = getSession();

        final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
                req.getAutoScalingGroupName());
        if (en == null) {
            throw AutoScaleQueryFaults.groupDoesNotExist();
        }
        final CommaObject co = new CommaObject(req.getScalingProcessesList());
        en.setSuspend(co.toString());
        session.save(en);

      final SuspendProcessesResultMessage.Builder result =
              SuspendProcessesResultMessage.newBuilder();

      return result.buildPartial();

    }
}
