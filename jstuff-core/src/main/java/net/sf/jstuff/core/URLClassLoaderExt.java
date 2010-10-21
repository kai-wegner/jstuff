/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

/**
 * Extended {@link URLClassLoader} that supports parent last classloading strategy and 
 * the recursive loading of JARs from directories.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class URLClassLoaderExt extends URLClassLoader
{
	private static final Logger LOG = Logger.get();

	private boolean parentLast = false;

	public URLClassLoaderExt()
	{
		super(new URL[0]);
	}

	public URLClassLoaderExt(final ClassLoader parent)
	{
		super(new URL[0], parent);
	}

	public void addJAR(final File jarFile) throws IOException
	{
		Assert.argumentNotNull("jarFile", jarFile);
		Assert.isFileReadable(jarFile);

		LOG.trace("Adding file [%s] to classpath", jarFile);
		addURL(jarFile.toURL());
	}

	public boolean addJARs(final File jarDirectory, final boolean recursive) throws IOException
	{
		Assert.argumentNotNull("jarDirectory", jarDirectory);

		final File[] children = jarDirectory.listFiles();
		if (children == null) return false;

		boolean jarsAdded = false;
		for (final File child : children)
			if (child.isFile() && child.getName().endsWith(".jar"))
			{
				addJAR(child);
				jarsAdded = true;
			}
			else if (recursive && child.isDirectory()) if (addJARs(child, true)) jarsAdded = true;
		return jarsAdded;
	}

	public boolean addJARs(final File jarDirectory, final boolean recursive, final Pattern jarNamePattern)
			throws IOException
	{
		Assert.argumentNotNull("jarDirectory", jarDirectory);
		Assert.argumentNotNull("jarNamePattern", jarNamePattern);

		final File[] children = jarDirectory.listFiles();
		if (children == null) return false;

		boolean jarsAdded = false;
		for (final File child : children)
			if (child.isFile() && jarNamePattern.matcher(child.getName()).matches())
			{
				addJAR(child);
				jarsAdded = true;
			}
			else if (recursive && child.isDirectory()) if (addJARs(child, true, jarNamePattern)) jarsAdded = true;
		return jarsAdded;
	}

	public boolean addJARs(final File jarDirectory, final boolean recursive, final String jarNamePattern)
			throws IOException
	{
		Assert.argumentNotNull("jarDirectory", jarDirectory);
		Assert.argumentNotNull("jarNamePattern", jarNamePattern);

		final File[] children = jarDirectory.listFiles();
		if (children == null) return false;

		boolean jarsAdded = false;
		final Pattern pattern = Pattern.compile(jarNamePattern);
		for (final File child : children)
			if (child.isFile() && pattern.matcher(child.getName()).matches())
			{
				addJAR(child);
				jarsAdded = true;
			}
			else if (recursive && child.isDirectory()) if (addJARs(child, true, pattern)) jarsAdded = true;

		return jarsAdded;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getResource(final String name)
	{
		// if class loader strategy is parent first, then use the default resource loading behavior
		if (!parentLast) return super.getResource(name);

		final URL url = findResource(name);
		return url == null ? super.getResource(name) : url;
	}

	public boolean isParentLast()
	{
		return parentLast;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized Class< ? > loadClass(final String name, final boolean resolve) throws ClassNotFoundException
	{
		Assert.argumentNotNull("name", name);

		// if class loader strategy is parent first, then use the default class loading behavior
		if (!parentLast) return super.loadClass(name, resolve);

		// see if the class was loaded already
		Class< ? > clazz = findLoadedClass(name);

		// load the class
		if (clazz == null) try
		{
			clazz = findClass(name);
			LOG.trace("Loaded class %s", name);
		}
		catch (final ClassNotFoundException ex)
		{
			LOG.trace("Loading class %s via parent class loader", name);
			// in case the class is not part of the registered URLs let the super class handle the class loading
			return super.loadClass(name, resolve);
		}

		if (resolve) resolveClass(clazz);
		return clazz;
	}

	public void setParentLast(final boolean parentLast)
	{
		this.parentLast = parentLast;
	}
}
