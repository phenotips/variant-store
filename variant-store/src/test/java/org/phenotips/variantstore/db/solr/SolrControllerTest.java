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
package org.phenotips.variantstore.db.solr;

import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.input.tsv.ExomiserTSVIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

/**
 * Created by meatcar on 6/12/15.
 */
public class SolrControllerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Path tsvs;
    private Path solr;

    private VariantHeader header;

    @Before
    public void before() throws IOException {
        // copy resources
        FileUtils.copyDirectoryToDirectory(Paths.get(getClass().getResource("/tsvs").getPath()).toFile(), folder.getRoot());
        tsvs = folder.getRoot().toPath().resolve("tsvs");
        solr = folder.getRoot().toPath().resolve("solr");

        header = new VariantHeader("someId", true);
    }

    @Test
    public void testAddIndividualWithHugeField() throws Exception {
        ExomiserTSVIterator iterator = new ExomiserTSVIterator(tsvs.resolve("large-field.variants.tsv"), header);
        SolrController controller = new SolrController();
        controller.init(solr);
        Future future = controller.addIndividual(iterator);

        future.get();
        future = controller.addIndividual(iterator);
        future.get();
    }
}
