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
package net.sf.jstuff.integration.persistence;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jstuff.core.GCTracker;
import net.sf.jstuff.core.collection.WeakIdentityHashSet;
import net.sf.jstuff.core.types.Identifiable;
import net.sf.jstuff.core.validation.Args;

/**
 * Alternative solution to solve:
 *
 * http://stackoverflow.com/questions/5031614/the-jpa-hashcode-equals-dilemma
 * http://stackoverflow.com/questions/4323245/what-is-the-best-practice-when-implementing-equals-for-entities-with-generated
 * http://onjava.com/pub/a/onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html?page=3
 * https://community.jboss.org/wiki/EqualsandHashCode
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class HashCodeManager {
    private static final class FQId {
        private final Object id;
        private final Object realm;

        private FQId(final Object realm, final Object id) {
            this.realm = realm;
            this.id = id;
        }

        @Override
        public boolean equals(final Object obj) {
            /* not needed as we have full control over the usage of this class's objects:
             * if (this == obj) return true;
             * if (obj == null) return false;
             * if (getClass() != obj.getClass()) return false;
             */
            final FQId other = (FQId) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (realm == null) {
                if (other.realm != null)
                    return false;
            } else if (!realm.equals(other.realm))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (id == null ? 0 : id.hashCode());
            result = prime * result + (realm == null ? 0 : realm.hashCode());
            return result;
        }
    }

    private static final class HashCodeAssignment {
        protected final int hashCode;
        protected FQId id;
        protected final WeakIdentityHashSet<Identifiable<?>> identifiables = WeakIdentityHashSet.create();

        protected HashCodeAssignment(final int hashCode) {
            this.hashCode = hashCode;
        }
    }

    private static final GCTracker<String> GC_TRACKER = new GCTracker<String>(100) {
        @Override
        protected void onGCEvent(final String trackingId) {
            try {
                final HashCodeAssignment ida = HASHCODE_ASSIGNMENT_BY_TRACKING_ID.get(trackingId);
                if (ida == null)
                    return;

                synchronized (ida.identifiables) {
                    if (ida.identifiables.size() == 0) {
                        HASHCODE_ASSIGNMENT_BY_TRACKING_ID.remove(trackingId, ida);
                    }
                }

                if (ida.id == null)
                    return;
                final HashCodeAssignment ida2 = HASHCODE_ASSIGNMENT_BY_ID.get(ida.id);
                if (ida2 != null) {
                    synchronized (ida2.identifiables) {
                        if (ida2.identifiables.size() == 0) {
                            HASHCODE_ASSIGNMENT_BY_ID.remove(ida2.id, ida2);
                        }
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private static ConcurrentMap<FQId, HashCodeAssignment> HASHCODE_ASSIGNMENT_BY_ID = new ConcurrentHashMap<FQId, HashCodeAssignment>();
    private static ConcurrentMap<String, HashCodeAssignment> HASHCODE_ASSIGNMENT_BY_TRACKING_ID = new ConcurrentHashMap<String, HashCodeAssignment>();

    private static final AtomicLong LOCAL_ID_GENERATOR = new AtomicLong(0);
    private static final String LOCAL_JVM_ID = UUID.randomUUID().toString();

    public static int getManagedIdsCount() {
        return HASHCODE_ASSIGNMENT_BY_ID.size();
    }

    public static int getManagedTrackingIdsCount() {
        return HASHCODE_ASSIGNMENT_BY_TRACKING_ID.size();
    }

    private static HashCodeAssignment getOrRegisterHashCodeAssignmentByTrackingId(final Identifiable<?> entity, final String trackingId) {
        final HashCodeAssignment newHca = new HashCodeAssignment(trackingId.hashCode());
        HashCodeAssignment hca = HASHCODE_ASSIGNMENT_BY_TRACKING_ID.putIfAbsent(trackingId, newHca);
        if (hca == null) {
            hca = newHca;
        }
        synchronized (hca.identifiables) {
            hca.identifiables.add(entity);
        }
        return hca;
    }

    public static int hashCodeFor(final Identifiable<?> entity, final String trackingId) {
        Args.notNull("entity", entity);
        Args.notNull("trackingId", trackingId);

        final Object id = entity.getId();
        if (id != null) {
            final HashCodeAssignment ida = HASHCODE_ASSIGNMENT_BY_ID.get(new FQId(entity.getIdRealm(), id));
            return ida == null ? id.hashCode() : ida.hashCode;
        }
        return getOrRegisterHashCodeAssignmentByTrackingId(entity, trackingId).hashCode;
    }

    /**
     * @return a new cross-JVM unique hash code tracking id
     */
    public static <T> String onEntityInstantiated(final Identifiable<T> entity) {
        Args.notNull("entity", entity);

        final String trackingId = LOCAL_JVM_ID + "#" + LOCAL_ID_GENERATOR.getAndIncrement();
        GC_TRACKER.track(entity, trackingId);
        return trackingId;
    }

    public static <T> void onIdSet(final Identifiable<T> entity, final String trackingId) {
        Args.notNull("entity", entity);
        Args.notNull("trackingId", trackingId);

        /*
         * register trackingId hashCode with the entity ID
         */
        final HashCodeAssignment newHca = new HashCodeAssignment(trackingId.hashCode());
        final FQId id = new FQId(entity.getIdRealm(), entity.getId());
        HashCodeAssignment hca = HASHCODE_ASSIGNMENT_BY_ID.putIfAbsent(id, newHca);
        if (hca == null) {
            hca = newHca;
        }
        synchronized (hca.identifiables) {
            hca.id = id;
            hca.identifiables.add(entity);
        }

        getOrRegisterHashCodeAssignmentByTrackingId(entity, trackingId).id = id;
    }
}
