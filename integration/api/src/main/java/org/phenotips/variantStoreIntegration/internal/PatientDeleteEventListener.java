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
import org.phenotips.data.events.PatientDeletedEvent;
import org.phenotips.data.events.PatientEvent;
import org.phenotips.variantStoreIntegration.VCFUploadManager;
import org.phenotips.variantStoreIntegration.VariantStoreService;

import org.xwiki.component.annotation.Component;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.observation.event.Event;

import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;

/**
 * Monitors document deletion and removes patient data from variant store via {@link VCFUploadManager uploadManager}.
 *
 * @version $Id$
 */
@Component
@Named("variant-store-patient-remover")
@Singleton
public class PatientDeleteEventListener extends AbstractEventListener
{
    @Inject
    private Logger logger;

    @Inject
    private VariantStoreService varStore;

    /** Default constructor, sets up the listener name and the list of events to subscribe to. */
    public PatientDeleteEventListener()
    {
        super("variant-store-patient-remover", new PatientDeletedEvent());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(final Event event, final Object source, final Object data)
    {
        Patient patient = ((PatientEvent) event).getPatient();
        try {
            @SuppressWarnings("unused")
            Future varStoreFuture = this.varStore.removeIndividual(patient.getId());
        } catch (Exception ex) {
            this.logger.error("Failed to remove individual from variant store [{}]: {}", patient.getId(),
                ex.getMessage(), ex);
        }
    }
}
