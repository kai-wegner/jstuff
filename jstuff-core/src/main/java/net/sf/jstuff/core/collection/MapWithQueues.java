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
package net.sf.jstuff.core.collection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithQueues<K, V> extends MapWithCollections<K, V, Queue<V>>
{
	public static <K, V> MapWithQueues<K, V> create()
	{
		return new MapWithQueues<K, V>();
	}

	public MapWithQueues()
	{
		super();
	}

	@Override
	protected Queue<V> createCollection(final int initialCapacity, final float growthFactor)
	{
		return new ConcurrentLinkedQueue<V>();
	}
}
