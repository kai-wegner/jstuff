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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingCollection<V> implements Collection<V>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Collection<V> delegate;

    public DelegatingCollection(final Collection<V> delegate) {
        Args.notNull("delegate", delegate);

        this.delegate = delegate;
    }

    public boolean add(final V o) {
        return delegate.add(o);
    }

    public boolean addAll(final Collection<? extends V> c) {
        return delegate.addAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public Iterator<V> iterator() {
        return delegate.iterator();
    }

    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    public int size() {
        return delegate.size();
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }
}
