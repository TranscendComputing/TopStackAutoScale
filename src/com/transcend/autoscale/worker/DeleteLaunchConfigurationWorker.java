package com.transcend.autoscale.worker;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.transform.DeleteLaunchConfigurationRequestUnmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DeleteLaunchConfigurationMessage.DeleteLaunchConfigurationRequestMessage;
import com.transcend.autoscale.message.DeleteLaunchConfigurationMessage.DeleteLaunchConfigurationResultMessage;

public class DeleteLaunchConfigurationWorker extends
        AbstractWorker<DeleteLaunchConfigurationRequestMessage,
        DeleteLaunchConfigurationResultMessage> {
    private final Logger logger = Appctx.getLogger(DeleteLaunchConfigurationWorker.class
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
    public DeleteLaunchConfigurationResultMessage doWork(
    		DeleteLaunchConfigurationRequestMessage req) throws Exception {
        logger.debug("Performing work for DeleteLaunchConfiguration.");
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
    protected DeleteLaunchConfigurationResultMessage doWork0(DeleteLaunchConfigurationRequestMessage req,
            ServiceRequestContext context) throws Exception {
    	

		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();

		final LaunchConfigBean en = ASUtil.readLaunchConfig(session,
				ac.getId(), req.getLaunchConfigurationName());
		if (en == null) {
			throw AutoScaleQueryFaults.launchConfigDoesNotExist();
		}
		final List<ASGroupBean> grps = ASUtil.readASGroup(session, ac.getId());
		if (grps != null) {
			for (final ASGroupBean g : grps) {
				if (g.getLaunchConfig().equals(req.getLaunchConfigurationName())) {
					throw AutoScaleQueryFaults.launchConfigInuse(g.getName());
				}
			}
		}
		session.delete(en);

		final DeleteLaunchConfigurationResultMessage.Builder result =
				DeleteLaunchConfigurationResultMessage.newBuilder();

		return result.buildPartial();

	}
}