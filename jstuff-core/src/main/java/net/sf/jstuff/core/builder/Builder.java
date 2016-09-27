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
package net.sf.jstuff.core.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Builder<TARGET_TYPE> {
    TARGET_TYPE build();

    /**
     * To specify the default behaviour for all properties add this annotation to the builder interface,
     * otherwise to the respective interface methods representing the properties.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    public @interface Property {

        /**
         * specifies if the property can have a null value
         */
        boolean nullable() default false;

        /**
         * specifies if the property must be set
         */
        boolean required() default false;
    }

}
