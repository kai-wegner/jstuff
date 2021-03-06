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
package net.sf.jstuff.integration.persistence.hibernate;

import java.io.Serializable;
import java.util.Properties;

import net.sf.jstuff.core.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;

/**
 * <code>
 * &#64;javax.persistence.Id
 * &#64;javax.persistence.GeneratedValueValue(generator = "oid")
 * &#64;org.hibernate.annotations.GenericGenerator(
 *    name = "oid", strategy = net.sf.jstuff.integration.persistence.hibernate.FixedLengthOIDGenerator.class.getName(),
 *    parameters = {
 *	     &#64;Parameter(name = net.sf.jstuff.integration.persistence.hibernate.FixedLengthOIDGenerator.PARAM_SUFFIX, value = "001"),
 *       &#64;Parameter(name = net.sf.jstuff.integration.persistence.hibernate.FixedLengthOIDGenerator.PARAM_PREFIX, value = "AAA"),
 *       &#64;Parameter(name = net.sf.jstuff.integration.persistence.hibernate.FixedLengthOIDGenerator.PARAM_LENGTH, value = "32"),
 *       &#64;Parameter(name = "max_lo", value = "1")}
 * )
 * private Integer oid;
 * </code>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FixedLengthStringOIDGenerator extends TableHiLoGenerator {
    private static final Logger LOG = Logger.create();

    public static final String PARAM_LENGTH = "length";
    public static final String PARAM_PREFIX = "prefix";
    public static final String PARAM_SUFFIX = "suffix";

    private int idLength;
    private int length;
    private String prefix;
    private String suffix;
    private String fill;

    public FixedLengthStringOIDGenerator() {
        LOG.infoNew(this);
    }

    @Override
    public void configure(final Type type, final Properties params, final Dialect d) {
        super.configure(new IntegerType(), params, d);

        length = PropertiesHelper.getInt(PARAM_LENGTH, params, 16);
        prefix = PropertiesHelper.getString(PARAM_PREFIX, params, "");
        suffix = PropertiesHelper.getString(PARAM_SUFFIX, params, "");
        idLength = length - prefix.length() - suffix.length();

        fill = StringUtils.repeat("0", length);
    }

    @Override
    public synchronized Serializable generate(final SessionImplementor session, final Object obj) throws HibernateException {
        final int id = ((Integer) super.generate(session, obj)).intValue();
        final String idAsHexString = Integer.toHexString(id);

        return prefix + fill.substring(0, idLength - idAsHexString.length()) + idAsHexString + suffix;
    }
}
