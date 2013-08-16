package com.transcend.autoscale.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.autoscaling.model.ResumeProcessesRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.ResumeProcessesMessage.ResumeProcessesRequestMessage;
import com.transcend.autoscale.message.ResumeProcessesMessage.ResumeProcessesResultMessage;

public class ResumeProcessesWorker extends
        AbstractWorker<ResumeProcessesRequestMessage,
        ResumeProcessesResultMessage> {
    private final Logger logger = Appctx.getLogger(ResumeProcessesWorker.class
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
    public ResumeProcessesResultMessage doWork(
            ResumeProcessesRequestMessage req) throws Exception {
        logger.debug("Performing work for ResumeProcesses.");
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
    protected ResumeProcessesResultMessage doWork0(ResumeProcessesRequestMessage req,
            ServiceRequestContext context) throws Exception {
    	
		final AccountBean ac = context.getAccountBean();
		Session session = getSession();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				req.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}
		final CommaObject co = new CommaObject(en.getSuspend());
		final List<String> l = co.getList();
		for (final String rem : req.getScalingProcessesList()) {
			l.remove(rem);
		}
		final CommaObject c = new CommaObject(l);
		en.setSuspend(c.toString());
		session.save(en);
		
      final ResumeProcessesResultMessage.Builder result =
    		  ResumeProcessesResultMessage.newBuilder();

      return result.buildPartial();
      
	}
}