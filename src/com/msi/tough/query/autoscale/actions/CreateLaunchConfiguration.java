package com.msi.tough.query.autoscale.actions;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.compute.MachineImage;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.autoscaling.model.BlockDeviceMapping;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.transform.CreateLaunchConfigurationRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.StringHelper;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.autoscale.AutoScaleQueryFaults;
import com.msi.tough.query.autoscale.AutoScaleQueryUtil;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.yammer.metrics.core.Meter;

public class CreateLaunchConfiguration extends
		AbstractAction<LaunchConfiguration> {
	private final static Logger logger = Appctx
			.getLogger(CreateLaunchConfiguration.class.getName());

	private static Map<String, Meter> meters = initMeter("AutoScaling",
			"CreateLaunchConfiguration");

	@Override
	protected void mark(LaunchConfiguration ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<LaunchConfiguration> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode("CreateLaunchConfigurationResponse");
		xn.addAttr("xmlns", "http://autoscaling.amazonaws.com/doc/2010-08-01/");
		final XMLNode xr = QueryUtil.addNode(xn,
				"CreateLaunchConfigurationResult");

		// add metadata
		final XMLNode meta = QueryUtil.addNode(xn, "ResponseMetaData");
		QueryUtil.addNode(meta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public LaunchConfiguration process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
	//	final CreateLaunchConfigurationRequest r = CreateLaunchConfigurationRequestUnmarshaller
	//			.getInstance().unmarshall(map);
		
		final CreateLaunchConfigurationRequest r = null;
		final AccountBean ac = getAccountBean();

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
		if (r.getBlockDeviceMappings() != null
				&& r.getBlockDeviceMappings().size() > 0) {
			for (final BlockDeviceMapping blk : r.getBlockDeviceMappings()) {
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
		if (r.getSecurityGroups() != null && r.getSecurityGroups().size() > 0) {
			launch.setSecGrps(StringHelper.concat(r.getSecurityGroups()
					.toArray(new String[1]), ","));
		} else {
			launch.setSecGrps(null);
		}
		launch.setUserData(r.getUserData());
		launchConfigs.add(launch);
		session.save(launch);

		final LaunchConfiguration ret = AutoScaleQueryUtil
				.toLaunchConfiguration(launch);
		logger.debug("Response " + ret);
		return ret;
	}
}
