/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.integration.serviceregistry.impl;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.integration.serviceregistry.ServiceListener;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteBuddyServiceRegistryITest extends TestCase {
    private static final class CountingListener<T> implements ServiceListener<T> {
        private final AtomicInteger count;

        public CountingListener(final AtomicInteger count) {
            this.count = count;
        }

        public void onServiceAvailable(final ServiceProxy<T> service) {
            count.incrementAndGet();
        }

        public void onServiceUnavailable(final ServiceProxy<T> service) {
            count.incrementAndGet();
        }
    };

    public static class DefaultService1 implements Service1 {
        public String getGreeting() {
            return "Hello";
        }
    }

    public static class DefaultService2 implements Service2 {
        public String getGreeting() {
            return "Hello";
        }
    }

    public static class DefaultService2Extended extends DefaultService2 implements Service2Extended {
        public String getGoodbye() {
            return "Adios";
        }
    }

    public interface Service1 extends TestService {
        String ENDPOINT_ID = Service1.class.getName();

        String getGreeting();
    }

    public interface Service2 extends TestService {
        String ENDPOINT_ID = Service2.class.getName();

        String getGreeting();
    }

    public interface Service2Extended extends Service2 {
        String getGoodbye();
    }

    public interface TestService {
    }

    private ByteBuddyServiceRegistry registry;

    @Override
    protected void setUp() throws Exception {
        registry = new ByteBuddyServiceRegistry();
    }

    @Override
    protected void tearDown() throws Exception {
        registry = null;
    }

    public void testGarbageCollection() {
        assertEquals(0, registry.getServieEndpointsCount());
        @SuppressWarnings("unused")
        final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
        ServiceProxy<Service2> srv2Proxy = registry.getService(Service2.ENDPOINT_ID, Service2.class);
        final int srv2ProxyHashCode = srv2Proxy.hashCode();
        assertTrue(srv2ProxyHashCode == registry.getService(Service2.ENDPOINT_ID, Service2.class).hashCode());
        assertEquals(2, registry.getServieEndpointsCount());
        System.gc();
        Threads.sleep(100);
        assertEquals(2, registry.getServieEndpointsCount());
        srv2Proxy = null;
        System.gc();
        Threads.sleep(100);
        registry.addService(Service1.class, new DefaultService1()); // this will trigger execution of _cleanup method
        assertEquals(1, registry.getServieEndpointsCount());

        registry.addService(Service2.class, new DefaultService2());
        assertFalse(srv2ProxyHashCode == registry.getService(Service2.ENDPOINT_ID, Service2.class).hashCode());
    }

    @SuppressWarnings("unused")
    public void testServiceInheritance() throws IOException {
        final Service2 srv2 = new DefaultService2();
        final Service2Extended srv2Ext = new DefaultService2Extended();
        assertFalse(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable());

        registry.addService(Service2.ENDPOINT_ID, Service2.class, srv2);
        assertEquals(DefaultService2.class, registry.getService(Service2.ENDPOINT_ID, Service2.class).getServiceImplementationClass());
        assertTrue(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable());
        assertFalse(registry.getService(Service2.ENDPOINT_ID, Service2Extended.class).isServiceAvailable());
        registry.removeService(Service2.ENDPOINT_ID, srv2);

        registry.addService(Service2.ENDPOINT_ID, Service2Extended.class, srv2Ext);
        assertEquals(DefaultService2Extended.class, registry.getService(Service2.ENDPOINT_ID, Service2.class).getServiceImplementationClass());
        assertEquals(DefaultService2Extended.class, registry.getService(Service2.ENDPOINT_ID, Service2Extended.class).getServiceImplementationClass());
        assertTrue(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable());
        assertTrue(registry.getService(Service2.ENDPOINT_ID, Service2Extended.class).isServiceAvailable());
        registry.removeService(Service2.ENDPOINT_ID, srv2Ext);
    }

    public void testServiceListener() {
        final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
        final AtomicInteger count = new AtomicInteger();
        final CountingListener<ByteBuddyServiceRegistryITest.Service1> listener = new CountingListener<ByteBuddyServiceRegistryITest.Service1>(count);
        srv1Proxy.addServiceListener(listener);

        final DefaultService1 srv1Impl = new DefaultService1();

        count.set(0);
        registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
        registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
        assertEquals(1, count.get());

        count.set(0);
        registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
        registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
        assertEquals(1, count.get());

        count.set(0);
        srv1Proxy.removeServiceListener(listener);
        registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
        assertEquals(0, count.get());
    }

    public void testServiceListenerGC() throws InterruptedException {
        ServiceProxy<Runnable> srv1Proxy = registry.getService(Runnable.class.getName(), Runnable.class);
        final AtomicInteger count = new AtomicInteger();
        CountingListener<Runnable> listener = new CountingListener<Runnable>(count);
        assertTrue(srv1Proxy.addServiceListener(listener));
        assertFalse(srv1Proxy.addServiceListener(listener));

        srv1Proxy = null; // remove ref to service proxy, but still hold ref to listener
        System.gc();
        Thread.sleep(500);

        final Runnable service = new Runnable() {
            public void run() {
            }
        };

        count.set(0);
        registry.addService(Runnable.class.getName(), Runnable.class, service);
        registry.removeService(Runnable.class.getName(), service);
        assertEquals(2, count.get());

        listener.toString(); // this call ensures that the listener is not GCed before by some JIT optimization
        listener = null; // also remove ref to listener
        System.gc();
        Thread.sleep(500);

        count.set(0);
        registry.addService(Runnable.class.getName(), Runnable.class, service);
        registry.removeService(Runnable.class.getName(), service);
        assertEquals(0, count.get());
    }

    @SuppressWarnings("unused")
    public void testServiceRegistry() throws IOException {
        assertNotNull(registry.getService(Service1.ENDPOINT_ID, Service1.class));
        assertNotNull(registry.getService(Service2.ENDPOINT_ID, Service2.class));
        assertFalse(registry.getService(Service1.ENDPOINT_ID, Service1.class).isServiceAvailable());
        assertFalse(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable());

        // test adding one service
        DefaultService1 srv1Impl = new DefaultService1();
        registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
        final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
        assertTrue(srv1Proxy.isServiceAvailable());
        final Service1 srv1 = srv1Proxy.get();
        assertNotNull(srv1);
        assertEquals("Hello", srv1.getGreeting());
        assertNotSame(srv1, srv1Impl);
        assertTrue(srv1 instanceof ServiceProxy);
        assertSame(srv1, srv1Proxy);
        assertEquals(Service1.ENDPOINT_ID, srv1Proxy.getServiceEndpointId());
        assertSame(Service1.class, srv1Proxy.getServiceInterface());
        assertSame(srv1Proxy, registry.getService(Service1.ENDPOINT_ID, Service1.class));

        // test adding another service instance to the same endpoint
        try {
            registry.addService(Service1.ENDPOINT_ID, Service1.class, new DefaultService1());
            fail();
        } catch (final IllegalStateException ex) {
            // expected
        }

        // test removing service1
        registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
        assertSame(srv1Proxy, registry.getService(Service1.ENDPOINT_ID, Service1.class));
        assertFalse(srv1Proxy.isServiceAvailable());

        // test loading service2
        final DefaultService2 srv2Impl = new DefaultService2();
        registry.addService(Service2.ENDPOINT_ID, Service2.class, srv2Impl);
        assertNotNull(registry.getService(Service1.ENDPOINT_ID, Service1.class));
        assertNotNull(registry.getService(Service2.ENDPOINT_ID, Service2.class));

        // test reloading service1
        assertFalse(srv1Proxy.isServiceAvailable());
        srv1Impl = new DefaultService1();
        registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
        assertTrue(srv1Proxy.isServiceAvailable());
        assertSame(srv1Proxy, registry.getService(Service1.ENDPOINT_ID, Service1.class));

        assertEquals(2, registry.getActiveServiceEndpoints().size());

        // test replacing service at endpoint1 with a service with a different interface (e.g. same class from a different classloader)
        registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
        assertEquals(1, registry.getActiveServiceEndpoints().size());
        registry.addService(Service1.ENDPOINT_ID, Service2.class, srv2Impl);
        assertEquals(false, srv1Proxy.isServiceAvailable());
        assertNotNull(registry.getService(Service1.ENDPOINT_ID, Service1.class));
        assertNotNull(registry.getService(Service1.ENDPOINT_ID, Service2.class));
    }
}
