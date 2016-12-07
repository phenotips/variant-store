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
package org.phenotips.variantstore.input.tsv;

import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse variants from TSV files, such as those returned by [Exomiser](http://www.sanger.ac.uk/science/tools/exomiser).
 * Expose each line as a ga4gh GAVariant object.
 *
 * @version $Id$
 */
public abstract class AbstractTSVIterator extends AbstractVariantIterator
{
    private Logger logger = LoggerFactory.getLogger(AbstractTSVIterator.class);
    private CSVParser tsvParser;
    private Iterator<CSVRecord> tsvRecordIterator;
    private List<String> columns;

    /**
     * Create a new TSV iterator for files, such as those outputted by Exomiser.
     *
     * @param path          the path to the file
     * @param variantHeader the header with file meta-information
     */
    public AbstractTSVIterator(Path path, VariantHeader variantHeader) {
        super(path, variantHeader);

        Reader reader = null;
        try {
            reader = new FileReader(this.path.toString());
            this.tsvParser = CSVFormat.TDF.parse(reader);
        } catch (IOException e) {
            logger.error(String.format("Error when opening file %s, this should NOT be happening", this.path), e);
        }

        this.tsvRecordIterator = tsvParser.iterator();
        // Read column names
        if (this.hasNext()) {
            this.columns = new ArrayList<String>();
            for (String field : tsvRecordIterator.next()) {
                // Remove leading hashes
                columns.add(field.replaceAll("^#+", ""));
            }
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
        GACall call = new GACall();

        variant.setCalls(Collections.singletonList(call));

        initializeVariant(variant);
        int i = 0;
        for (String field : tsvRecordIterator.next()) {
            String column = columns.get(i);
            processField(variant, call, column, field);
            i++;
        }
        finalizeVariant(variant);

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

    protected void initializeVariant(GAVariant variant) {

    }

    protected void finalizeVariant(GAVariant variant) {
        variant.setEnd(variant.getStart() + variant.getReferenceBases().length() - 1);
    }

    protected void processField(GAVariant variant, GACall call, String column, String field) {
        switch (column) {
            case "CHROM":
                variant.setReferenceName(field);
                break;
            case "POS":
                // GA4GH uses 0-based indexing, unlike TSV's 1-based.
                variant.setStart(Long.valueOf(field) - 1);
                break;
            case "REF":
                variant.setReferenceBases(field);
                break;
            case "ALT":
                variant.setAlternateBases(Arrays.asList(field.split(",")));
                break;
            case "GENOTYPE":
                String splitter = "/";
                String phasedSplitter = "|";
                if (!field.contains(splitter)) {
                    if (field.contains(phasedSplitter)) {
                        // phased
                        splitter = phasedSplitter;
                    } else {
                        //TODO: SHOULD NOT BE DOING THIS.
                        call.setGenotype(Arrays.asList(0, 0));
                        break;
                    }
                }
                String[] split = field.split(splitter);

                if (".".equals(split[0])) {
                    //TODO: SHOULD NOT BE DOING THIS.
                    call.setGenotype(Arrays.asList(0, 0));
                    break;
                }

                call.setGenotype(Arrays.asList(Integer.valueOf(split[0]), Integer.valueOf(split[1])));
                break;
            case "QUAL":
                VariantUtils.addInfo(call, GACallInfoFields.QUALITY, field);
                break;
            case "FILTER":
                VariantUtils.addInfo(call, GACallInfoFields.FILTER, field);
                break;
            default:
        }
    }
}
