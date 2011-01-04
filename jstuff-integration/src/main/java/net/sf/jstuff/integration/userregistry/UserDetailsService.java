/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.jstuff.integration.userregistry;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface UserDetailsService
{
	UserDetails getUserDetailsByLogonName(String logonName);

	UserDetails getUserDetailsByUserId(String userId);
}
