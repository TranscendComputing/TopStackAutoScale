/**
 *
 */
package com.msi.tough.query.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.MapUtil;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.InstanceUtil;

public class ProcessServlet extends HttpServlet implements Constants {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Appctx.getLogger(ProcessServlet.class
			.getName());
	static {
		initialize();
	}

	private static List<InstanceBean> applyPolicy(final Session s,
			final AccountBean ac, final List<InstanceBean> linst,
			final String policy) {
		final List<InstanceBean> nl = new ArrayList<InstanceBean>();
		if (policy.equals("NewestInstance")) {
			nl.add(linst.get(linst.size() - 1));
		}
		if (policy.equals("OldestInstance")) {
			nl.add(linst.get(0));
		}
		if (policy.equals("ClosestToNextInstanceHour")) {
			final InstanceBean i = linst.get(0);
			nl.add(i);
		}
		if (policy.equals("OldestLaunchConfiguration")) {
			final InstanceBean i = linst.get(0);
			nl.add(i);
		}
		return nl;
	}

	private static void initialize() {
		logger.debug("initialize");
		final Runnable r = new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				// Appctx.refresh();
				final String sleepSec = (String) ConfigurationUtil
						.getConfiguration(Arrays
								.asList(new String[] { "AutoScaling_Sleep" }));
				logger.debug("Sleep Sec " + sleepSec);
				long sleep = 30;
				if (sleepSec != null) {
					sleep = Long.parseLong(sleepSec);
				}
				for (;;) {
					Session s = null;
					try {
						Thread.sleep(sleep * 1000);
						logger.debug("Looping ASGroups sleep " + sleep);
						s = HibernateUtil.newSession();
						s.beginTransaction();
						final Query qry = s.createQuery("from AccountBean");
						final List<AccountBean> list = qry.list();
						for (final AccountBean ac : list) {
							final Query gq = s
									.createQuery("from ASGroupBean where userId="
											+ ac.getId());
							for (final ASGroupBean grp : (List<ASGroupBean>) gq
									.list()) {
								final long capacity = newCapacity(s, ac, grp);
								final CommaObject instances = new CommaObject(
										grp.getInstances());
								final int noinst = instances.toArray().length;
								final int addInst = (int) capacity - noinst;
								logger.debug("Account " + ac.getId()
										+ " Group " + grp.getId()
										+ "Current instances=" + noinst
										+ " desired capacity=" + capacity
										+ " adding=" + addInst);
								if (addInst > 0) {
									final String slaunch = grp
											.getLaunchConfig();
									final LaunchConfigBean lcb = ASUtil
											.readLaunchConfig(s, ac.getId(),
													slaunch);
									if (lcb == null) {
										logger.error("Launch Config Not found "
												+ slaunch + " for ac "
												+ ac.getId());
										continue;
									}

									final CallStruct call = new CallStruct();
									call.setName(InstanceUtil.getHostName(grp
											.getAvzones()));
									call.setAc(AccountUtil.toAccount(ac));
									call.setParentId(grp.getName());
									call.setAvailabilityZone(grp.getAvzones() != null ? grp
											.getAvzones() : ac.getDefZone());
									call.setCtx(new TemplateContext(null));
									call.setStackId(grp.getStackId());
									// call.setWaitHookClass(lcb
									// .getWaitHookClass());
									final Map<String, Object> prop = MapUtil
											.create(AVAILABILITYZONE,
													grp.getAvzones(),
													SECURITYGROUPIDS,
													lcb.getSecGrps(), IMAGEID,
													lcb.getImageId(),
													INSTANCETYPE,
													lcb.getInstType());
									if (lcb.getKernel() != null) {
										prop.put(KERNELID, lcb.getKernel());
									}
									if (lcb.getRamdisk() != null) {
										prop.put(RAMDISKID, lcb.getRamdisk());
									}
									if (lcb.getUserData() != null) {
										prop.put(USERDATA, lcb.getUserData());
									}
									if (lcb.getKey() != null) {
										prop.put(KEYNAME, lcb.getKey());
									}
									if (lcb.getChefRoles() != null) {
										prop.put(CHEFROLES, lcb.getChefRoles());
									}
									if (lcb.getDatabag() != null) {
										prop.put(DATABAG, lcb.getDatabag());
									}
									call.setProperties(prop);
									// c0.setNoWait(1);
									call.setType(Instance.TYPE);
									// launch instance
									final InstanceType res = (InstanceType) CFUtil
											.createResource(call);
									// final InstanceType res = Instance
									// .createChefInstance(
									// AccountUtil.toAccount(ac),
									// InstanceUtil.getHostName(grp
									// .getAvzones()), grp
									// .getName(), call,
									// MapUtil.create(
									// AVAILABILITYZONE,
									// grp.getAvzones(),
									// CHEFROLES,
									// lcb.getChefRoles(),
									// DATABAG,
									// lcb.getDatabag()));

									// save the grp instance
									instances.add(res.getInstanceId());
									grp.setInstances(instances.toString());
									s.save(grp);
									ASUtil.reconfigLBInstance(s, grp, false,
											res.getInstanceId());
								} else if (addInst < 0) {
									final String instId = removeInstance(s, ac,
											grp);
									final CommaObject insts = new CommaObject(
											grp.getInstances());
									CFUtil.deleteStackResources(
											AccountUtil.toAccount(ac),
											grp.getStackId(), grp.getName(),
											instId);
									insts.remove(instId);
									grp.setInstances(insts.toString());
									s.save(grp);
									ASUtil.reconfigLBInstance(s, grp, true,
											instId);
								}
							}
						}
						s.getTransaction().commit();
					} catch (final Exception e) {
						s.getTransaction().rollback();
						e.printStackTrace();
					} finally {
						s.close();
					}
				}
			}
		};
		// ExecutorHelper.execute(r);
		new Thread(r).start();
	}

	@SuppressWarnings("unchecked")
	private static long newCapacity(final Session s, final AccountBean ac,
			final ASGroupBean grp) {
		long capacity = grp.getCapacity();
		if (capacity < grp.getMinSz()) {
			capacity = grp.getMinSz();
		}
		if (capacity > grp.getMaxSz()) {
			capacity = grp.getMaxSz();
		}
		final Query q = s.createQuery("from ASScheduledBean where userId="
				+ ac.getId() + " and grpName='" + grp.getName() + "'");
		final List<ASScheduledBean> l = q.list();
		if (l != null) {
			for (final ASScheduledBean b : l) {
				if (b.getStartTime() != null
						&& b.getStartTime().getTime() > System
								.currentTimeMillis()) {
					continue;
				}
				if (b.getEndTime() != null
						&& b.getEndTime().getTime() < System
								.currentTimeMillis()) {
					continue;
				}
				if (b.getMinSize() != 0 && b.getMinSize() > capacity) {
					capacity = b.getMinSize();
				}
				if (b.getMaxSize() != 0 && b.getMaxSize() < capacity) {
					capacity = b.getMaxSize();
				}
			}
		}
		return capacity;
	}

	private static String removeInstance(final Session s, final AccountBean ac,
			final ASGroupBean grp) {
		final CommaObject policies = new CommaObject(
				grp.getTerminationPolicies());
		if (policies.toList().size() == 0) {
			policies.add("default");
		}
		if (policies.toArray()[0].equals("default")) {
			policies.setList(Arrays.asList(new String[] {
					"OldestLaunchConfiguration", "ClosestToNextInstanceHour",
					"OldestInstance" }));
		}
		final CommaObject insts = new CommaObject(grp.getInstances());
		List<InstanceBean> linst = new ArrayList<InstanceBean>();
		for (final String i : insts.toList()) {
			linst.add(InstanceUtil.getInstance(s, i));
		}
		for (final String policy : policies.toList()) {
			if (linst.size() == 1) {
				break;
			}
			linst = applyPolicy(s, ac, linst, policy);
		}
		final String instId = linst.get(0).getInstanceId();
		return instId;
	}

}
