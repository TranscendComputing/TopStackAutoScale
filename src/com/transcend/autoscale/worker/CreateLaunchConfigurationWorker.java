package com.transcend.autoscale.worker;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.compute.MachineImage;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;


import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.StringHelper;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationRequestMessage;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationRequestMessage.BlockDeviceMapping;
import com.transcend.autoscale.message.CreateLaunchConfigurationMessage.CreateLaunchConfigurationResultMessage;

public class CreateLaunchConfigurationWorker extends
        AbstractWorker<CreateLaunchConfigurationRequestMessage,
        CreateLaunchConfigurationResultMessage> {
    private final Logger logger = Appctx.getLogger(CreateLaunchConfigurationWorker.class
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
    public CreateLaunchConfigurationResultMessage doWork(
            CreateLaunchConfigurationRequestMessage req) throws Exception {
        logger.debug("Performing work for CreateLaunchConfiguration.");
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
    protected CreateLaunchConfigurationResultMessage doWork0(CreateLaunchConfigurationRequestMessage r,
            ServiceRequestContext context) throws Exception {
    	
    	 final AccountBean ac = context.getAccountBean();

         final String name = r.getLaunchConfigurationName();

         Session session = getSession();
   
    		
    		logger.info("Launch Configuration Name = " + r.getLaunchConfigurationName());

    		final LaunchConfigBean en = ASUtil.readLaunchConfig(session,
    				ac.getId(), r.getLaunchConfigurationName());
    		if (en != null) {
    			throw AutoScaleQueryFaults.alreadyExists();
    		}

    		final CloudProvider cloud = DaseinHelper.getProvider(null,
    				AccountUtil.toAccount(ac));
    		final MachineImage image = DaseinHelper.getImage(cloud, r.getImageId());
    		if (image == null) {
    			throw QueryFaults.InvalidParameterValue("imageId not found "
    					+ r.getImageId());
    		}

    		final Set<LaunchConfigBean> launchConfigs = new HashSet<LaunchConfigBean>();
    		final LaunchConfigBean launch = new LaunchConfigBean();
    		launch.setUserId(ac.getId());
    		launch.setName(r.getLaunchConfigurationName());
    		launch.setCreatedTime(new Date());
    		launch.setImageId(r.getImageId());
    		launch.setInstType(r.getInstanceType());
    		launch.setKernel(r.getKernelId());
    		launch.setKey(r.getKeyName());
    		launch.setRamdisk(r.getRamdiskId());
    		final StringBuilder sb = new StringBuilder();
    		
    		if (r.getBlockDeviceMappingsCount() > 0) {
    			for (int i = 0; i < r.getBlockDeviceMappingsCount(); i++) {
    				final BlockDeviceMapping blk = r.getBlockDeviceMappings(i);
    				if (sb.length() > 0) {
    					sb.append(",");
    				}
    				sb.append(blk.getDeviceName()).append(":")
    						.append(blk.getVirtualName());
    			}
    			launch.setBlk_devs(sb.toString());
    		} else {
    			launch.setBlk_devs(null);
    		}
    		
    		
    		if (r.getSecurityGroupsCount() > 0) {
    			final CommaObject groups = new CommaObject(r.getSecurityGroupsList());
    			launch.setSecGrps(groups.toString());
    		} else {
    			launch.setSecGrps(null);
    		}
    		logger.debug("Assigned securityGroups : " + launch.getSecGrps());
    		launch.setUserData(r.getUserData());
    		launchConfigs.add(launch);
    		session.save(launch);
    		
		final CreateLaunchConfigurationResultMessage.Builder result =
        		CreateLaunchConfigurationResultMessage.newBuilder();

        return result.buildPartial();
        
    }
}