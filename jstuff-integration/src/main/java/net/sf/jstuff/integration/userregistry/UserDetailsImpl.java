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
package net.sf.jstuff.integration.userregistry;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UserDetailsImpl implements Serializable, UserDetails
{
	private static final long serialVersionUID = 1L;

	private final String displayName;
	private final String distingueshedName;
	private final String eMailAddress;
	private final String logonName;
	private final String userId;

	/**
	 * @param userId
	 * @param displayName
	 * @param logonName
	 * @param distingueshedName
	 * @param mailAddress
	 */
	public UserDetailsImpl(final String userId, final String displayName, final String logonName,
			final String distingueshedName, final String mailAddress)
	{
		this.userId = userId;
		this.displayName = displayName;
		this.logonName = logonName;
		this.distingueshedName = distingueshedName;
		eMailAddress = mailAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDistingueshedName()
	{
		return distingueshedName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEMailAddress()
	{
		return eMailAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLogonName()
	{
		return logonName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
