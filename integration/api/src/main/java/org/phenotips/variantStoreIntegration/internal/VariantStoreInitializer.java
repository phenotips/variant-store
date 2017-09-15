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

import org.phenotips.variantStoreIntegration.VariantStoreService;

import org.xwiki.component.annotation.Component;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.ApplicationStartedEvent;
import org.xwiki.observation.event.ApplicationStoppedEvent;
import org.xwiki.observation.event.Event;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Hook into phenotips startup to pre-initialize the Variant Store.
 *
 * @version $Id$
 * @since 1.1M1
 */

@Component
@Named("variantstoreinitializer")
@Singleton
public class VariantStoreInitializer implements EventListener
{
    @Inject
    private VariantStoreService service;

    @Override
    public String getName()
    {
        return "variantstoreinitializer";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(
                new ApplicationStartedEvent(),
                new ApplicationStoppedEvent()
        );
    }

    @Override
    public void onEvent(Event event, Object o, Object o2)
    {
        // don't do anything, just injecting the service.
        if (event instanceof ApplicationStoppedEvent) {
            this.service.stop();
        }
    }
}
