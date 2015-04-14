package org.phenotips.variantstore.input.tsv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.VariantHeader;

/**
 * Wrap around a CSV file flattened by vcfflatten, with exomiser results appened to the end of each line. Expose each
 * line as a ga4gh GAVariant object.
 */
public class ExomiserTSVIterator extends AbstractVariantIterator {
    private Logger logger = Logger.getLogger(ExomiserTSVIterator.class);
    private CSVParser tsvParser;
    private Iterator<CSVRecord> tsvRecordIterator;

    public ExomiserTSVIterator(Path path, VariantHeader variantHeader) {
        super(path, variantHeader);

        Reader reader = null;
        try {
            reader = new FileReader(this.path.toString());
        } catch (FileNotFoundException e) {
            logger.error(String.format("Error when opening file %s, this should NOT be happening", this.path), e);
        }

        try {
            this.tsvParser = CSVFormat.TDF.parse(reader);
        } catch (IOException e) {
            logger.error(String.format("Error when opening file %s, this should NOT be happening", this.path), e);
        }

        this.tsvRecordIterator = tsvParser.iterator();
        // skip first row >.>
        if (this.hasNext()) {
            tsvRecordIterator.next();
        }
    }

    @Override
    public boolean hasNext() {
        return this.tsvRecordIterator.hasNext();
    }

    @Override
    public GAVariant next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        GAVariant variant = new GAVariant();

        Map<String, List<String>> info = new HashMap<>();

        double exacFreq = (double) 0;

        int i = 0;
        for (String field : tsvRecordIterator.next()) {
            ExomiserTSVColumn column = ExomiserTSVColumn.values()[i++];

            switch (column) {
                case CHROM:
                    variant.setReferenceName(field);
                    break;
                case POS:
                    variant.setStart(Long.valueOf(field));
                    break;
                case REF:
                    variant.setReferenceBases(field);
                    break;
                case ALT:
                    variant.setAlternateBases(Arrays.asList(field.split(",")));
                    break;
                case QUAL:
                    info.put("QUAL", Collections.singletonList(field));
                    break;
                case FILTER:
                    info.put("FILTER", Collections.singletonList(field));
                    break;
                case EXOMISER_VARIANT_SCORE:
                    info.put("EXOMISER_VARIANT_SCORE", Collections.singletonList(field));
                    break;
                case EXOMISER_GENE_PHENO_SCORE:
                    info.put("EXOMISER_GENE_PHENO_SCORE", Collections.singletonList(field));
                    break;
                case EXOMISER_GENE_COMBINED_SCORE:
                    info.put("EXOMISER_GENE_COMBINED_SCORE", Collections.singletonList(field));
                    break;
                case EXOMISER_GENE_VARIANT_SCORE:
                    info.put("EXOMISER_GENE_VARIANT_SCORE", Collections.singletonList(field));
                    break;
                case EXOMISER_GENE:
                    info.put("GENE", Collections.singletonList(field));
                    break;
                case FUNCTIONAL_CLASS:
                    info.put("GENE_EFFECT", Collections.singletonList(field));
                    break;
                case EXAC_AFR_FREQ:
                case EXAC_AMR_FREQ:
                case EXAC_EAS_FREQ:
                case EXAC_FIN_FREQ:
                case EXAC_NFE_FREQ:
                case EXAC_SAS_FREQ:
                case EXAC_OTH_FREQ:
                    if (!".".equals(field)) {
                        exacFreq = Math.max(exacFreq, Double.parseDouble(field));
                    }
                    break;
                case GENOTYPE:
                case COVERAGE:
                case HGVS:
                case CADD:
                case POLYPHEN:
                case MUTATIONTASTER:
                case SIFT:
                case DBSNP_ID:
                case MAX_FREQUENCY:
                case DBSNP_FREQUENCY:
                case EVS_EA_FREQUENCY:
                case EVS_AA_FREQUENCY:
                default:
                    break;
            }
        }

        info.put("EXAC_AF", Collections.singletonList(String.valueOf(exacFreq)));

        variant.setEnd(variant.getStart() + variant.getReferenceBases().length());
        variant.setInfo(info);

        if (!this.hasNext()) {
            // Cleanup
            try {
                tsvParser.close();
            } catch (IOException e) {
                logger.error(String.format("Error when closing file %s", this.path), e);
            }
        }

        return variant;
    }

}
