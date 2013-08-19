package com.transcend.autoscale.worker;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.DateUtils;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASActivityLog;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeScalingActivitiesMessage.DescribeScalingActivitiesRequestMessage;
import com.transcend.autoscale.message.DescribeScalingActivitiesMessage.DescribeScalingActivitiesResultMessage;
import com.transcend.autoscale.message.DescribeScalingActivitiesMessage.DescribeScalingActivitiesResultMessage.Activity;

public class DescribeScalingActivitiesWorker extends
        AbstractWorker<DescribeScalingActivitiesRequestMessage,
        DescribeScalingActivitiesResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeScalingActivitiesWorker.class
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
    public DescribeScalingActivitiesResultMessage doWork(
    		DescribeScalingActivitiesRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeScalingActivities.");
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
    protected DescribeScalingActivitiesResultMessage doWork0(DescribeScalingActivitiesRequestMessage req,
            ServiceRequestContext context) throws Exception {


    		final AccountBean ac   = context.getAccountBean();
    		final Session session = getSession();

    		final DescribeScalingActivitiesResultMessage.Builder ret = DescribeScalingActivitiesResultMessage.newBuilder();

    		final List<ASGroupBean> grpl = ASUtil.readASGroup(session, ac.getId());

    		final Collection<Activity> vals = new ArrayList<Activity>();
    		final String nextToken = null;
    		boolean all = false;
    		if (req.getActivityIdsList() == null || req.getActivityIdsCount() == 0) {
    			all = true;
    		}
    		final String grpName = req.getAutoScalingGroupName();
    		if (grpl != null) {
    			for (final ASGroupBean g : grpl) {
    				if (!"".equals(grpName) && !grpName.equals(g.getName())) {
    					continue;
    				}
    				final List<ASActivityLog> actl = ASUtil.readASActivityLog(
    						session, ac.getId(), g.getId());
    				for (final ASActivityLog a : actl) {
    					// if (req.getNextToken() != null
    					// && a.getName().compareTo(req.getNextToken()) < 0) {
    					// continue;
    					// }
    					// if (req.getMaxRecords() != 0 && req.getMaxRecords() <= cnt) {
    					// nextToken = g.getName();
    					// break;
    					// }
    					boolean select = false;
    					if (!all) {
    						for (final String s : req.getActivityIdsList()) {
    							if (s.equals("" + a.getId())) {
    								select = true;
    								break;
    							}
    						}
    					} else {
    						select = true;
    					}
    					if (select) {
    						cnt++;
    						final Activity.Builder activity = Activity.newBuilder();
    						activity.setActivityId("" + a.getId());
    						activity.setAutoScalingGroupName(g.getName());
    						activity.setCause(a.getCause());
    						activity.setDescription(a.getDescription());
    						activity.setDetails(a.getDetails());
    						activity.setEndTime(new DateUtils().formatIso8601Date(a.getEndTime()));
    						activity.setProgress(a.getProgress());
    						activity.setStartTime(new DateUtils().formatIso8601Date(a.getStartTime()));
    						activity.setStatusCode(a.getStatusCode());
    						activity.setStatusMessage(a.getStatusMsg());
    						vals.add(activity.buildPartial());
    					}
    				}
    			}
    		}
    		ret.addAllActivities(vals);
    		ret.setNextToken(Strings.nullToEmpty(nextToken));
    		if (!"".equals(req.getNextToken()) && vals.size() == 0) {
    			throw AutoScaleQueryFaults.invalidNextToken();
    		}
    		logger.debug("Response " + ret);
    		return ret.buildPartial();

	}
}