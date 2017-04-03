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
package org.phenotips.variantStoreIntegration;

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
import org.phenotips.data.Patient;

import org.xwiki.component.annotation.Role;
import org.xwiki.component.phase.Initializable;

import java.util.List;

/**
 * The main component meant to interact with the variant store and coordinate PhenoTips events and data.
 *
 * @version $Id$
 */
@Role
public interface VCFUploadManager extends Initializable
{

    /**
     * Attempts to add the patient and VCF file to the variant store.
     *
     * @param patientID A valid PhenoTips patient ID
     * @param filePath A VCF path to upload
     * @throws Exception If there was an error while uploading the file.
     */
    void uploadVCF(String patientID, String filePath) throws Exception;

    /**
     * Attempts to cancel the given patients VCF upload.
     *
     * @param patient A valid PhenoTips patient
     */
    void cancelUpload(Patient patient);

    /**
     * Removes the patients variants from the variant store.
     *
     * @param patientID A valid PhenoTips patientID
     * @throws Exception If there was an error while uploading the file.
     */
    void removeVCF(String patientID) throws Exception;

    /**
     * Get a list of patients in the variant store.
     * @return a list of patients
     */
    List<String> getUploadedPatients();

}
