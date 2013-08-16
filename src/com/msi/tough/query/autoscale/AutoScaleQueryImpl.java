/**
 * 
 */
package com.msi.tough.query.autoscale;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.Action;

/**
 * @author tdhite
 */
public class AutoScaleQueryImpl
{
    private final static Logger logger = Appctx.getLogger(AutoScaleQueryImpl.class
        .getName());

    private final Map<String, Action> actionMap;

    public AutoScaleQueryImpl(final Map<String, Action> actionMap)
    {
        this.actionMap = actionMap;
    }

    public void process(final HttpServletRequest req,
        final HttpServletResponse resp) throws Exception
    {
        final Action a = this.actionMap.get(req.getParameter("Action"));
        if (a == null)
        {
            logger.debug("No action exists for " + req.getQueryString());
            logger.debug("Those that exist are:");
            for (Entry<String, Action> item : this.actionMap.entrySet())
            {
                logger.error("\"" + item.getKey() + "\"");
            }
        }
        else
        {
            logger.debug("calling action " + a);
            a.process(req, resp);
        }
    }
}
