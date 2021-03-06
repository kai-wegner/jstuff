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
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Functions.*;
import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FunctionsTest extends TestCase {
    public void testConverts() {
        final Function<Object, Integer> t1 = objectToString()//
            .and(stringToInt())//
            .and(castTo(Number.class))//
            .and(objectToString())//
            .and(trim()) //
            .and(stringToInt());

        assertEquals(null, t1.apply(null));
        assertEquals(Integer.valueOf(1), t1.apply("1"));
    }
}
