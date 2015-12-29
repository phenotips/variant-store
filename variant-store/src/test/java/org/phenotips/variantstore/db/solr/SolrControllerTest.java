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
        //todo: see what error gets thrown on get, fix it.

    }
}
