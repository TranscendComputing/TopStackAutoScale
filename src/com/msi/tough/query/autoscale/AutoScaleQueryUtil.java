package com.msi.tough.query.autoscale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;
import com.amazonaws.services.autoscaling.model.ScheduledUpdateGroupAction;
import com.amazonaws.services.cloudformation.model.Parameter;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.InstanceUtil;

public class AutoScaleQueryUtil {
	public static void marshallAutoPolicy(final XMLNode xpolicy,
			final ScalingPolicy i) {
		QueryUtil.addNode(xpolicy, "PolicyARN", i.getPolicyARN());
		QueryUtil.addNode(xpolicy, "AdjustmentType", i.getAdjustmentType());
		QueryUtil.addNode(xpolicy, "AutoScalingGroupName",
				i.getAutoScalingGroupName());
		QueryUtil.addNode(xpolicy, "Cooldown", i.getCooldown());
		QueryUtil.addNode(xpolicy, "MinAdjustmentStep",
				i.getMinAdjustmentStep());
		QueryUtil.addNode(xpolicy, "PolicyName", i.getPolicyName());
		QueryUtil.addNode(xpolicy, "ScalingAdjustment",
				i.getScalingAdjustment());
	}

	public static void marshallAutoScalingGroup(final XMLNode m,
			final AutoScalingGroup en) {

		QueryUtil
				.addNode(m, "AutoScalingGroupARN", en.getAutoScalingGroupARN());
		QueryUtil.addNode(m, "AutoScalingGroupName",
				en.getAutoScalingGroupName());
		final XMLNode az = QueryUtil.addNode(m, "AvailabilityZones");
		for (final String e : en.getAvailabilityZones()) {
			QueryUtil.addNode(az, "member", e);
		}
		QueryUtil.addNode(m, "CreatedTime", en.getCreatedTime());
		QueryUtil.addNode(m, "DefaultCooldown", en.getDefaultCooldown());
		QueryUtil.addNode(m, "DesiredCapacity", en.getDesiredCapacity());
		// QueryUtil.addNode(m, "EnabledMetrics", en.getEnabledMetrics());
		QueryUtil.addNode(m, "HealthCheckGracePeriod",
				en.getHealthCheckGracePeriod());
		QueryUtil.addNode(m, "HealthCheckType", en.getHealthCheckType());
		final XMLNode inst = QueryUtil.addNode(m, "Instances");
		for (final Instance e : en.getInstances()) {
			final XMLNode mem = QueryUtil.addNode(inst, "member");
			QueryUtil.addNode(mem, "AvailabilityZone", e.getAvailabilityZone());
			QueryUtil.addNode(mem, "HealthStatus", e.getHealthStatus());
			QueryUtil.addNode(mem, "InstanceId", e.getInstanceId());
			QueryUtil.addNode(mem, "LaunchConfigurationName",
					e.getLaunchConfigurationName());
			QueryUtil.addNode(mem, "LifecycleState", e.getLifecycleState());
		}
		QueryUtil.addNode(m, "LaunchConfigurationName",
				en.getLaunchConfigurationName());
		final XMLNode lb = QueryUtil.addNode(m, "LoadBalancerNames");
		for (final String e : en.getLoadBalancerNames()) {
			QueryUtil.addNode(lb, "member", e);
		}
		QueryUtil.addNode(m, "MaxSize", en.getMaxSize());
		QueryUtil.addNode(m, "MinSize", en.getMinSize());
		QueryUtil.addNode(m, "PlacementGroup", en.getPlacementGroup());
		// final XMLNode sp = QueryUtil.addNode(m, "SuspendedProcesses");
		// en.getSuspendedProcesses());
		QueryUtil.addNode(m, "VPCZoneIdentifier", en.getVPCZoneIdentifier());
	}

	public static void marshallAutoScalingInstances(final XMLNode m,
			final AutoScalingInstanceDetails en) {
		QueryUtil.addNode(m, "AutoScalingGroupName",
				en.getAutoScalingGroupName());
		QueryUtil.addNode(m, "AvailabilityZone", en.getAvailabilityZone());
		QueryUtil.addNode(m, "HealthStatus", en.getHealthStatus());
		QueryUtil.addNode(m, "InstanceId", en.getInstanceId());
		QueryUtil.addNode(m, "LaunchConfigurationName",
				en.getLaunchConfigurationName());
		QueryUtil.addNode(m, "LifecycleState", en.getLifecycleState());
	}

	public static void marshallLaunchConfiguration(final XMLNode n,
			final LaunchConfiguration r) {
		QueryUtil.addNode(n, "BlockDeviceMappings");
		// r.getBlockDeviceMappings();
		QueryUtil.addNode(n, "CreatedTime", r.getCreatedTime());
		QueryUtil.addNode(n, "ImageId", r.getImageId());
		QueryUtil.addNode(n, "InstanceMonitoring");
		// r.getInstanceMonitoring());
		QueryUtil.addNode(n, "InstanceType", r.getInstanceType());
		QueryUtil.addNode(n, "KernelId", r.getKernelId());
		QueryUtil.addNode(n, "KeyName", r.getKeyName());
		QueryUtil.addNode(n, "LaunchConfigurationARN",
				r.getLaunchConfigurationARN());
		QueryUtil.addNode(n, "LaunchConfigurationName",
				r.getLaunchConfigurationName());
		QueryUtil.addNode(n, "RamdiskId", r.getRamdiskId());
		final XMLNode sg = QueryUtil.addNode(n, "SecurityGroups");
		for (final String e : r.getSecurityGroups()) {
			QueryUtil.addNode(sg, "member", e);
		}
		QueryUtil.addNode(n, "UserData", r.getUserData());
	}

	public static void marshallScheduledAction(final XMLNode n,
			final ScheduledUpdateGroupAction r) {
		QueryUtil.addNode(n, "AutoScalingGroupName",
				r.getAutoScalingGroupName());
		QueryUtil.addNode(n, "DesiredCapacity", r.getDesiredCapacity());
		QueryUtil.addNode(n, "EndTime", r.getEndTime());
		QueryUtil.addNode(n, "MaxSize", r.getMaxSize());
		QueryUtil.addNode(n, "MinSize", r.getMinSize());
		QueryUtil.addNode(n, "Recurrence", r.getRecurrence());
		QueryUtil.addNode(n, "ScheduledActionARN", r.getScheduledActionARN());
		QueryUtil.addNode(n, "ScheduledActionName", r.getScheduledActionName());
		QueryUtil.addNode(n, "StartTime", r.getStartTime());
	}

	public static AutoScalingGroup toAutoScalingGroup(final Session s,
			final long acid, final ASGroupBean en) {
		final AutoScalingGroup as = new AutoScalingGroup();
		as.setAutoScalingGroupARN(en.getArn());
		as.setAutoScalingGroupName(en.getName());
		final CommaObject co = new CommaObject(en.getAvzones());
		as.setAvailabilityZones(co.toList());
		as.setCreatedTime(en.getCreatedTime());
		as.setDefaultCooldown((int) en.getCooldown());
		as.setDesiredCapacity((int) en.getCapacity());
		// as.setEnabledMetrics(enabledMetrics);
		as.setHealthCheckGracePeriod(en.getHealthCheckGracePeriod());
		as.setHealthCheckType(en.getHealthCheckType());
		final CommaObject ico = new CommaObject(en.getInstances());
		final Collection<Instance> instances = new ArrayList<Instance>();
		for (final String id : ico.toList()) {
			final InstanceBean i = InstanceUtil.getInstance(s, id);
			if (i != null) {
				final Instance inst = InstanceUtil.toInstance(i);
				inst.setLaunchConfigurationName(en.getLaunchConfig());
				instances.add(inst);
			}
		}
		as.setInstances(instances);
		as.setLaunchConfigurationName(en.getLaunchConfig());
		final CommaObject colb = new CommaObject(en.getLoadBalancers());
		as.setLoadBalancerNames(colb.toList());
		as.setMaxSize((int) en.getMaxSz());
		as.setMinSize((int) en.getMinSz());
		// as.setPlacementGroup(en.getP);
		// as.setSuspendedProcesses(suspendedProcesses);
		// as.setVPCZoneIdentifier(vPCZoneIdentifier);
		return as;
	}

	public static LaunchConfiguration toLaunchConfiguration(
			final LaunchConfigBean b) {
		final LaunchConfiguration r = new LaunchConfiguration();
		// r.setBlockDeviceMappings(b.getBlockDeviceMappings());
		r.setCreatedTime(b.getCreatedTime());
		r.setImageId(b.getImageId());
		// r.setInstanceMonitoring(instanceMonitoring);
		r.setInstanceType(b.getInstType());
		r.setKernelId(b.getKernel());
		r.setKeyName(b.getKey());
		// r.setLaunchConfigurationARN(launchConfigurationARN);
		r.setLaunchConfigurationName(b.getName());
		r.setRamdiskId(b.getRamdisk());
		// r.setSecurityGroups(securityGroups);
		r.setUserData(b.getUserData());
		return r;
	}

	public static String toString(final List<Parameter> parameters) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final Parameter p : parameters) {
			if (i++ == 0) {
				sb.append("|");
			}
			sb.append(p.getParameterKey()).append("=")
					.append(p.getParameterValue());
		}
		return sb.toString();
	}
}
