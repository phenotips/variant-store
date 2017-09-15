/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.variantStoreIntegration.internal;

import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantstore.VariantStore;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.tsv.ExomiserTSVManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.environment.Environment;

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @version $Id$
 */
@Component
@Singleton
public class DefaultVariantStoreService extends AbstractVariantStoreProxy implements Initializable, VariantStoreService
{
    @Inject
    private Environment env;

    @Override
    public void initialize() throws InitializationException
    {
        this.variantStore = new VariantStore(
                new ExomiserTSVManager(),
                new SolrController()
        );

        try {
            this.variantStore.init(Paths.get(this.env.getPermanentDirectory().getPath()).resolve("variant-store"));
        } catch (VariantStoreException e) {
            throw new InitializationException("Error setting up Variant Store", e);
        }
    }
}
