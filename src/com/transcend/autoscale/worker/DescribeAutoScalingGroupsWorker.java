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
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsRequestMessage;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage.AutoScalingGroup;
import com.transcend.autoscale.message.DescribeAutoScalingGroupsMessage.DescribeAutoScalingGroupsResultMessage.Instance;

public class DescribeAutoScalingGroupsWorker extends
        AbstractWorker<DescribeAutoScalingGroupsRequestMessage,
        DescribeAutoScalingGroupsResultMessage> {
    private final Logger logger = Appctx.getLogger(DescribeAutoScalingGroupsWorker.class
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
    public DescribeAutoScalingGroupsResultMessage doWork(
            DescribeAutoScalingGroupsRequestMessage req) throws Exception {
        logger.debug("Performing work for DescribeAutoScalingGroups.");
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
    protected DescribeAutoScalingGroupsResultMessage doWork0(DescribeAutoScalingGroupsRequestMessage req,
            ServiceRequestContext context) throws Exception {

        final DescribeAutoScalingGroupsResultMessage.Builder result = DescribeAutoScalingGroupsResultMessage.newBuilder();
        final AccountBean ac = context.getAccountBean();
        final Session session = getSession();

        final List<ASGroupBean> grpl = ASUtil.readASGroup(session, ac.getId());

        final List<AutoScalingGroup> autoScalingGroups = new ArrayList<AutoScalingGroup>();
        String nextToken = "";
        String firstToken = "";
        int cnt = 0;
        boolean all = false;
        if (req.getAutoScalingGroupNamesCount() <= 0) {
            all = true;
        }
        if (grpl != null) {
            for (final ASGroupBean en : grpl) {
                if (req.getNextToken() != ""
                        && en.getName().compareTo(req.getNextToken()) < 0) {
                    continue;
                }
                if (req.getMaxRecords() != 0 && req.getMaxRecords() <= cnt) {
                    nextToken = en.getName();
                    break;
                }
                boolean select = false;
                if (!all) {
                    for (final String s : req.getAutoScalingGroupNamesList()) {
                        if (s.equals(en.getName())) {
                            select = true;
                            break;
                        }
                    }
                } else {
                    select = true;
                }
                if (select) {
                    cnt++;
                    autoScalingGroups.add(toAutoScalingGroup(session, ac.getId(), en));
                    if (firstToken == "") {
                        firstToken = en.getName();
                    }
                }
            }
        }

        result.addAllAutoScalingGroups(autoScalingGroups);
        result.setNextToken(Strings.nullToEmpty(nextToken));
        if (req.getNextToken() != ""
                && (firstToken == "" || !req.getNextToken().equals(firstToken))) {
            throw AutoScaleQueryFaults.invalidNextToken();
        }
      return result.buildPartial();

    }

    public static AutoScalingGroup toAutoScalingGroup(final Session s,
            final long acid, final ASGroupBean en) {
        final AutoScalingGroup.Builder as = AutoScalingGroup.newBuilder();
        as.setAutoScalingGroupARN(en.getArn());
        as.setAutoScalingGroupName(en.getName());
        final CommaObject co = new CommaObject(en.getAvzones());
        as.addAllAvailabilityZones(co.getList());
        as.setCreatedTime(new DateUtils().formatIso8601Date(en.getCreatedTime()));
        as.setDefaultCooldown((int) en.getCooldown());
        as.setDesiredCapacity((int) en.getCapacity());
        // as.setEnabledMetrics(enabledMetrics);
        as.setHealthCheckGracePeriod(en.getHealthCheckGracePeriod());
        as.setHealthCheckType(en.getHealthCheckType());

        Collection<InstanceBean> instanceBeans = en.getScaledInstances(s);
        final Collection<Instance> instances = new ArrayList<Instance>();
        for (final InstanceBean i : instanceBeans) {
            if (i != null) {
                final Instance inst = toInstance(i, en);
                instances.add(inst);
            }
        }
        as.addAllInstances(instances);
        as.setLaunchConfigurationName(en.getLaunchConfig());
        final CommaObject colb = new CommaObject(en.getLoadBalancers());
        as.addAllLoadBalancerNames(colb.toList());
        as.setMaxSize((int) en.getMaxSz());
        as.setMinSize((int) en.getMinSz());
        // as.setPlacementGroup(en.getP);
        // as.setSuspendedProcesses(suspendedProcesses);
        // as.setVPCZoneIdentifier(vPCZoneIdentifier);
        return as.buildPartial();
    }

    public static Instance toInstance(final InstanceBean i, final ASGroupBean en) {
        final Instance.Builder b = Instance.newBuilder();
        b.setAvailabilityZone(i.getAvzone());
        b.setInstanceId(i.getInstanceId());
        b.setLifecycleState(i.getStatus());
        b.setHealthStatus(i.getHealth());
        b.setLaunchConfigurationName(en.getLaunchConfig());
        return b.buildPartial();
    }

}
