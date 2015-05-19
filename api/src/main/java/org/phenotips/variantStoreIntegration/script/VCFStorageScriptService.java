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
package org.phenotips.variantStoreIntegration.script;

import org.phenotips.data.Patient;
import org.phenotips.variantStoreIntegration.VCFUploadManager;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * API for providing access to the variant store.
 *
 * @version $Id$
 * @Since 1.0
 */
@Component
@Named("VCFStorage")
@Singleton
public class VCFStorageScriptService implements ScriptService
{
    @Inject
    private VCFUploadManager uploadManager;

    /**
     * @param patientID A PhenoTips patient ID.
     * @param filePath The path to where the patients VCF is stored
     */
    public void upload(String patientID, String filePath)
    {
        this.uploadManager.uploadVCF(patientID, filePath);
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
