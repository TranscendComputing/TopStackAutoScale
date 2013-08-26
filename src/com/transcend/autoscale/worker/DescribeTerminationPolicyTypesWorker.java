package com.transcend.autoscale.worker;


import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeTerminationPolicyTypesMessage.DescribeTerminationPolicyTypesRequestMessage;
import com.transcend.autoscale.message.DescribeTerminationPolicyTypesMessage.DescribeTerminationPolicyTypesResultMessage;

public class DescribeTerminationPolicyTypesWorker extends
        AbstractWorker<DescribeTerminationPolicyTypesRequestMessage,
        DescribeTerminationPolicyTypesResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeTerminationPolicyTypesWorker.class
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
    public DescribeTerminationPolicyTypesResultMessage doWork(
            DescribeTerminationPolicyTypesRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeTerminationPolicyTypes.");
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
    protected DescribeTerminationPolicyTypesResultMessage doWork0(DescribeTerminationPolicyTypesRequestMessage req,
            ServiceRequestContext context) throws Exception {

      final DescribeTerminationPolicyTypesResultMessage.Builder result =
              DescribeTerminationPolicyTypesResultMessage.newBuilder();

      return result.buildPartial();

    }
}
