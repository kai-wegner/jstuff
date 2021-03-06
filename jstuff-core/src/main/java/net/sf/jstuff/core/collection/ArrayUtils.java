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
package net.sf.jstuff.core.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.functional.Function;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {
    public static <T> boolean containsIdentical(final T[] theArray, final T theItem) {
        Args.notNull("theArray", theArray);

        for (final T t : theArray)
            if (t == theItem)
                return true;
        return false;
    }

    /**
     * Returns a new list with all items accepted by the filter
     * 
     * @throws IllegalArgumentException if <code>accept == null</code>
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T[] filter(final Accept<? super T> accept, final T... array) throws IllegalArgumentException {
        if (array == null)
            return null;
        if (array.length == 0)
            return array;
        Args.notNull("accept", accept);
        final ArrayList<T> result = CollectionUtils.newArrayList();
        for (final T item : array)
            if (accept.accept(item))
                result.add(item);

        return result.toArray((T[]) Array.newInstance(array.getClass().getComponentType(), result.size()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Collection<T> values, final Class<T> itemType) {
        if (values == null)
            return null;

        return values.toArray((T[]) Array.newInstance(itemType, values.size()));
    }

    public static List<Boolean> toList(final boolean... array) {
        if (array == null)
            return null;

        final ArrayList<Boolean> result = new ArrayList<Boolean>(array.length);
        for (final boolean i : array)
            result.add(i);
        return result;
    }

    public static List<Byte> toList(final byte... array) {
        if (array == null)
            return null;

        final ArrayList<Byte> result = new ArrayList<Byte>(array.length);
        for (final byte i : array)
            result.add(i);
        return result;
    }

    public static List<Character> toList(final char... array) {
        if (array == null)
            return null;

        final ArrayList<Character> result = new ArrayList<Character>(array.length);
        for (final char i : array)
            result.add(i);
        return result;
    }

    public static List<Double> toList(final double... array) {
        if (array == null)
            return null;

        final ArrayList<Double> result = new ArrayList<Double>(array.length);
        for (final double i : array)
            result.add(i);
        return result;
    }

    public static List<Float> toList(final float... array) {
        if (array == null)
            return null;

        final ArrayList<Float> result = new ArrayList<Float>(array.length);
        for (final float i : array)
            result.add(i);
        return result;
    }

    public static List<Integer> toList(final int... array) {
        if (array == null)
            return null;

        final ArrayList<Integer> result = new ArrayList<Integer>(array.length);
        for (final int i : array)
            result.add(i);
        return result;
    }

    public static List<Long> toList(final long... array) {
        if (array == null)
            return null;

        final ArrayList<Long> result = new ArrayList<Long>(array.length);
        for (final long i : array)
            result.add(i);

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(final Object array, @SuppressWarnings("unused") final Class<T> itemType) {
        if (array == null)
            return null;
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("[array] is not an array but of type: " + array.getClass());

        final int l = Array.getLength(array);
        final List<Object> result = CollectionUtils.newArrayList(l);
        for (int i = 0; i < l; i++)
            result.add(Array.get(array, i));
        return (List<T>) result;
    }

    public static List<Short> toList(final short... array) {
        if (array == null)
            return null;

        final ArrayList<Short> result = new ArrayList<Short>(array.length);
        for (final short i : array)
            result.add(i);
        return result;
    }

    public static <T> List<T> toList(final T... array) {
        return CollectionUtils.newArrayList(array);
    }

    public static <S, T> T[] transform(final S[] source, final Class<T> targetType, final Function<? super S, ? extends T> op) {
        if (source == null)
            return null;

        @SuppressWarnings("unchecked")
        final T[] target = (T[]) Array.newInstance(targetType, source.length);
        for (int i = 0, l = source.length; i < l; i++)
            target[i] = op.apply(source[i]);
        return target;
    }
}
