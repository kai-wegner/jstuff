/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AuthenticationFailedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public AuthenticationFailedException()
	{
		super();
	}

	public AuthenticationFailedException(final String message)
	{
		super(message);
	}

	public AuthenticationFailedException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public AuthenticationFailedException(final Throwable cause)
	{
		super(cause);
	}
}
