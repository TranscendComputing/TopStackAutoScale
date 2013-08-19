package com.msi.tough.query.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;

/**
 * Deprecated; replaced with V2 version.
 * @see com.transcend.autoscale.actions.CreateLaunchConfiguration
 * @author jgardner
 *
 */
@Deprecated
public class CreateLaunchConfiguration extends
		AbstractAction<LaunchConfiguration> {

	@Override
	protected void mark(LaunchConfiguration ret, Exception e) {
	}

	@Override
	public String marshall(final MarshallStruct<LaunchConfiguration> input,
			final HttpServletResponse resp) throws Exception {
		return null;
	}

	@Override
	public LaunchConfiguration process0(final Session session,
			final HttpServletRequest req, final HttpServletResponse resp,
			final Map<String, String[]> map) throws Exception {
	    return null;
	}
}
