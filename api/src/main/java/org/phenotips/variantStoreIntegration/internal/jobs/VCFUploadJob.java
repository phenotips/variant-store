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
package org.phenotips.variantStoreIntegration.internal.jobs;

import org.phenotips.Constants;
import org.phenotips.data.Patient;
import org.phenotips.variantStoreIntegration.events.VCFUploadCompleteEvent;

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
 * A runnable class to wrap the future returned from a call to Variant Store addPatient. Ensures that patient
 * information is synchronized with the status of the variant store.
 *
 * @version $Id$
 */
public class VCFUploadJob implements Runnable
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
     * @param patient A PhenoTips Patient ID
     * @param variantStoreFuture The future returned by the variant store.
     * @param provider The xwiki context provider
     * @param observationManager The observation manager for event pubs
     */
    public VCFUploadJob(Patient patient, Future variantStoreFuture, Provider<XWikiContext> provider,
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
            // set patient VCF upload status to 'Inititialized' on disk
            XWiki xwiki = this.contextProvider.get().getWiki();
            XWikiDocument d = xwiki.getDocument(this.patient.getDocument(), this.contextProvider.get());

            BaseObject uploadStatusObj = d.getXObject(CLASS_REFERENCE, true, this.contextProvider.get());
            uploadStatusObj.set(propertyName, "Initialized", this.contextProvider.get());
            xwiki.saveDocument(d, this.contextProvider.get());

            this.future.get();

            // upon successful VCF upload set patient VCF upload status to 'Done' on disk
            uploadStatusObj.set(propertyName, "Done", this.contextProvider.get());
            xwiki.saveDocument(d, this.contextProvider.get());

            this.observationManager.notify(new VCFUploadCompleteEvent(this.patient), this);
        } catch (InterruptedException e) {
            // variant store job was interrupted (canceled?) set VCF upload status to null.
            XWiki xwiki = this.contextProvider.get().getWiki();
            XWikiDocument d;
            try {
                d = xwiki.getDocument(this.patient.getDocument(), this.contextProvider.get());
                BaseObject uploadStatusObj = d.getXObject(CLASS_REFERENCE, true, this.contextProvider.get());

                StringProperty status = (StringProperty) uploadStatusObj.get(propertyName);
                status.setValue("Cancelling");

                this.future.cancel(true);
                status.setValue(null);
            } catch (XWikiException e1) {
                this.future.cancel(true);
                this.observationManager.notify(new VCFUploadCompleteEvent(this.patient), this);
            }

        } catch (Exception e) {

            this.future.cancel(true);
            e.printStackTrace();
            this.observationManager.notify(new VCFUploadCompleteEvent(this.patient), this);
        }

    }
}
