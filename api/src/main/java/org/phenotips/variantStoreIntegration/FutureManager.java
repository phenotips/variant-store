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

import org.phenotips.variantStoreIntegration.Events.VCFEvent;

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
    public void add(String id, Future f)
    {
        this.futures.put(id, f);
    }

    /**
     * @param id The key for the future
     * @return Future The stored future
     */
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
