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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Created by meatcar on 6/12/15.
 */
public class SolrControllerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public static final List<String> TSV_FILES = Arrays.asList("large-field.variants.tsv", "patient.variants.tsv");

    private Path tsvs;
    private Path solr;

    private VariantHeader header;

    @Before
    public void before() throws IOException {
        Files.createDirectories(folder.getRoot().toPath().resolve("tsvs"));
        // copy resources
        for (String file : TSV_FILES) {
            InputStream in = getClass().getResourceAsStream("/tsvs/" + file);
            if (in == null) {
                continue;
            }
            Files.copy(in, folder.getRoot().toPath().resolve("tsvs/" + file), StandardCopyOption.REPLACE_EXISTING);
        }

        tsvs = folder.getRoot().toPath().resolve("tsvs");
        solr = folder.getRoot().toPath().resolve("solr");

        header = new VariantHeader("someId", true);
    }

    //@Test
    @SuppressWarnings("rawtypes")
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
