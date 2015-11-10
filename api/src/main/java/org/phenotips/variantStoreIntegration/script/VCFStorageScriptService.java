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

import org.phenotips.data.Patient;
import org.phenotips.variantStoreIntegration.VCFUploadManager;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;


import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import net.sf.json.JSONObject;

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
    private static final String STATUS_STRING = "status";
    @Inject
    private VCFUploadManager uploadManager;

    /**
     * @param patientID A PhenoTips patient ID.
     * @param filePath The path to where the patients VCF is stored
     * @return A json object with key status representing the status of the intial request. NOTE: The status is only
     * relevant to the submission of the request. Failures during asynchronous vcf processing will not effect the status
     * of the request.
     */
    public JSONObject upload(String patientID, String filePath)
    {
        JSONObject response = new JSONObject();
        try {
            this.uploadManager.uploadVCF(patientID, filePath);
            response.element(STATUS_STRING, 201);
        } catch (Exception e) {
            response.element(STATUS_STRING, 500);
            response.element("message", e.getMessage());
            response.element("trace", e.getStackTrace());
        }
        return response;
    }

    /**
     * Cancels the ongoing upload of the given patient's VCF.
     *
     * @param patient a phenotips patient
     */
    public void cancelUpload(Patient patient)
    {
        this.uploadManager.cancelUpload(patient);
    }

    /**
     * Removes Patients VCF from the variant store.
     *
     * @param patient a PhenoTips patient
     */
    public void removeVCF(Patient patient)
    {
        this.uploadManager.removeVCF(patient);
    }

    /**
     * @param patient a PhenoTips patient
     * @return The current status of the VCF file 0-Unknown. 1-Uploading 2-Stored -1-Removing
     */
    public int getVCFStatus(Patient patient)
    {
        // I guess this will just query the database??? Doesn't seem like such a good way of doing things.
        return 0;
    }
}
