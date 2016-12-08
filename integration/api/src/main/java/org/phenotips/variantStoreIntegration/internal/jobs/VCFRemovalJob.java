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
package org.phenotips.variantStoreIntegration.internal.jobs;

import org.phenotips.Constants;
import org.phenotips.data.Patient;
import org.phenotips.variantStoreIntegration.events.VCFRemovalCompleteEvent;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.ObservationManager;

import java.util.concurrent.Future;

import javax.inject.Provider;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.StringProperty;

/**
 * The wrapper class for the future returned from a call to variant stores remove method.
 *
 * @version $Id$
 */
public class VCFRemovalJob implements Runnable
{
    /**
     * Entity reference.
     */
    public static final EntityReference CLASS_REFERENCE = new EntityReference("VCFStatusClass", EntityType.DOCUMENT,
        Constants.CODE_SPACE_REFERENCE);

    private Future future;

    private Patient patient;

    private Provider<XWikiContext> contextProvider;

    private ObservationManager observationManager;

    /**
     * @param patient A PhenoTips Patient
     * @param variantStoreFuture The future returned by the variant store.
     * @param provider The xwiki context provider
     * @param observationManager The observation manager for event pubs
     */
    public VCFRemovalJob(Patient patient, Future variantStoreFuture, Provider<XWikiContext> provider,
        ObservationManager observationManager)
    {
        this.future = variantStoreFuture;
        this.patient = patient;
        this.contextProvider = provider;
        this.observationManager = observationManager;
    }

    @Override
    public void run()
    {
        String propertyName = "status";
        try {
            // set patient VCF removal status to 'Removing' on disk
            XWiki xwiki = this.contextProvider.get().getWiki();
            XWikiDocument d = xwiki.getDocument(this.patient.getDocument(), this.contextProvider.get());

            BaseObject removingStatusObj = d.getXObject(CLASS_REFERENCE, true, this.contextProvider.get());
            removingStatusObj.set(propertyName, "Removing", this.contextProvider.get());
            xwiki.saveDocument(d, this.contextProvider.get());

            this.future.get();

            // upon successful VCF removal set patient VCF removal status to 'Done' on disk
            removingStatusObj.set(propertyName, "Done", this.contextProvider.get());
            xwiki.saveDocument(d, this.contextProvider.get());

            this.observationManager.notify(new VCFRemovalCompleteEvent(this.patient), this);
        } catch (InterruptedException e) {
            // variant store job was interrupted (canceled?) set VCF removal status to null.
            XWiki xwiki = this.contextProvider.get().getWiki();
            XWikiDocument d;
            try {
                d = xwiki.getDocument(this.patient.getDocument(), this.contextProvider.get());
                BaseObject removingStatusObj = d.getXObject(CLASS_REFERENCE, true, this.contextProvider.get());

                StringProperty status = (StringProperty) removingStatusObj.get(propertyName);
                status.setValue("Cancelling");

                this.future.cancel(true);
                status.setValue(null);
            } catch (XWikiException e1) {
                this.future.cancel(true);
                this.observationManager.notify(new VCFRemovalCompleteEvent(this.patient), this);
            }

        } catch (Exception e) {

            this.future.cancel(true);
            e.printStackTrace();
            this.observationManager.notify(new VCFRemovalCompleteEvent(this.patient), this);
        }

    }
}
