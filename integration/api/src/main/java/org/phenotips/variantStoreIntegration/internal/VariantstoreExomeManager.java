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

import org.phenotips.data.Patient;
import org.phenotips.data.similarity.Exome;
import org.phenotips.data.similarity.ExomeManager;
import org.phenotips.variantStoreIntegration.VariantStoreService;

import org.xwiki.component.annotation.Component;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;

/**
 * This is an implementation of the {@link ExomeManager}, and allows accessing {@link VariantstoreExome} objects for the
 * given {@link Patient}.
 *
 * @version $Id$
 * @since 1.0M6
 */
@Component
@Singleton
@Named("variantstore-exomiser")
public class VariantstoreExomeManager implements ExomeManager
{
    @Inject
    private static Logger logger;

    @Inject
    private VariantStoreService vs;

    /**
     * Get the {@link Exome} for the given {@link Patient}.
     *
     * @param p the patient for which the {@link Exome} will be retrieved
     *
     * @return the corresponding {@link Exome}, or {@code null} if no exome available
     */
    @Override
    public Exome getExome(Patient p) {
        List<String> allIndividuals = vs.getAllIndividuals();
        if (!allIndividuals.contains(p.getId())) {
            logger.info("No exome data exists for " + p.getId());
            return null;
        }
        Exome exome = new VariantstoreExome(vs, p);
        return exome;
    }
}
