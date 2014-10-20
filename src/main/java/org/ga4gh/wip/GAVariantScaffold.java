/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.ga4gh.wip;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class GAVariantScaffold extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GAVariantScaffold\",\"namespace\":\"org.ga4gh.wip\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"alleles\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GAAllele\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"sequence\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GASegment\",\"doc\":\"*******************************************************************\",\"fields\":[{\"name\":\"variantId\",\"type\":\"string\"},{\"name\":\"start\",\"type\":\"int\"},{\"name\":\"end\",\"type\":\"int\"},{\"name\":\"side\",\"type\":{\"type\":\"enum\",\"name\":\"GAVariantSide\",\"symbols\":[\"PLUS\",\"MINUS\"]}},{\"name\":\"length\",\"type\":\"int\"}]}}}]}}},{\"name\":\"isForwards\",\"type\":{\"type\":\"array\",\"items\":\"boolean\"}},{\"name\":\"gapSizes\",\"type\":{\"type\":\"array\",\"items\":\"int\"},\"default\":[]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence id;
  @Deprecated public java.util.List<org.ga4gh.wip.GAAllele> alleles;
  @Deprecated public java.util.List<java.lang.Boolean> isForwards;
  @Deprecated public java.util.List<java.lang.Integer> gapSizes;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public GAVariantScaffold() {}

  /**
   * All-args constructor.
   */
  public GAVariantScaffold(java.lang.CharSequence id, java.util.List<org.ga4gh.wip.GAAllele> alleles, java.util.List<java.lang.Boolean> isForwards, java.util.List<java.lang.Integer> gapSizes) {
    this.id = id;
    this.alleles = alleles;
    this.isForwards = isForwards;
    this.gapSizes = gapSizes;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return alleles;
    case 2: return isForwards;
    case 3: return gapSizes;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.CharSequence)value$; break;
    case 1: alleles = (java.util.List<org.ga4gh.wip.GAAllele>)value$; break;
    case 2: isForwards = (java.util.List<java.lang.Boolean>)value$; break;
    case 3: gapSizes = (java.util.List<java.lang.Integer>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   */
  public java.lang.CharSequence getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'alleles' field.
   */
  public java.util.List<org.ga4gh.wip.GAAllele> getAlleles() {
    return alleles;
  }

  /**
   * Sets the value of the 'alleles' field.
   * @param value the value to set.
   */
  public void setAlleles(java.util.List<org.ga4gh.wip.GAAllele> value) {
    this.alleles = value;
  }

  /**
   * Gets the value of the 'isForwards' field.
   */
  public java.util.List<java.lang.Boolean> getIsForwards() {
    return isForwards;
  }

  /**
   * Sets the value of the 'isForwards' field.
   * @param value the value to set.
   */
  public void setIsForwards(java.util.List<java.lang.Boolean> value) {
    this.isForwards = value;
  }

  /**
   * Gets the value of the 'gapSizes' field.
   */
  public java.util.List<java.lang.Integer> getGapSizes() {
    return gapSizes;
  }

  /**
   * Sets the value of the 'gapSizes' field.
   * @param value the value to set.
   */
  public void setGapSizes(java.util.List<java.lang.Integer> value) {
    this.gapSizes = value;
  }

  /** Creates a new GAVariantScaffold RecordBuilder */
  public static org.ga4gh.wip.GAVariantScaffold.Builder newBuilder() {
    return new org.ga4gh.wip.GAVariantScaffold.Builder();
  }
  
  /** Creates a new GAVariantScaffold RecordBuilder by copying an existing Builder */
  public static org.ga4gh.wip.GAVariantScaffold.Builder newBuilder(org.ga4gh.wip.GAVariantScaffold.Builder other) {
    return new org.ga4gh.wip.GAVariantScaffold.Builder(other);
  }
  
  /** Creates a new GAVariantScaffold RecordBuilder by copying an existing GAVariantScaffold instance */
  public static org.ga4gh.wip.GAVariantScaffold.Builder newBuilder(org.ga4gh.wip.GAVariantScaffold other) {
    return new org.ga4gh.wip.GAVariantScaffold.Builder(other);
  }
  
  /**
   * RecordBuilder for GAVariantScaffold instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GAVariantScaffold>
    implements org.apache.avro.data.RecordBuilder<GAVariantScaffold> {

    private java.lang.CharSequence id;
    private java.util.List<org.ga4gh.wip.GAAllele> alleles;
    private java.util.List<java.lang.Boolean> isForwards;
    private java.util.List<java.lang.Integer> gapSizes;

    /** Creates a new Builder */
    private Builder() {
      super(org.ga4gh.wip.GAVariantScaffold.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.ga4gh.wip.GAVariantScaffold.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.alleles)) {
        this.alleles = data().deepCopy(fields()[1].schema(), other.alleles);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.isForwards)) {
        this.isForwards = data().deepCopy(fields()[2].schema(), other.isForwards);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.gapSizes)) {
        this.gapSizes = data().deepCopy(fields()[3].schema(), other.gapSizes);
        fieldSetFlags()[3] = true;
      }
    }
    
    /** Creates a Builder by copying an existing GAVariantScaffold instance */
    private Builder(org.ga4gh.wip.GAVariantScaffold other) {
            super(org.ga4gh.wip.GAVariantScaffold.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.alleles)) {
        this.alleles = data().deepCopy(fields()[1].schema(), other.alleles);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.isForwards)) {
        this.isForwards = data().deepCopy(fields()[2].schema(), other.isForwards);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.gapSizes)) {
        this.gapSizes = data().deepCopy(fields()[3].schema(), other.gapSizes);
        fieldSetFlags()[3] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.CharSequence getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder setId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'id' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'alleles' field */
    public java.util.List<org.ga4gh.wip.GAAllele> getAlleles() {
      return alleles;
    }
    
    /** Sets the value of the 'alleles' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder setAlleles(java.util.List<org.ga4gh.wip.GAAllele> value) {
      validate(fields()[1], value);
      this.alleles = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'alleles' field has been set */
    public boolean hasAlleles() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'alleles' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder clearAlleles() {
      alleles = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'isForwards' field */
    public java.util.List<java.lang.Boolean> getIsForwards() {
      return isForwards;
    }
    
    /** Sets the value of the 'isForwards' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder setIsForwards(java.util.List<java.lang.Boolean> value) {
      validate(fields()[2], value);
      this.isForwards = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'isForwards' field has been set */
    public boolean hasIsForwards() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'isForwards' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder clearIsForwards() {
      isForwards = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'gapSizes' field */
    public java.util.List<java.lang.Integer> getGapSizes() {
      return gapSizes;
    }
    
    /** Sets the value of the 'gapSizes' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder setGapSizes(java.util.List<java.lang.Integer> value) {
      validate(fields()[3], value);
      this.gapSizes = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'gapSizes' field has been set */
    public boolean hasGapSizes() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'gapSizes' field */
    public org.ga4gh.wip.GAVariantScaffold.Builder clearGapSizes() {
      gapSizes = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    public GAVariantScaffold build() {
      try {
        GAVariantScaffold record = new GAVariantScaffold();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.alleles = fieldSetFlags()[1] ? this.alleles : (java.util.List<org.ga4gh.wip.GAAllele>) defaultValue(fields()[1]);
        record.isForwards = fieldSetFlags()[2] ? this.isForwards : (java.util.List<java.lang.Boolean>) defaultValue(fields()[2]);
        record.gapSizes = fieldSetFlags()[3] ? this.gapSizes : (java.util.List<java.lang.Integer>) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
