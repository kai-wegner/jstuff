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
package net.sf.jstuff.integration.serviceregistry;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceListener<SERVICE_INTERFACE> {
    void onServiceAvailable(ServiceProxy<SERVICE_INTERFACE> service);

    void onServiceUnavailable(ServiceProxy<SERVICE_INTERFACE> service);
}
