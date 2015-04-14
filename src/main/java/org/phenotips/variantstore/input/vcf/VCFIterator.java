package org.phenotips.variantstore.input.vcf;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import java.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.VariantHeader;

/**
 * Created by meatcar on 3/13/15.
 */
public class VCFIterator extends AbstractVariantIterator {
    private Logger logger = Logger.getLogger(getClass());

    private final VCFFileReader reader;
    private final CloseableIterator<VariantContext> iterator;
    private Map<String, List<String>> filter;
    private VariantContext nextRow = null;

    public VCFIterator(Path path, VariantHeader header) {
        this(path, null, header, null);
    }
    public VCFIterator(Path path, Path index, VariantHeader header) {
        this(path, index, header, null);
    }
    public VCFIterator(Path path, VariantHeader header, Map<String, List<String>> filter) {
        this(path, null, header, filter);
    }

    /**
     * Set a filter for the Info fields. Any VCF row with info fields that match this filter will be skipped.
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


    public boolean hasNext() {
        return nextRow != null;
    }

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
        info.put("QUAL", Collections.singletonList(String.valueOf(context.getPhredScaledQual())));
        info.put("FILTER", new ArrayList<String>(context.getFilters()));

        if (context.hasAttribute("AF")) {
            // handling ExAC VCF file
            info.put("AF", Collections.singletonList((String) context.getAttribute("AF")));
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
     * Advance the iterator to the next filtered
     */
    private VariantContext nextFiltered() {

        // no next
        if (!iterator.hasNext()) {
            return null;
        }
        // no filter, don't do extra work.
        if (this.filter == null ) {
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


    /**
     * Turn a list of htsjdk Alleles into a list of Strings/Strings
     * @param alleles a list of htsjdk Alleles
     * @return a list of String representations of each allele
     */
    public static List<String> stringifyAlleles(List<Allele> alleles) {
        List<String> alts = new ArrayList<>();
        for (Allele a : alleles) {
            alts.add(a.getDisplayString());
        }
        return alts;
    }
}
