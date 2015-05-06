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
package org.phenotips.variantstore.input.tsv;

import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.GAVariantInfoFields;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;

/**
 * Wrap around a CSV file flattened by vcfflatten, with exomiser results appened to the end of each line. Expose each
 * line as a ga4gh GAVariant object.
 *
 * @version $Id$
 */
public class ExomiserTSVIterator extends AbstractVariantIterator
{
    private static ExomiserTSVColumn[] columns = ExomiserTSVColumn.values();

    private Logger logger = Logger.getLogger(ExomiserTSVIterator.class);
    private CSVParser tsvParser;
    private Iterator<CSVRecord> tsvRecordIterator;

    /**
     * Create a new TSV iterator for files output by Exomiser.
     * @param path the path to the file
     * @param variantHeader the header with file meta-information
     */
    public ExomiserTSVIterator(Path path, VariantHeader variantHeader) {
        super(path, variantHeader);

        Reader reader = null;
        try {
            reader = new FileReader(this.path.toString());
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

        double exacFreq = 0;

        int i = 0;
        for (String field : tsvRecordIterator.next()) {

            addFieldToVariant(variant, field, i);
            addFieldToInfo(info, field, i);

            exacFreq = getMaxExacFreqFromField(exacFreq, field, i);

            i++;
        }

        if (exacFreq != 0) {
            info.put(GAVariantInfoFields.EXAC_AF, Collections.singletonList(String.valueOf(exacFreq)));
        }

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

    private void addFieldToVariant(GAVariant variant, String field, int i) {
        switch (columns[i]) {
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
            default:
        }
    }

    private void addFieldToInfo(Map<String, List<String>> info, String field, int i) {
        switch (columns[i]) {
            case QUAL:
                info.put(GAVariantInfoFields.QUALITY, Collections.singletonList(field));
                break;
            case FILTER:
                info.put(GAVariantInfoFields.FILTER, Collections.singletonList(field));
                break;
            case EXOMISER_VARIANT_SCORE:
                info.put(GAVariantInfoFields.EXOMISER_VARIANT_SCORE, Collections.singletonList(field));
                break;
            case EXOMISER_GENE_PHENO_SCORE:
                info.put(GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE, Collections.singletonList(field));
                break;
            case EXOMISER_GENE_COMBINED_SCORE:
                info.put(GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE, Collections.singletonList(field));
                break;
            case EXOMISER_GENE_VARIANT_SCORE:
                info.put(GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE, Collections.singletonList(field));
                break;
            case EXOMISER_GENE:
                info.put(GAVariantInfoFields.GENE, Collections.singletonList(field));
                break;
            case FUNCTIONAL_CLASS:
                info.put(GAVariantInfoFields.GENE_EFFECT, Collections.singletonList(field));
                break;
            default:
        }
    }

    private double getMaxExacFreqFromField(double exacFreq, String field, int i) {
        switch (columns[i]) {
            case EXAC_AFR_FREQ:
            case EXAC_AMR_FREQ:
            case EXAC_EAS_FREQ:
            case EXAC_FIN_FREQ:
            case EXAC_NFE_FREQ:
            case EXAC_SAS_FREQ:
            case EXAC_OTH_FREQ:
                if (!".".equals(field)) {
                    return Math.max(exacFreq, Double.parseDouble(field));
                }
                break;
            default:
        }
        return 0;
    }
}
