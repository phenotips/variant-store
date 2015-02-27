package org.phenotips.variantstore.input.csv;

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
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.input.AbstractVariantIterator;

/**
 * Wrap around a CSV file flattened by vcfflatten, with exomiser results appened to the end of each line. Expose each
 * line as a ga4gh GAVariant object.
 */
public class CSVIterator extends AbstractVariantIterator {
    private Logger logger = Logger.getLogger(CSVIterator.class);
    private Reader reader;
    private CSVParser csvParser;
    private Iterator<CSVRecord> csvRecordIterator;

    public CSVIterator(Path path, VariantHeader variantHeader) throws InputException {
        super(path, variantHeader);

        try {
            this.reader = new FileReader(this.path.toString());
        } catch (FileNotFoundException e) {
            throw new InputException(String.format("Error when opening file %s", this.path), e);
        }

        try {
            this.csvParser = CSVFormat.DEFAULT.parse(this.reader);
        } catch (IOException e) {
            throw new InputException(String.format("Error when opening file %s", this.path), e);
        }

        this.csvRecordIterator = csvParser.iterator();
    }

    @Override
    public boolean hasNext() {
        return this.csvRecordIterator.hasNext();
    }

    @Override
    public GAVariant next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        GAVariant variant = new GAVariant();

        Map<String, List<String>> info = new HashMap<>();

        int i = 0;
        for (String field : csvRecordIterator.next()) {
            CSVColumn column = CSVColumn.values()[i++];

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
                    info.put("QUAL", Arrays.asList(field));
                    break;
                case FILTER:
                    info.put("FILTER", Arrays.asList(field));
                    break;
                case EXOMISER_VARIANT_SCORE:
                    info.put("EXOMISER_VARIANT_SCORE", Arrays.asList(field));
                    break;
                case EXOMISER_GENE_PHENO_SCORE:
                    info.put("EXOMISER_GENE_PHENO_SCORE", Arrays.asList(field));
                    break;
                case EXOMISER_GENE:
                    info.put("EXOMISER_GENE", Arrays.asList(field));
                    break;
                case EXOMISER_EFFECT:
                    info.put("EXOMISER_EFFECT", Arrays.asList(field));
                    break;
                case EXOMISER_GENE_COMBINED_SCORE:
                    info.put("EXOMISER_GENE_COMBINED_SCORE", Arrays.asList(field));
                    break;
                case EXOMISER_GENE_VARIANT_SCORE:
                    info.put("EXOMISER_GENE_VARIANT_SCORE", Arrays.asList(field));
                    break;
                case VAR_ID:
                case AC:
                case AN:
                case CGA_BF:
                case CGA_BNDG:
                case CGA_BNDGO:
                case CGA_FI:
                case CGA_MEDEL:
                case CGA_MIRB:
                case CGA_PFAM:
                case CGA_RPT:
                case CGA_SDO:
                case CGA_WINEND:
                case CGA_XR:
                case CIPOS:
                case ENDD:
                case MATEID:
                case MEINFO:
                case NS:
                case SVLEN:
                case SVTYPE:
                case IMPRECISE:
                default:
                    break;
            }
        }

        variant.setEnd(variant.getStart() + variant.getReferenceBases().length());
        variant.setInfo(info);

        if (!this.hasNext()) {
            // Cleanup
            try {
                csvParser.close();
            } catch (IOException e) {
                logger.error(String.format("Error when closing file %s", this.path), e);
            }
        }

        return variant;
    }

}
