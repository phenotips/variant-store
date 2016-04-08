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

import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantStoreIntegration.internal.AbstractVariantStoreProxy;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.script.service.ScriptService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * A script service that directly exposes all methods in {{@link org.phenotips.variantstore.VariantStore}} to velocity.
 *
 * Often in PhenoTips you see fat ScriptServices that pass methods onto the actual Component with the buisness logic.
 * In this case, both the Component and the ScriptService implement a "proxy" functionality, where they just
 * marshall the method calls onto the actual {{@link org.phenotips.variantstore.VariantStore}} implementation.
 *
 * @version $Id$
 */
@Component
@Named("VariantStore")
@Singleton
public class VariantStoreScriptService extends AbstractVariantStoreProxy implements ScriptService, Initializable
{
    @Inject
    protected VariantStoreService variantStoreService;

    @Override
    public void initialize() throws InitializationException {
        this.variantStore = variantStoreService;
    }
}
