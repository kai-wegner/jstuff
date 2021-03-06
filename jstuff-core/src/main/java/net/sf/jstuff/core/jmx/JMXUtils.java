/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.jmx;

import java.lang.reflect.Method;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class JMXUtils {
    private static Logger LOG = Logger.create();

    private static MBeanServer mbeanServer;

    public static synchronized MBeanServer getMBeanServer() {
        if (mbeanServer != null)
            return mbeanServer;

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try {
            // http://wiki.jboss.org/wiki/FindMBeanServer
            final Class<?> clazz = cl.loadClass("org.jboss.mx.util.MBeanServerLocator");
            final Method method = clazz.getMethod("locateJBoss", (Class[]) null);
            mbeanServer = (MBeanServer) method.invoke((Object[]) null, (Object[]) null);
            if (mbeanServer != null) {
                LOG.info("Located MBeanServer via org.jboss.mx.util.MBeanServerLocator#locateJBoss()");
                return mbeanServer;
            }
        } catch (final Exception ex) {
            LOG.debug("Locating MBeanServer via org.jboss.mx.util.MBeanServerLocator#locateJBoss() way failed.", ex);
        }

        try {
            final Class<?> clazz = cl.loadClass("java.lang.management.ManagementFactory");
            final Method method = clazz.getMethod("getPlatformMBeanServer", (Class[]) null);
            mbeanServer = (MBeanServer) method.invoke((Object[]) null, (Object[]) null);
            if (mbeanServer != null) {
                LOG.info("Located MBeanServer via java.lang.management.ManagementFactory#getPlatformMBeanServer()");
                return mbeanServer;
            }
        } catch (final Exception ex) {
            LOG.debug("Locating MBeanServer via java.lang.management.ManagementFactory#getPlatformMBeanServer() failed.", ex);
        }

        try {
            mbeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            if (mbeanServer != null) {
                LOG.info("Located MBeanServer via MBeanServerFactory#findMBeanServer(null).get(0)");
                return mbeanServer;
            }
        } catch (final Exception ex) {
            LOG.debug("Locating MBeanServer via MBeanServerFactory#findMBeanServer(null).get(0) failed.", ex);
        }

        LOG.info("Creating new MBeanServer...");
        mbeanServer = MBeanServerFactory.createMBeanServer();
        return mbeanServer;
    }
}
