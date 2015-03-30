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

import java.util.concurrent.Future;

/**
 * The wrapper class for the future returned from a call to variant stores remove method.
 *
 * @version $Id$
 */
public class VCFRemovalJob implements Runnable
{
    private Future future;

    private String patientId;

    /**
     * @param patientId The unique id of the patient.
     * @param variantStoreFuture The future returned by the variant store.
     */
    public VCFRemovalJob(String patientId, Future variantStoreFuture)
    {
        this.future = variantStoreFuture;
        this.patientId = patientId;
    }

    @Override
    public void run()
    {
        try {
            // set patient VCF upload status to 'Removing' on disk

            this.future.get();

            // upon successful VCF upload set patient VCF upload status to 'null' on disk
        } catch (InterruptedException e) {
            this.future.cancel(true);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
