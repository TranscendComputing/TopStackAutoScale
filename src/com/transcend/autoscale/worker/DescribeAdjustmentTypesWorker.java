package com.transcend.autoscale.worker;


import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeAdjustmentTypesMessage.DescribeAdjustmentTypesRequestMessage;
import com.transcend.autoscale.message.DescribeAdjustmentTypesMessage.DescribeAdjustmentTypesResultMessage;

public class DescribeAdjustmentTypesWorker extends
        AbstractWorker<DescribeAdjustmentTypesRequestMessage,
        DescribeAdjustmentTypesResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeAdjustmentTypesWorker.class
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
    public DescribeAdjustmentTypesResultMessage doWork(
            DescribeAdjustmentTypesRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeAdjustmentTypes.");
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
    protected DescribeAdjustmentTypesResultMessage doWork0(DescribeAdjustmentTypesRequestMessage req,
            ServiceRequestContext context) throws Exception {

      final DescribeAdjustmentTypesResultMessage.Builder result =
    		  DescribeAdjustmentTypesResultMessage.newBuilder();

      return result.buildPartial();
      
	}
}