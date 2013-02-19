/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Types
{
	public static <T> T createSynchronized(final Class<T> objectInterface, final T object)
	{
		Args.notNull("objectInterface", objectInterface);
		Args.notNull("object", object);

		return createSynchronized(objectInterface, object, object);
	}

	public static <T> T createSynchronized(final Class<T> objectInterface, final T object, final Object lock)
	{
		Args.notNull("objectInterface", objectInterface);
		Args.notNull("object", object);

		return Proxies.create(objectInterface, new InvocationHandler()
			{
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					synchronized (lock)
					{
						return method.invoke(object, args);
					}
				}
			});
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(final Object obj)
	{
		return (T) obj;
	}

	/**
	 * @return true if objects of type <code>from</code> can be casted to type <code>to</code>
	 */
	public static boolean isCastable(final Class< ? > fromType, final Class< ? > toType)
	{
		Args.notNull("fromType", fromType);
		Args.notNull("toType", toType);

		return toType.isAssignableFrom(fromType);
	}

	public static boolean isScalar(final Class< ? > type)
	{
		return type == boolean.class || type == Boolean.class || //
				type == char.class || type == Character.class || //
				type == int.class || //
				type == long.class || //
				type == byte.class || //
				type == short.class || //
				type == float.class || //
				Enum.class.isAssignableFrom(type) || //
				Number.class.isAssignableFrom(type) || //
				CharSequence.class.isAssignableFrom(type) || //
				Date.class.isAssignableFrom(type);
	}
}