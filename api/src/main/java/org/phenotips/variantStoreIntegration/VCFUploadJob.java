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

import org.phenotips.Constants;
import org.phenotips.data.Patient;
import org.phenotips.variantStoreIntegration.events.VCFUploadCompleteEvent;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.ObservationManager;

import java.util.concurrent.Future;

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

    private XWikiContext context;

    private ObservationManager observationManager;

    /**
     * @param patient A PhenoTips Patient ID
     * @param variantStoreFuture The future returned by the variant store.
     * @param context The xwiki context
     * @param observationManager The observation manager for event pubs
     */
    public VCFUploadJob(Patient patient, Future variantStoreFuture, XWikiContext context,
        ObservationManager observationManager)
    {
        this.future = variantStoreFuture;
        this.patient = patient;
        this.context = context;
        this.observationManager = observationManager;
    }

    @Override
    public void run()
    {
        String propertyName = "status";
        try {
            // set patient VCF upload status to 'Inititialized' on disk
            XWiki xwiki = this.context.getWiki();
            XWikiDocument d = xwiki.getDocument(this.patient.getDocument(), this.context);

            BaseObject uploadStatusObj = d.getXObject(CLASS_REFERENCE, true, this.context);

            StringProperty status = (StringProperty) uploadStatusObj.get(propertyName);
            status.setValue("Initialized");
            xwiki.saveDocument(d, this.context);

            this.future.get();

            // upon successful VCF upload set patient VCF upload status to 'Done' on disk
            status.setValue("Done");
            xwiki.saveDocument(d, this.context);

            this.observationManager.notify(new VCFUploadCompleteEvent(this.patient), this);
        } catch (InterruptedException e) {
            // variant store job was interrupted (canceled?) set VCF upload status to null.
            XWiki xwiki = this.context.getWiki();
            XWikiDocument d;
            try {
                d = xwiki.getDocument(this.patient.getDocument(), this.context);
                BaseObject uploadStatusObj = d.getXObject(CLASS_REFERENCE, true, this.context);

                StringProperty status = (StringProperty) uploadStatusObj.get(propertyName);
                status.setValue("Cancelling");

                this.future.cancel(true);
                status.setValue(null);
            } catch (XWikiException e1) {
                // TODO figure out what to do here. Ignore it?
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
