package com.transcend.autoscale.worker;


import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeScalingProcessTypesMessage.DescribeScalingProcessTypesRequestMessage;
import com.transcend.autoscale.message.DescribeScalingProcessTypesMessage.DescribeScalingProcessTypesResultMessage;

public class DescribeScalingProcessTypesWorker extends
        AbstractWorker<DescribeScalingProcessTypesRequestMessage,
        DescribeScalingProcessTypesResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeScalingProcessTypesWorker.class
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
    public DescribeScalingProcessTypesResultMessage doWork(
            DescribeScalingProcessTypesRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeScalingProcessTypes.");
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
    protected DescribeScalingProcessTypesResultMessage doWork0(DescribeScalingProcessTypesRequestMessage req,
            ServiceRequestContext context) throws Exception {

      final DescribeScalingProcessTypesResultMessage.Builder result =
              DescribeScalingProcessTypesResultMessage.newBuilder();

      return result.buildPartial();

    }
}
