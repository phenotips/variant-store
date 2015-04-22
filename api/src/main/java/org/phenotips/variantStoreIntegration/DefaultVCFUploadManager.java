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

import org.phenotips.data.Patient;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.data.permissions.Visibility;
import org.phenotips.variantStoreIntegration.VCFUploadManager;
import org.phenotips.variantStoreIntegration.events.VCFRemovalCompleteEvent;
import org.phenotips.variantStoreIntegration.events.VCFUploadCompleteEvent;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.context.Execution;
import org.xwiki.observation.ObservationManager;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.xpn.xwiki.XWikiContext;

/**
 * A PhenoTips component to manage the uploading of VCF files to the variant store.
 *
 * @version $Id$
 */

@Component
@Singleton
public class DefaultVCFUploadManager implements VCFUploadManager
{
    @Inject
    private static PermissionsManager permissions;

    @Inject
    @Named("hidden")
    private static Visibility hiddenVisibility;

    @Inject
    private Logger logger;

    @Inject
    private MockVariantStore varStore;

    /** Provides access to the current execution context. */
    @Inject
    private Execution execution;

    @Inject
    private ObservationManager observationManager;

    private ExecutorService executor;

    private FutureManager currentUploads;

    private FutureManager currentRemovals;

    @Override
    public void initialize() throws InitializationException
    {
        this.varStore.init();
        this.executor = Executors.newCachedThreadPool();

        this.currentUploads = new FutureManager("currentUploads", new VCFUploadCompleteEvent(null));
        this.currentRemovals = new FutureManager("currentRemovals", new VCFRemovalCompleteEvent(null));

        this.observationManager.addListener(this.currentUploads);
        this.observationManager.addListener(this.currentRemovals);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.phenotips.variantStoreIntegration.VCFUploadManager#uploadVCF(org.phenotips.data.Patient, java.io.File)
     */
    @Override
    public void uploadVCF(Patient patient, Path filePath)
    {
        String id = patient.getId();

        if (this.currentUploads.get(id) != null) {
            this.logger.warn("Tried to upload VCF of {} while it was already uploading", patient.toString());
            return;
        } else if (this.currentRemovals.get(id) != null) {
            this.logger.warn("Tried to upload VCF of {} while it was being removed", patient.toString());
            return;
        }

        XWikiContext context = (XWikiContext) this.execution.getContext().getProperty("xwikicontext");

        boolean isPublic = DefaultVCFUploadManager.resolvePatientPermission(id);

        Future varStoreFuture = this.varStore.addIndividual(id, isPublic, filePath);
        VCFUploadJob newUploadJob = new VCFUploadJob(patient, varStoreFuture, context, this.observationManager);

        this.currentUploads.add(id, this.executor.submit(newUploadJob));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.phenotips.variantStoreIntegration.VCFUploadManager#cancelUpload(org.phenotips.data.Patient)
     */
    @Override
    public void cancelUpload(Patient patient)
    {
        String id = patient.getId();

        if (this.currentUploads.get(id) != null) {
            this.currentUploads.get(id).cancel(true);
            this.currentUploads.remove(id);
        } else {
            this.logger.warn("Tried to cancel the VCF upload of {} but failed because it was not uploading!",
                patient.toString());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.phenotips.variantStoreIntegration.VCFUploadManager#removeVCF(org.phenotips.data.Patient)
     */
    @Override
    public void removeVCF(Patient patient)
    {
        String id = patient.getId();
        if (this.currentUploads.get(id) != null) {
            this.logger.warn("Tried to remove the VCF of {} while it was uploading", patient.toString());
            return;
        } else if (this.currentRemovals.get(id) != null) {
            this.logger.warn("Tried to remove the VCF of {} while it was already removing", patient.toString());
            return;
        }

        Future varStoreFuture = this.varStore.removeIndividual(id);
        VCFRemovalJob newRemovalJob = new VCFRemovalJob(id, varStoreFuture);
        this.currentRemovals.add(id, this.executor.submit(newRemovalJob));
    }

    private static boolean resolvePatientPermission(String id)
    {
        Visibility patientVisibility = permissions.resolveVisibility(id);
        return (patientVisibility.compareTo(hiddenVisibility) > 0);
    }

}
