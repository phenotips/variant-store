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
package org.phenotips.variantStoreIntegration.internal.jobs;

import org.phenotips.variantStoreIntegration.events.VCFEvent;

import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * An object to wrap a map of unfinished futures. Once a future completes it will be removed from the map.
 *
 * @version $Id$
 */
public class FutureManager implements EventListener
{

    @SuppressWarnings("rawtypes")
    private Map<String, Future> futures;

    private String name;

    private Event removalEvent;

    /**
     * @param name The name of this future manager. Used to register it as an Event Listener
     * @param removalEvent The event that the future manger listens for. Causes the removal of a future from the
     *            manager.
     */
    public FutureManager(String name, Event removalEvent)
    {
        this.name = name;
        this.futures = new HashMap<>();
        this.removalEvent = removalEvent;

    }

    /**
     * @param id The key for the future
     * @param f The stored future
     */
    @SuppressWarnings("rawtypes")
    public void add(String id, Future f)
    {
        this.futures.put(id, f);
    }

    /**
     * @param id The key for the future
     * @return Future The stored future
     */
    @SuppressWarnings("rawtypes")
    public Future get(String id)
    {
        return this.futures.get(id);
    }

    /**
     * @param id The key for the future
     */
    public void remove(String id)
    {
        this.futures.remove(id);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<Event> getEvents()
    {
        return Collections.<Event>singletonList(this.removalEvent);
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        this.futures.remove(((VCFEvent) event).getPatient().getId());
    }

}
