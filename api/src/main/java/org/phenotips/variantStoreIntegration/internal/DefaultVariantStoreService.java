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
package org.phenotips.variantStoreIntegration.internal;

import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantstore.VariantStore;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.environment.Environment;

import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import org.ga4gh.GAVariant;

/**
 * @version $Id: 1f3bc36ff53b79ba95f90d7f1eaa24fa48d6bf4a $
 */
@Component
public class DefaultVariantStoreService implements Initializable, VariantStoreService
{
    @Inject
    private Environment env;

    private VariantStore variantStore;

    @Override
    public void initialize() throws InitializationException {
        variantStore = new VariantStore();

        try {
            variantStore.init(Paths.get(this.env.getPermanentDirectory().getPath()).resolve("variant-store"));
        } catch (VariantStoreException e) {
            throw new InitializationException("Error setting up Variant Store", e);
        }
    }

    @Override
    public List<GAVariant> getTopHarmfulVariants(String id, int n) {
        return null;
    }
}
