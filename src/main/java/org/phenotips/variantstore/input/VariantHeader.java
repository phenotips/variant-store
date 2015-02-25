package org.phenotips.variantstore.input;

/**
 * The metadata associated with a collection of variants.
 */
public class VariantHeader {
    private String individualId;
    private boolean isPublic = false;

    public VariantHeader(String individualId, boolean isPublic) {
        this.individualId = individualId;
        this.isPublic = isPublic;
    }

    public String getIndividualId() {
        return individualId;
    }

    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
