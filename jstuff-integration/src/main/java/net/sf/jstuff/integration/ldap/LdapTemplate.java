/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.integration.ldap;

import java.util.Hashtable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;

import net.sf.jstuff.core.functional.Invocable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapTemplate
{
	private String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
	private Hashtable<String, Object> ldapSettings;
	private String ldapURL;
	private boolean pooled = true;
	private boolean useStartTSL = false;

	public Object execute(final Invocable<Object, LdapContext> callback)
	{
		LdapContext ctx = null;

		try
		{
			ctx = new InitialLdapContext(ldapSettings, null);
			if (useStartTSL) ((StartTlsResponse) ctx.extendedOperation(new StartTlsRequest())).negotiate();
			return callback.invoke(ctx);
		}
		catch (final Exception ex)
		{
			throw new LdapException(ex);
		}
		finally
		{
			LdapUtils.closeQuietly(ctx);
		}
	}

	/**
	 * @return the initialContextFactory
	 */
	public String getInitialContextFactory()
	{
		return initialContextFactory;
	}

	/**
	 * @return the ldapURL
	 */
	public String getLdapURL()
	{
		return ldapURL;
	}

	/**
	 * {@inheritDoc}
	 */
	@PostConstruct
	public void initialize()
	{
		ldapSettings = new Hashtable<String, Object>();
		ldapSettings.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		ldapSettings.put(Context.PROVIDER_URL, ldapURL);
		ldapSettings.put(Context.REFERRAL, "throw");
		if (pooled) ldapSettings.put("com.sun.jndi.ldap.connect.pool", "true");
	}

	/**
	 * @return the pooled
	 */
	public boolean isPooled()
	{
		return pooled;
	}

	public boolean isUseStartTSL()
	{
		return useStartTSL;
	}

	/**
	 * @param initialContextFactory the initialContextFactory to set
	 */
	public void setInitialContextFactory(final String initialContextFactory)
	{
		this.initialContextFactory = initialContextFactory;
	}

	/**
	 * @param ldapURL the ldapURL to set
	 */
	@Inject
	public void setLdapURL(final String ldapURL)
	{
		this.ldapURL = ldapURL;
	}

	/**
	 * @param pooled the pooled to set
	 */
	public void setPooled(final boolean pooled)
	{
		this.pooled = pooled;
	}

	public void setUseStartTSL(final boolean useStartTSL)
	{
		this.useStartTSL = useStartTSL;
	}
}