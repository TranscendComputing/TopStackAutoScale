package com.transcend.autoscale.worker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupRequestMessage;
import com.transcend.autoscale.message.CreateAutoScalingGroupMessage.CreateAutoScalingGroupResultMessage;

public class CreateAutoScalingGroupWorker extends
        AbstractWorker<CreateAutoScalingGroupRequestMessage,
        CreateAutoScalingGroupResultMessage> {
    private final Logger logger = Appctx.getLogger(CreateAutoScalingGroupWorker.class
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
    public CreateAutoScalingGroupResultMessage doWork(
            CreateAutoScalingGroupRequestMessage req) throws Exception {
        logger.debug("Performing work for CreateAutoScalingGroup.");
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
    protected CreateAutoScalingGroupResultMessage doWork0(CreateAutoScalingGroupRequestMessage req,
            ServiceRequestContext context) throws Exception {
    	
        final AccountBean account = context.getAccountBean();

        final String name = req.getAutoScalingGroupName();

        Session session = getSession();
        final ASGroupBean en = ASUtil.readASGroup(session, account.getId(),
                req.getAutoScalingGroupName());
        if (en != null) {
            throw AutoScaleQueryFaults.alreadyExists();
        }
        {
            final LaunchConfigBean b = ASUtil.readLaunchConfig(session,
                    account.getId(), req.getLaunchConfigurationName());
            if (b == null) {
                throw AutoScaleQueryFaults.launchConfigDoesNotExist();
            }
        }

        List<String> avZones = null;
        if (req.getAvailabilityZoneCount() > 0) {
            for (final String i : req.getAvailabilityZoneList()) {
                final Object az = ConfigurationUtil.getConfiguration(
                        "AvailabilityZone", i);
                if (az == null) {
                    throw QueryFaults
                            .InvalidParameterValue("Invalid AvailabilityZone "
                                    + i);
                }
            }
            avZones = req.getAvailabilityZoneList();
        } else {
            avZones = Arrays.asList(new String[] { account.getDefZone() });
        }

        final ASGroupBean g = new ASGroupBean();
        g.setUserId(account.getId());
        final CommaObject co = new CommaObject(avZones);
        g.setAvzones(co.toString());
        g.setCapacity(req.getDesiredCapacity());
        g.setCooldown(req.getDefaultCooldown());
        g.setLaunchConfig(req.getLaunchConfigurationName());
        final CommaObject col = new CommaObject(req.getLoadBalancerNameList());
        g.setLoadBalancers(col.toString());
        final CommaObject terms = new CommaObject(req.getTerminationPolicyList());
        g.setTerminationPolicies(terms.toString());
        g.setMaxSz(req.getMaxSize());
        g.setMinSz(req.getMinSize());
        g.setHealthCheckGracePeriod(req.getHealthCheckGracePeriod());
        g.setHealthCheckType(req.getHealthCheckType());
        

        final CallStruct call = new CallStruct();
        call.setAc(AccountUtil.toAccount(account));
        call.setCtx(new TemplateContext(null));
        call.setName(name);
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("AvailabilityZones", g.getAvzones());
        properties.put("Cooldown", "" + g.getCooldown());
        properties.put("DesiredCapacity", "" + g.getCapacity());
        if (g.getHealthCheckGracePeriod() != null) {
            properties.put("HealthCheckGracePeriod",
                    "" + g.getHealthCheckGracePeriod());
        }
        if (g.getHealthCheckType() != null) {
            properties.put("HealthCheckType", g.getHealthCheckType());
        }
        properties.put("LaunchConfigurationName", g.getLaunchConfig());
        properties.put("MaxSize", "" + g.getMaxSz());
        properties.put("MinSize", "" + g.getMinSz());

        properties.put("TerminationPolicies", terms.toList());
        
        call.setProperties(properties);
        call.setType(com.msi.tough.engine.aws.autoscaling.AutoScalingGroup.TYPE);
        call.setStackId("__as_" + account.getId() + "_" + name);
        CFUtil.createResource(call);
        final CreateAutoScalingGroupResultMessage.Builder result =
        		CreateAutoScalingGroupResultMessage.newBuilder();

        return result.buildPartial();
        
    }
}