package com.transcend.autoscale.worker;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.autoscaling.model.SetInstanceHealthRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.SetInstanceHealthMessage.SetInstanceHealthRequestMessage;
import com.transcend.autoscale.message.SetInstanceHealthMessage.SetInstanceHealthResultMessage;

public class SetInstanceHealthWorker extends
        AbstractWorker<SetInstanceHealthRequestMessage,
        SetInstanceHealthResultMessage> {
    private final Logger logger = Appctx.getLogger(SetInstanceHealthWorker.class
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
    public SetInstanceHealthResultMessage doWork(
    		SetInstanceHealthRequestMessage req) throws Exception {
        logger.debug("Performing work for SetInstanceHealth.");
        return super.doWork(req, getSession());
    }
    

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.workflow.core.AbstractWorker#doWork0(com.google.protobuf
     * .Message, com.msi.tough.query.ServiceRequestContext)
     */
	@SuppressWarnings("unchecked")
    @Override
    @Transactional
    protected SetInstanceHealthResultMessage doWork0(SetInstanceHealthRequestMessage req,
            ServiceRequestContext context) throws Exception {
    	
		final AccountBean ac = context.getAccountBean();
	      final SetInstanceHealthResultMessage.Builder result =
	    		  SetInstanceHealthResultMessage.newBuilder();

	      return result.buildPartial();
      
	}

}