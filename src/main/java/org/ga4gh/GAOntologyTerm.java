/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.ga4gh;  
@SuppressWarnings("all")
/** An ontology term describing an attribute. (e.g. the phenotype attribute
'polydactyly' from HPO) */
@org.apache.avro.specific.AvroGenerated
public class GAOntologyTerm extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GAOntologyTerm\",\"namespace\":\"org.ga4gh\",\"doc\":\"An ontology term describing an attribute. (e.g. the phenotype attribute\\n'polydactyly' from HPO)\",\"fields\":[{\"name\":\"ontologySource\",\"type\":\"string\",\"doc\":\"The source of the onotology term.\\n  (e.g. `Ontology for Biomedical Investigation`)\"},{\"name\":\"id\",\"type\":\"string\",\"doc\":\"The ID defined by the external onotology source.\\n  (e.g. `http://purl.obolibrary.org/obo/OBI_0001271`)\"},{\"name\":\"name\",\"type\":[\"null\",\"string\"],\"doc\":\"The name of the onotology term. (e.g. `RNA-seq assay`)\",\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** The source of the onotology term.
  (e.g. `Ontology for Biomedical Investigation`) */
  @Deprecated public java.lang.CharSequence ontologySource;
  /** The ID defined by the external onotology source.
  (e.g. `http://purl.obolibrary.org/obo/OBI_0001271`) */
  @Deprecated public java.lang.CharSequence id;
  /** The name of the onotology term. (e.g. `RNA-seq assay`) */
  @Deprecated public java.lang.CharSequence name;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public GAOntologyTerm() {}

  /**
   * All-args constructor.
   */
  public GAOntologyTerm(java.lang.CharSequence ontologySource, java.lang.CharSequence id, java.lang.CharSequence name) {
    this.ontologySource = ontologySource;
    this.id = id;
    this.name = name;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return ontologySource;
    case 1: return id;
    case 2: return name;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: ontologySource = (java.lang.CharSequence)value$; break;
    case 1: id = (java.lang.CharSequence)value$; break;
    case 2: name = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'ontologySource' field.
   * The source of the onotology term.
  (e.g. `Ontology for Biomedical Investigation`)   */
  public java.lang.CharSequence getOntologySource() {
    return ontologySource;
  }

  /**
   * Sets the value of the 'ontologySource' field.
   * The source of the onotology term.
  (e.g. `Ontology for Biomedical Investigation`)   * @param value the value to set.
   */
  public void setOntologySource(java.lang.CharSequence value) {
    this.ontologySource = value;
  }

  /**
   * Gets the value of the 'id' field.
   * The ID defined by the external onotology source.
  (e.g. `http://purl.obolibrary.org/obo/OBI_0001271`)   */
  public java.lang.CharSequence getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * The ID defined by the external onotology source.
  (e.g. `http://purl.obolibrary.org/obo/OBI_0001271`)   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'name' field.
   * The name of the onotology term. (e.g. `RNA-seq assay`)   */
  public java.lang.CharSequence getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * The name of the onotology term. (e.g. `RNA-seq assay`)   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /** Creates a new GAOntologyTerm RecordBuilder */
  public static org.ga4gh.GAOntologyTerm.Builder newBuilder() {
    return new org.ga4gh.GAOntologyTerm.Builder();
  }
  
  /** Creates a new GAOntologyTerm RecordBuilder by copying an existing Builder */
  public static org.ga4gh.GAOntologyTerm.Builder newBuilder(org.ga4gh.GAOntologyTerm.Builder other) {
    return new org.ga4gh.GAOntologyTerm.Builder(other);
  }
  
  /** Creates a new GAOntologyTerm RecordBuilder by copying an existing GAOntologyTerm instance */
  public static org.ga4gh.GAOntologyTerm.Builder newBuilder(org.ga4gh.GAOntologyTerm other) {
    return new org.ga4gh.GAOntologyTerm.Builder(other);
  }
  
  /**
   * RecordBuilder for GAOntologyTerm instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GAOntologyTerm>
    implements org.apache.avro.data.RecordBuilder<GAOntologyTerm> {

    private java.lang.CharSequence ontologySource;
    private java.lang.CharSequence id;
    private java.lang.CharSequence name;

    /** Creates a new Builder */
    private Builder() {
      super(org.ga4gh.GAOntologyTerm.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.ga4gh.GAOntologyTerm.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.ontologySource)) {
        this.ontologySource = data().deepCopy(fields()[0].schema(), other.ontologySource);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.id)) {
        this.id = data().deepCopy(fields()[1].schema(), other.id);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
    }
    
    /** Creates a Builder by copying an existing GAOntologyTerm instance */
    private Builder(org.ga4gh.GAOntologyTerm other) {
            super(org.ga4gh.GAOntologyTerm.SCHEMA$);
      if (isValidValue(fields()[0], other.ontologySource)) {
        this.ontologySource = data().deepCopy(fields()[0].schema(), other.ontologySource);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.id)) {
        this.id = data().deepCopy(fields()[1].schema(), other.id);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
    }

    /** Gets the value of the 'ontologySource' field */
    public java.lang.CharSequence getOntologySource() {
      return ontologySource;
    }
    
    /** Sets the value of the 'ontologySource' field */
    public org.ga4gh.GAOntologyTerm.Builder setOntologySource(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.ontologySource = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'ontologySource' field has been set */
    public boolean hasOntologySource() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'ontologySource' field */
    public org.ga4gh.GAOntologyTerm.Builder clearOntologySource() {
      ontologySource = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'id' field */
    public java.lang.CharSequence getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public org.ga4gh.GAOntologyTerm.Builder setId(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.id = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'id' field */
    public org.ga4gh.GAOntologyTerm.Builder clearId() {
      id = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.CharSequence getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public org.ga4gh.GAOntologyTerm.Builder setName(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.name = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'name' field has been set */
    public boolean hasName() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'name' field */
    public org.ga4gh.GAOntologyTerm.Builder clearName() {
      name = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public GAOntologyTerm build() {
      try {
        GAOntologyTerm record = new GAOntologyTerm();
        record.ontologySource = fieldSetFlags()[0] ? this.ontologySource : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.id = fieldSetFlags()[1] ? this.id : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.name = fieldSetFlags()[2] ? this.name : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
