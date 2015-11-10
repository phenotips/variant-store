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
package org.phenotips.variantStoreIntegration.events;

import org.phenotips.data.Patient;

import org.xwiki.users.User;

/**
 * VCF event fired upon successful completion of the removal of a patients VCF from the variant store.
 *
 * @version $Id$
 */
public class VCFRemovalCompleteEvent implements VCFEvent
{
    private Patient patient;

    /**
     * @param patient A valid PhenoTips patient
     */
    public VCFRemovalCompleteEvent(Patient patient) {
        this.patient = patient;
    }

    @Override
    public boolean matches(Object otherEvent) {
        if (otherEvent instanceof VCFRemovalCompleteEvent) {
            VCFRemovalCompleteEvent otherUploadEvent = (VCFRemovalCompleteEvent) otherEvent;
            return this.patient == null || (otherUploadEvent.getPatient() != null && this.patient.getDocument().equals(
                    otherUploadEvent.getPatient().getDocument()));

        }
        return false;
    }

    @Override
    public Patient getPatient() {
        return this.patient;
    }

    @Override
    public String getEventType() {
        return null;
    }

    @Override
    public User getAuthor() {
        return null;
    }
}
