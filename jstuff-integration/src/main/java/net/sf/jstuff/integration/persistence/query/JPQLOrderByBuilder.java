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
package net.sf.jstuff.integration.persistence.query;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JPQLOrderByBuilder
{
	private final SortBy[] defaultSortBy;
	private final Map<String, String> mappings = new HashMap<String, String>(4);

	public JPQLOrderByBuilder(final SortBy... defaultSortBy)
	{
		this.defaultSortBy = defaultSortBy;
	}

	public JPQLOrderByBuilder addMapping(final String sortField, final String jpqlExpression)
	{
		mappings.put(sortField, jpqlExpression);
		return this;
	}

	public String buildOrderBy(final SortBy[] sortBy) throws IllegalArgumentException
	{
		final StringBuilder orderBy = new StringBuilder();

		for (final SortBy sb : getActiveSortBy(sortBy))
		{
			if (sb.getDirection() == null)
				throw new IllegalArgumentException("Sort direction not specified for sort field [" + sb.getField()
						+ "]");

			final String jpqlExpression = mappings.get(sb.getField());
			if (jpqlExpression == null)
				throw new IllegalArgumentException("Invalid sorting field [" + sb.getField() + "]");

			orderBy.append(' ');
			orderBy.append(jpqlExpression);
			orderBy.append(' ');
			orderBy.append(sb.getDirection());
			orderBy.append(',');
		}
		orderBy.setLength(orderBy.length() - 1); // strip off the last comma
		return orderBy.toString();
	}

	public SortBy[] getActiveSortBy(final SortBy[] sortBy)
	{
		if (sortBy == null || sortBy.length == 0) return defaultSortBy;
		return sortBy;
	}
}
