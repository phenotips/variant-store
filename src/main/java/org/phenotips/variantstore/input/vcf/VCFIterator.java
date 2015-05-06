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
package org.phenotips.variantstore.input.vcf;

import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.GAVariantInfoFields;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Created by meatcar on 3/13/15.
 *
 * @version $Id$
 */
public class VCFIterator extends AbstractVariantIterator
{
    private final VCFFileReader reader;
    private final CloseableIterator<VariantContext> iterator;
    private Logger logger = Logger.getLogger(getClass());
    private Map<String, List<String>> filter;
    private VariantContext nextRow;

    /**
     * Create a new Variant Iterator for a VCF file.
     *
     * @param path   the path to the vcf
     * @param header vcf meta info
     */
    public VCFIterator(Path path, VariantHeader header) {
        this(path, null, header, null);
    }

    /**
     * Create a new Variant Iterator for a VCF file that has an index file.
     *
     * @param path   the vcf file
     * @param index  the index file
     * @param header the header
     */
    public VCFIterator(Path path, Path index, VariantHeader header) {
        this(path, index, header, null);
    }

    /**
     * Create a new Variant Iterator for a VCF file with a filter to skip any info field that matches the filter.
     *
     * @param path   the vcf file
     * @param header vcf meta info
     * @param filter map of info field names and the values to skip on
     */
    public VCFIterator(Path path, VariantHeader header, Map<String, List<String>> filter) {
        this(path, null, header, filter);
    }

    /**
     * Set a filter for the Info fields. Any VCF row with info fields that match this filter will be skipped.
     *
     * @param path   the vcf file
     * @param index  the index file
     * @param header vcf meta info
     * @param filter A Map of Info field -> List of values to exclude
     */
    public VCFIterator(Path path, Path index, VariantHeader header, Map<String, List<String>> filter) {
        super(path, header);

        this.filter = filter;
        if (index == null) {
            this.reader = new VCFFileReader(path.toFile(), false);
        } else {
            this.reader = new VCFFileReader(path.toFile(), index.toFile());
        }

        this.iterator = this.reader.iterator();
        this.nextRow = this.nextFiltered();
    }

    /**
     * Turn a list of htsjdk Alleles into a list of Strings/Strings.
     *
     * @param alleles a list of htsjdk Alleles
     * @return a list of String representations of each allele
     */
    private static List<String> stringifyAlleles(List<Allele> alleles) {
        List<String> alts = new ArrayList<>();
        for (Allele a : alleles) {
            alts.add(a.getDisplayString());
        }
        return alts;
    }

    @Override
    public boolean hasNext() {
        return nextRow != null;
    }

    @Override
    public GAVariant next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        GAVariant variant = new GAVariant();

        VariantContext context = nextRow;

        Map<String, List<String>> info = new HashMap<>();

        variant.setReferenceName(context.getChr());
        variant.setStart((long) context.getStart());
        variant.setEnd((long) context.getEnd());
        variant.setReferenceBases(context.getReference().getBaseString());

        // ALT
        List<String> alts = stringifyAlleles(context.getAlternateAlleles());
        variant.setAlternateBases(alts);

        // INFO
        info.put(GAVariantInfoFields.QUALITY, Collections.singletonList(String.valueOf(context.getPhredScaledQual())));
        info.put(GAVariantInfoFields.FILTER, new ArrayList<String>(context.getFilters()));

        String alleleFrequency = (String) context.getAttribute("AF");
        if (alleleFrequency != null) {
            // handling ExAC VCF file
            info.put(GAVariantInfoFields.EXAC_AF, Collections.singletonList(alleleFrequency));
        }

        variant.setInfo(info);

        this.nextRow = this.nextFiltered();
        if (!hasNext()) {
            iterator.close();
            reader.close();
        }

        return variant;
    }

    /**
     * Advance the iterator to the next filtered.
     */
    private VariantContext nextFiltered() {

        // no next
        if (!iterator.hasNext()) {
            return null;
        }
        // no filter, don't do extra work.
        if (this.filter == null) {
            return iterator.next();
        }

        while (iterator.hasNext()) {
            VariantContext ctx = iterator.next();
            CommonInfo contextInfo = ctx.getCommonInfo();

            // Skip any vcf row that matches the filter.
            boolean matched = false;
            for (Map.Entry<String, List<String>> filterEntry : filter.entrySet()) {

                String ctxInfoValue = String.valueOf(contextInfo.getAttribute(filterEntry.getKey()));
                if (filterEntry.getValue().contains(ctxInfoValue)) {
                    // the INFO field matched the filter, skip to discarding this element.
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return ctx;
            }
        }

        // no items found!
        return null;

    }
}
