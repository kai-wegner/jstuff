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
package net.sf.jstuff.integration.persistence.jpa;

import static net.sf.jstuff.core.StringUtils.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import net.sf.jstuff.core.Identifiable;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.date.ImmutableDate;
import net.sf.jstuff.core.reflection.ReflectionUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@MappedSuperclass
public abstract class AbstractJPAEntity<KeyType extends Serializable> implements Serializable, Identifiable<KeyType>
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.create();

	/* ******************************************************************************
	 * Generic Entity Properties
	 * ******************************************************************************/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "_createdOn", updatable = false, nullable = false)
	// we assign a value here to work around http://opensource.atlassian.com/projects/hibernate/browse/EJB-46 which may also exist with other JPA implementations
	private final Date _firstPersistedOn = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "_modifiedOn", nullable = false)
	private Date _lastPersistedOn;

	@Basic(optional = false)
	@Column(nullable = false)
	private boolean _isMarkedAsDeleted = false;

	@Version
	@Column(nullable = false)
	private final int _version = 0;

	public final boolean _isMarkedAsDeleted()
	{
		return _isMarkedAsDeleted;
	}

	public void _setMarkedAsDeleted(final boolean isMarkedAsDeleted)
	{
		_isMarkedAsDeleted = isMarkedAsDeleted;
	}

	/**
	 * @return true if this is a new entity that has not been persisted yet
	 */
	public final boolean _isNew()
	{
		return getId() != null;
	}

	public final ImmutableDate _getFirstPersistedOn()
	{
		return new ImmutableDate(_firstPersistedOn);
	}

	public final ImmutableDate _getLastPersistedOn()
	{
		return new ImmutableDate(_lastPersistedOn);
	}

	public final int _getVersion()
	{
		return _version;
	}

	@PrePersist
	private void onPrePersist()
	{
		_lastPersistedOn = new Date();
	}

	@PreUpdate
	private void onPreUpdate()
	{
		_lastPersistedOn = new Date();
	}

	@Override
	public final String toString()
	{
		return getClass().getName() + "[id=" + getId() + ", version=, " + _getVersion() + "hashCode=" + hashCode() + "]";
	}

	public CharSequence toDebugString()
	{
		final StringBuilder sb = new StringBuilder(64);
		Class< ? > currClazz = this.getClass();
		String intend = "";

		try
		{
			while (currClazz != Object.class)
			{
				sb.append(intend).append(currClazz).append(" *** START ***").append(NEW_LINE);
				intend += "  ";
				for (final Field field : currClazz.getDeclaredFields())
					if (!ReflectionUtils.isStatic(field) && !field.getName().startsWith("class$"))
					{
						final Object fieldValue = ReflectionUtils.getFieldValue(field, this);
						if (field.getType().isAssignableFrom(AbstractJPAEntity.class))
						{
							if (fieldValue == null)
								sb.append(intend).append("[").append(field.getName()).append("] ").append(fieldValue).append(" | ")
										.append(field.getType().getName()).append(NEW_LINE);
							else
							{
								final AbstractJPAEntity< ? > referencedEntity = (AbstractJPAEntity< ? >) fieldValue;
								sb.append(intend).append("[").append(field.getName()).append("] id=").append(referencedEntity.getId())
										.append(" | ").append(referencedEntity.getClass().getName()).append(NEW_LINE);
							}
						}
						else
							sb.append(intend + "[" + field.getName() + "] " + fieldValue + " | " + field.getType().getName() + NEW_LINE);
					}
				sb.append(NEW_LINE);

				currClazz = currClazz.getSuperclass();
			}
			return sb.append(this.getClass()).append(" *** END ***").append(NEW_LINE);
		}
		catch (final Exception ex)
		{
			LOG.warn("toDebugString() failed on " + this, ex);
			return toString();
		}
	}
}