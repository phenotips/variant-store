/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.variantStoreIntegration;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

import java.util.List;

import org.ga4gh.GAVariant;

/**
 * Service that exposes Phenotips' Variant Store.
 *
 * @version $Id: 47fbeef7d4aac08639f2dc9016b2e6c6d3923293 $
 * @since 1.1M1
 */
@Unstable
@Role
public interface VariantStoreService
{
    /**
     * @param id Patient Id
     * @param n  Number of Variants to return
     * @return a list of GAVariants that are the most harmful
     */
    List<GAVariant> getTopHarmfulVariants(String id, int n);

    /**
     * Safely stop the VariantStore.
     */
    void stop();
}
