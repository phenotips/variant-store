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

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContext;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.nio.file.Path;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;

import com.xpn.xwiki.XWikiContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link DefaultVCFUploadManager} .
 *
 * @version $Id: f5805e3443d52c6e851b125b01713a2d1b7e774b $
 */
public class DefaultVCFUploadManagerTest
{
    @Rule
    public final MockitoComponentMockingRule<DefaultVCFUploadManager> mocker =
        new MockitoComponentMockingRule<DefaultVCFUploadManager>(DefaultVCFUploadManager.class);

    private Visibility hiddenVisibility;

    private MockVariantStore varStore;

    private PermissionsManager permissions;

    private Execution execution;

    @Before
    public void setup() throws ComponentLookupException
    {
        MockitoAnnotations.initMocks(this);
        this.varStore = this.mocker.getInstance(MockVariantStore.class);
        this.permissions = this.mocker.getInstance(PermissionsManager.class);
        this.execution = this.mocker.getInstance(Execution.class);
        this.hiddenVisibility = this.mocker.getInstance(Visibility.class, "hidden");
    }

    /** Basic tests for */
    @Test
    public void testNormalVCFUpload() throws ComponentLookupException
    {
        Patient patient = mock(Patient.class);
        Path filePath = mock(Path.class);
        Future varStoreFuture = mock(Future.class);
        ExecutionContext xContext = mock(ExecutionContext.class);
        XWikiContext context = mock(XWikiContext.class);
        Visibility visibility = mock(Visibility.class);

        when(patient.getId()).thenReturn("123");
        when(this.execution.getContext()).thenReturn(xContext);
        when(xContext.getProperty(Matchers.anyString())).thenReturn(context);

        when(this.permissions.resolveVisibility(Matchers.anyString())).thenReturn(visibility);
        when(visibility.compareTo(this.hiddenVisibility)).thenReturn(1);

        when(this.varStore.addIndividual("123", true, filePath))
            .thenReturn(varStoreFuture);

        VCFUploadManager mgr = this.mocker.getComponentUnderTest();
        FutureManager cu = mock(FutureManager.class);
        ReflectionUtils.setFieldValue(mgr, "currentUploads", cu);
        FutureManager cr = mock(FutureManager.class);
        ReflectionUtils.setFieldValue(mgr, "currentRemovals", cr);

        when(cu.get("123")).thenReturn(null);
        when(cr.get("123")).thenReturn(null);

        mgr.uploadVCF(patient, filePath);

    }
}
