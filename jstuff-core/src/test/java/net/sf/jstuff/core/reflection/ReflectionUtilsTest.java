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
package net.sf.jstuff.core.reflection;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReflectionUtilsTest extends TestCase
{

	public void testIsCastable()
	{
		assertTrue(ReflectionUtils.isCastable(Integer.class, Number.class));
		assertFalse(ReflectionUtils.isCastable(Number.class, Integer.class));

		assertTrue(ReflectionUtils.isCastable(String.class, Object.class));
	}
}