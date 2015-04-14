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
 * @version $Id: 1350962f052f3c535d6c023116cd41ad5f064e87 $
 * @since 1.1M1
 */

@Component
@Named("variantstoreinitializer")
@Singleton
public class VariantStoreInitializer implements EventListener
{
    @SuppressWarnings("unused")
    @Inject
    private VariantStoreService service;

    @Override
    public String getName() {
        return "variantstoreinitializer";
    }

    @Override
    public List<Event> getEvents() {
        return Arrays.<Event>asList(
                new ApplicationStartedEvent(),
                new ApplicationStoppedEvent()
        );
    }

    @Override
    public void onEvent(Event event, Object o, Object o2) {
        // don't do anything, just injecting the service.
        if (event instanceof ApplicationStoppedEvent) {
            service.stop();
        }
    }
}
