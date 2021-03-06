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
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ClassPathResourcesServletContext extends ServletContextWrapper {
    private final Logger LOG = Logger.create();

    public ClassPathResourcesServletContext(final ServletContext delegate) {
        super(delegate);
    }

    @Override
    public ServletContext getContext(final String uripath) {
        final ServletContext ctx = delegate.getContext(uripath);
        if (ctx instanceof ClassPathResourcesServletContext)
            return ctx;
        if (ctx == delegate)
            return this;
        return new ClassPathResourcesServletContext(ctx);
    }

    @Override
    public URL getResource(final String path) throws MalformedURLException {
        URL resource = delegate.getResource(path);
        if (resource == null) {
            resource = ClassPathResourcesFilter.findResourceInClassPath(path);
        }
        return resource;
    }

    @SuppressWarnings("resource")
    @Override
    public InputStream getResourceAsStream(final String path) {
        InputStream stream = delegate.getResourceAsStream(path);
        if (stream == null) {
            final URL resource = ClassPathResourcesFilter.findResourceInClassPath(path);
            if (resource != null) {
                try {
                    stream = resource.openStream();
                } catch (final IOException ex) {
                    LOG.error(ex, "Failed to open stream of resource [%s]", path);
                }
            }
        }
        return stream;
    }
}
