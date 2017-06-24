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
package org.phenotips.variantStoreIntegration.script;

import org.phenotips.data.ConsentManager;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientRepository;
import org.phenotips.variantStoreIntegration.VCFUploadManager;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.security.authorization.AuthorizationManager;
import org.xwiki.security.authorization.Right;
import org.xwiki.users.User;
import org.xwiki.users.UserManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.json.JSONObject;

/**
 * API for providing access to the variant store.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("VCFStorage")
@Singleton
public class VCFStorageScriptService implements ScriptService
{
    static final String MESSAGE = "message";
    private static final String STATUS_STRING = "status";
    private static final String UPLOAD_STRING = "upload";
    private static final String REMOVE_STRING = "remove";
    @Inject
    private VCFUploadManager uploadManager;

    @Inject
    private PatientRepository repository;

    @Inject
    private UserManager users;

    @Inject
    private AuthorizationManager access;

    @Inject
    private ConsentManager consentManager;

    /**
     * @param patientID A PhenoTips patient ID.
     * @param filePath  The path to where the patients VCF is stored
     *
     * @return A json object with key status representing the status of the initial request. NOTE: The status is only
     * relevant to the submission of the request. Failures during asynchronous vcf processing will not effect the status
     * of the request.
     */
    public JSONObject upload(String patientID, String filePath) {
        return executeJob(patientID, filePath, UPLOAD_STRING);
    }

    /**
     * Cancels the ongoing upload of the given patient's VCF.
     *
     * @param patient a phenotips patient
     */
    public void cancelUpload(Patient patient) {
        this.uploadManager.cancelUpload(patient);
    }

    /**
     * Removes Patients VCF from the variant store.
     *
     * @param patientID a PhenoTips patient ID
     *
     * @return A json object with key status representing the status of the initial request. NOTE: The status is only
     * relevant to the submission of the request. Failures during asynchronous vcf processing will not effect the status
     * of the request.
     */
    public JSONObject removeVCF(String patientID) {
        return executeJob(patientID, null, REMOVE_STRING);
    }

    /**
     * @param patient a PhenoTips patient
     *
     * @return The current status of the VCF file 0-Unknown. 1-Uploading 2-Stored -1-Removing
     */
    public int getVCFStatus(Patient patient) {
        // I guess this will just query the database??? Doesn't seem like such a good way of doing things.
        return 0;
    }

    /**
     * Get a list of uploaded patients.
     *
     * @return a list of patients.
     */
    public List<String> getUploadedPatients() {
        return uploadManager.getUploadedPatients();
    }

    private JSONObject executeJob(String patientID, String filePath, String jobName) {
        JSONObject response = new JSONObject();

        Patient patient = this.repository.get(patientID);
        if (patient == null) {
            response.put(STATUS_STRING, 404);
            response.put(MESSAGE, "patient " + patientID + " not found");
            return response;
        }
        User currentUser = this.users.getCurrentUser();
        if (!this.access.hasAccess(Right.EDIT, currentUser == null ? null : currentUser.getProfileDocument(),
                patient.getDocument())) {
            response.put(STATUS_STRING, 401);
            response.put(MESSAGE, "The current user is not authorized to edit this patient");
            return response;
        }
        if (!this.consentManager.hasConsent(patientID, "genetic")
            || !this.consentManager.hasConsent(patientID, "real")) {
            response.put(STATUS_STRING, 401);
            response.put(MESSAGE, "All required consents have not been granted for this patient");
            return response;
        }
        try {
            if (UPLOAD_STRING.equals(jobName)) {
                this.uploadManager.uploadVCF(patientID, filePath);
            } else if (REMOVE_STRING.equals(jobName)) {
                this.uploadManager.removeVCF(patientID);
            }
            response.put(STATUS_STRING, 202);
        } catch (Exception e) {
            response.put(STATUS_STRING, 500);
            response.put(MESSAGE, e.getMessage());
            response.put("trace", e.getStackTrace());
        }
        return response;
    }

    /**
     * Given an individual, get a timestamp of a TSV file generated by exomiser.
     * @param patientID the id of the individual
     * @return the file creation timestamp
     */
    public String getTSVTimeStamp(String patientID) {
        return this.uploadManager.getTSVTimeStamp(patientID);
    }
}
