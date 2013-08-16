package com.transcend.autoscale.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASPolicyBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesRequestMessage;
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesResultMessage;
import com.transcend.autoscale.message.DescribePoliciesMessage.DescribePoliciesResultMessage.ScalingPolicy;

public class DescribePoliciesWorker extends
        AbstractWorker<DescribePoliciesRequestMessage,
        DescribePoliciesResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribePoliciesWorker.class
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
    public DescribePoliciesResultMessage doWork(
            DescribePoliciesRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribePolicies.");
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
    protected DescribePoliciesResultMessage doWork0(DescribePoliciesRequestMessage req,
            ServiceRequestContext context) throws Exception {
		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final ASGroupBean en = ASUtil.readASGroup(session, ac.getId(),
				req.getAutoScalingGroupName());
		if (en == null) {
			throw AutoScaleQueryFaults.groupDoesNotExist();
		}

		String nextToken  = null;
		String firstToken = null;
		String reqNextToken   = req.getNextToken();
		if(reqNextToken.equals(""))
			reqNextToken = null;
		int cnt = 0;

		final Collection<ScalingPolicy> policies = new ArrayList<ScalingPolicy>();
	    final Query q = session.createQuery("from ASPolicyBean where userId="
				+ ac.getId() + " and grpName='" + req.getAutoScalingGroupName()
				+ "'");
		final List<ASPolicyBean> l = q.list();
		for (final ASPolicyBean b : l) {
			if (req.getPolicyNamesList() != null && req.getPolicyNamesCount() > 0
					&& !req.getPolicyNamesList().contains(b.getName())) {
				continue;
			}
			if (reqNextToken != null
					&& b.getName().compareTo(reqNextToken) < 0) {
				continue;
			}
			if (req.getMaxRecords() != 0 && req.getMaxRecords() <= cnt) {
				nextToken = b.getName();
				break;
			}
			if (firstToken == null) {
				firstToken = b.getName();
			}
			cnt++;
			final ScalingPolicy.Builder policy = ScalingPolicy.newBuilder();
			policy.setPolicyARN(b.getArn());
			policy.setAdjustmentType(b.getAdjustmentType());
			// policy.setAlarms(alarms);
			policy.setAutoScalingGroupName(req.getAutoScalingGroupName());
			policy.setCooldown(b.getCooldown());
			policy.setMinAdjustmentStep(b.getMinAdjustmentStep());
			policy.setPolicyName(b.getName());
			policy.setScalingAdjustment(b.getScalingAdjustment());
			policies.add(policy.build());
		}
      
	  final DescribePoliciesResultMessage.Builder result = DescribePoliciesResultMessage.newBuilder();
	  result.addAllScalingPolicies(new ArrayList<ScalingPolicy>(policies));
      result.setNextToken(Strings.nullToEmpty(nextToken));
      
		if (reqNextToken != null
				&& (firstToken == null || !reqNextToken.equals(firstToken))) {
			throw AutoScaleQueryFaults.invalidNextToken();
		}

      return result.buildPartial();
      
	}     
}