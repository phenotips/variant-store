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
import org.phenotips.data.PatientRepository;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.data.permissions.Visibility;
import org.phenotips.variantStoreIntegration.VCFUploadManager;
import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantStoreIntegration.events.VCFRemovalCompleteEvent;
import org.phenotips.variantStoreIntegration.events.VCFUploadCompleteEvent;
import org.phenotips.variantStoreIntegration.internal.jobs.FutureManager;
import org.phenotips.variantStoreIntegration.internal.jobs.VCFRemovalJob;
import org.phenotips.variantStoreIntegration.internal.jobs.VCFUploadJob;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.context.concurrent.ExecutionContextRunnable;
import org.xwiki.observation.ObservationManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
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
    private VariantStoreService varStore;

    /** Provides access to the current execution context. */
    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private PatientRepository pr;

    @Inject
    private ComponentManager componentManager;

    private ExecutorService executor;

    private FutureManager currentUploads;

    private FutureManager currentRemovals;

    @Override
    public void initialize() throws InitializationException
    {
        this.executor = Executors.newCachedThreadPool();

        this.currentUploads = new FutureManager("currentUploads", new VCFUploadCompleteEvent(null));
        this.currentRemovals = new FutureManager("currentRemovals", new VCFRemovalCompleteEvent(null));

        this.observationManager.addListener(this.currentUploads);
        this.observationManager.addListener(this.currentRemovals);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void uploadVCF(String patientID, String filePath) throws Exception
    {
        Patient patient = this.pr.getPatientById(patientID);

        if (patient == null) {
            this.logger.warn("No patient found with the id: {}", patientID);
            throw new Exception("Could not find the patient with ID: " + patientID);
        }

        File vcfFile = new File(filePath);
        if (!vcfFile.exists() || !vcfFile.isFile()) {
            this.logger.warn("Attempted to upload an invalid VCF file");
            throw new Exception("No file was found at: " + filePath);
        }

        if (this.currentUploads.get(patientID) != null) {
            this.logger.warn("Tried to upload VCF of {} while it was already uploading", patientID);
            return;
        } else if (this.currentRemovals.get(patientID) != null) {
            this.logger.warn("Tried to upload VCF of {} while it was being removed", patientID);
            return;
        }

        boolean isPublic = DefaultVCFUploadManager.resolvePatientPermission(patient);

        Future varStoreFuture = null;
        try {
            varStoreFuture = this.varStore.addIndividual(patientID, isPublic, vcfFile.toPath());
            VCFUploadJob newUploadJob = new VCFUploadJob(patient, varStoreFuture, contextProvider,
                this.observationManager);
            ExecutionContextRunnable wrappedJob = new ExecutionContextRunnable(newUploadJob, componentManager);
            this.currentUploads.add(patientID, this.executor.submit(wrappedJob));
        } catch (VariantStoreException e) {
            this.logger.warn("Variant store exception thrown when trying to upload a vcf for: {}", patientID);
            e.printStackTrace();
        }

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

        Future varStoreFuture = null;
        try {
            varStoreFuture = this.varStore.removeIndividual(id);
            VCFRemovalJob newRemovalJob = new VCFRemovalJob(id, varStoreFuture);
            this.currentRemovals.add(id, this.executor.submit(newRemovalJob));
        } catch (VariantStoreException e) {
            this.logger.warn("Variant store exception thrown when trying to remove a vcf for: {}", patient.getId());
            e.printStackTrace();
        }

    }

    private static boolean resolvePatientPermission(Patient patient)
    {

        Visibility patientVisibility = permissions.getPatientAccess(patient).getVisibility();
        return (patientVisibility.compareTo(hiddenVisibility) > 0);
    }

}
