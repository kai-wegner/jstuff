/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.integration.spring;

import net.sf.jstuff.core.StringUtils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * Bean names generated by {@link AnnotationBeanNameGenerator} starting with "Default" are
 * returned without this prefix. e.g. <code>defaultMailService</code> becomes <code>mailService</code>.
 * 
 * Bean names generated by {@link AnnotationBeanNameGenerator} ending with "Impl" are
 * returned without this suffix. e.g. <code>mailServiceImpl</code> becomes <code>mailService</code>.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AffixesRemovingBeanNameGenerator extends AnnotationBeanNameGenerator
{
	@Override
	public String generateBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry)
	{
		final String beanName = super.generateBeanName(definition, registry);
		if (beanName.startsWith("Default")) return StringUtils.substringAfter(beanName, "Default");
		if (beanName.endsWith("Impl")) return StringUtils.substringBeforeLast(beanName, "Impl");
		return beanName;
	}
}
