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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.variantstore;

import org.phenotips.variantstore.db.DatabaseController;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.exomiser6.tsv.Exomiser6TSVManager;
import org.phenotips.variantstore.input.vcf.VCFManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.ga4gh.GAVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Variant Store is capable of storing a large number of individuals genomic variants for further querying and
 * sorting.
 *
 * @version $Id$
 */
public class VariantStore implements VariantStoreInterface
{
    private static Logger logger = LoggerFactory.getLogger(VariantStore.class);
    private Path path;
    private InputManager inputManager;
    private DatabaseController db;

    /**
     * Use VCF and Solr by default.
     */
    public VariantStore() {
        this.inputManager = new VCFManager();
        this.db = new SolrController();
    }

    /**
     * Specify your own DB and Input format.
     *
     * @param inputManager an input format manager
     * @param db           the db implementation
     */
    public VariantStore(InputManager inputManager, DatabaseController db) {
        this.path = path;
        this.inputManager = inputManager;
        this.db = db;
    }

    /**
     * Main method for testing purposes. TODO:REMOVE
     *
     * @param args commandline args
     */
    public static void main(String[] args) {
        logger.debug("Starting");
        VariantStore vs = null;

        vs = new VariantStore(
                new Exomiser6TSVManager(),
                new SolrController()
        );

        try {
            vs.init(Paths.get("/data/dev-variant-store"));
        } catch (VariantStoreException e) {
            logger.error("Error initializing VariantStore", e);
            vs.stop();
            return;
        }

        logger.debug("Started");


        Collection<File> files = FileUtils.listFiles(Paths.get("/data/vcf/c4r/pc-full/").toFile(),
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE);
        try {
            for (String id : Arrays.asList("F0000012", "F0000012", "F0000012")) {
//                String id = FilenameUtils.removeExtension(tsv.getName());
//                if (vs.getIndividuals().contains(id)) {
//                    continue;
//                }
                long startTime;
                long endTime;

                startTime = System.currentTimeMillis();
                vs.addIndividual(
                        (String) id,
                        true,
                        Paths.get(String.format("/data/vcf/c4r/pc/%s.variants.tsv.pass", id))
                ).get();
                endTime = System.currentTimeMillis();
                logger.debug(String.format("csv: Insertion (ms): %d", endTime - startTime));

            }
            timeQueries(vs);
        } catch (InterruptedException | ExecutionException | VariantStoreException e) {
            logger.error("Error", e);
            vs.stop();
            return;
        }

        vs.stop();
        logger.debug("Stopped");
    }

    private static void timeQueries(VariantStore vs) {
        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();
        logger.debug(String.format("csv: Beacon: %s", vs.beacon("chr1", (long) 120572547, "C")));
        endTime = System.currentTimeMillis();
        logger.debug(String.format("csv: Beacon (ms): %d", endTime - startTime));

        List<String> effects = Arrays.asList("MISSENSE",
                "FS_DELETION",
                "FS_INSERTION",
                "NON_FS_DELETION",
                "NON_FS_INSERTION",
                "STOPGAIN",
                "STOPLOSS",
                "FS_DUPLICATION",
                "SPLICING",
                "NON_FS_DUPLICATION",
                "FS_SUBSTITUTION",
                "NON_FS_SUBSTITUTION",
                "STARTLOSS",
                "ncRNA_EXONIC",
                "ncRNA_SPLICING",
                "UTR3",
                "UTR5",
                "SYNONYMOUS",
                "INTRONIC",
                "ncRNA_INTRONIC",
                "UPSTREAM",
                "DOWNSTREAM",
                "INTERGENIC");
        Map<String, Double> afs = new HashMap<>();
        afs.put("EXAC", 0.01);
        afs.put("PhenomeCentral", 0.1);
        for (String gene : Arrays.asList("EFTUD", "NGLY1", "SRCAP", "TTN", "NOTCH2")) {
            startTime = System.currentTimeMillis();
            vs.getIndividualsWithGene(gene, effects, afs);
            endTime = System.currentTimeMillis();
            logger.debug(String.format("csv: Mendelian for %s (ms): %d", gene, endTime - startTime));
        }

//        for (String id : vs.getIndividuals()) {
        for (String id : Arrays.asList("F0000012", "F0000013")) {
            startTime = System.currentTimeMillis();
            Set<String> genes = vs.getAllGenesForIndividual(id);
            endTime = System.currentTimeMillis();
            logger.debug(String.format("csv: getAllGenesForIndividual(%s).size() = %d (ms): %d",
                    id, genes.size(), endTime - startTime));

            if (genes.size() > 0) {
                String gene = genes.iterator().next();

                startTime = System.currentTimeMillis();
                Double score = vs.getGeneScore(id, gene);
                endTime = System.currentTimeMillis();
                logger.debug(String.format("csv: getGeneScore(%s, %s) = %f (ms): %d",
                        id, gene, score, endTime - startTime));

                startTime = System.currentTimeMillis();
                List<GAVariant> variants = vs.getTopHarmfullVariantsForGene(id, gene, 5);
                endTime = System.currentTimeMillis();
                logger.debug(String.format("csv: getTopHarmfullVariantsForGene(%s, %s).size() = %d (ms): %d",
                        id, gene, variants.size(), endTime - startTime));
            }

            startTime = System.currentTimeMillis();
            List<String> topGenes = vs.getTopGenesForIndividual(id, 5);
            endTime = System.currentTimeMillis();
            logger.debug(String.format("csv: getTopGenesForIndividual(%s).size() = %d (ms): %d",
                    id, topGenes.size(), endTime - startTime));
        }

        startTime = System.currentTimeMillis();
        logger.debug(String.format("csv: Total Variants: %d", vs.getTotNumVariants()));
        endTime = System.currentTimeMillis();
        logger.debug(String.format("csv: Total Variants (ms): %d", endTime - startTime));
        logger.debug(String.format("csv: Total Individuals: %d", vs.getIndividuals().size()));

    }

    @Override
    public void init(Path path) throws VariantStoreException {
        this.path = path;
        db.init(this.path.resolve("db"));
        inputManager.init(this.path.resolve("tsv"));
    }

    @Override
    public void stop() {
        db.stop();
    }

    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException {
        logger.debug("Adding " + id + " from " + file.toString());
        // copy file to file cache
        inputManager.addIndividual(id, file);

        return this.db.addIndividual(this.inputManager.getIteratorForIndividual(id, isPublic));
    }

    @Override
    public Future removeIndividual(String id) throws VariantStoreException {
        this.inputManager.removeIndividual(id);
        return this.db.removeIndividual(this.inputManager.getIteratorForIndividual(id));
    }

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return this.db.getTopHarmfullVariants(id, n);
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(
            String geneSymbol,
            List<String> variantEffects,
            Map<String, Double> alleleFrequencies) {
        return this.db.getIndividualsWithGene(geneSymbol,
                variantEffects,
                alleleFrequencies,
                5,
                this.getIndividuals().size());
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        return this.db.getIndividualsWithVariant(chr, pos, ref, alt);
    }

    @Override
    public List<String> getIndividuals() {
        return this.inputManager.getAllIndividuals();
    }

    @Override
    public Set<String> getAllGenesForIndividual(String id) {
        return this.db.getAllGenesForIndividual(id);
    }

    @Override
    public Double getGeneScore(String id, String gene) {
        return this.db.getGeneScore(id, gene);
    }

    @Override
    public List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k) {
        return this.db.getTopHarmfullVariantsForGene(id, gene, k);
    }

    @Override
    public List<String> getTopGenesForIndividual(String id, Integer k) {
        return this.db.getTopGenesForIndividual(id, k);
    }

    /**
     * The implementation method for the GA4GH.
     *
     * @param chr    chromosome
     * @param pos    position
     * @param allele allele
     *
     * @return the allele frequency of this variant in the db.
     */
    public double beacon(String chr, long pos, String allele) {
        return (double) this.db.beacon(chr, pos, allele) / ((double) this.getIndividuals().size() * 2);
    }

    /**
     * Get the total number of variants in the db.
     *
     * @return the total number of variants in the db.
     */
    public long getTotNumVariants() {
        return db.getTotNumVariants();
    }

}
