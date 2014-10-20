/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.ga4gh;  
@SuppressWarnings("all")
/** An individual (or subject) typically corresponds to an individual
human or other organism. */
@org.apache.avro.specific.AvroGenerated
public class GAIndividual extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GAIndividual\",\"namespace\":\"org.ga4gh\",\"doc\":\"An individual (or subject) typically corresponds to an individual\\nhuman or other organism.\",\"fields\":[{\"name\":\"id\",\"type\":\"string\",\"doc\":\"The individual UUID. This is globally unique.\"},{\"name\":\"groupIds\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"doc\":\"The IDs of the individual groups this individual belongs to.\",\"default\":[]},{\"name\":\"name\",\"type\":[\"null\",\"string\"],\"doc\":\"The name of the individual.\",\"default\":null},{\"name\":\"description\",\"type\":[\"null\",\"string\"],\"doc\":\"A description of the individual.\",\"default\":null},{\"name\":\"created\",\"type\":[\"null\",\"long\"],\"doc\":\"The time at which this individual was created in milliseconds from the epoch.\",\"default\":null},{\"name\":\"updated\",\"type\":[\"null\",\"long\"],\"doc\":\"The time at which this individual was last updated in milliseconds\\n  from the epoch.\",\"default\":null},{\"name\":\"species\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"GAOntologyTerm\",\"doc\":\"An ontology term describing an attribute. (e.g. the phenotype attribute\\n'polydactyly' from HPO)\",\"fields\":[{\"name\":\"ontologySource\",\"type\":\"string\",\"doc\":\"The source of the onotology term.\\n  (e.g. `Ontology for Biomedical Investigation`)\"},{\"name\":\"id\",\"type\":\"string\",\"doc\":\"The ID defined by the external onotology source.\\n  (e.g. `http://purl.obolibrary.org/obo/OBI_0001271`)\"},{\"name\":\"name\",\"type\":[\"null\",\"string\"],\"doc\":\"The name of the onotology term. (e.g. `RNA-seq assay`)\",\"default\":null}]}],\"doc\":\"The species of this individual. Using\\n  [NCBI taxonomy](http://www.ncbi.nlm.nih.gov/taxonomy) is recommended.\\n  TODO: Is this the right representation?\",\"default\":null},{\"name\":\"sex\",\"type\":[\"null\",{\"type\":\"enum\",\"name\":\"GASex\",\"doc\":\"An enum that represents biological sex.\\nTODO: This is moving to common in https://github.com/ga4gh/schemas/pull/138\\nand should be removed from this file.\",\"symbols\":[\"FEMALE\",\"MALE\",\"MIXED\",\"BISEX\",\"ASEX\"]}],\"doc\":\"The biological sex of this individual. Use `null` when unknown.\",\"default\":null},{\"name\":\"developmentalStage\",\"type\":[\"null\",\"GAOntologyTerm\"],\"doc\":\"The developmental stage of this individual. Using Uberon is recommended.\\n  TODO: Add link to uberon\",\"default\":null},{\"name\":\"dateOfBirth\",\"type\":[\"null\",\"long\"],\"doc\":\"The date of birth of this individual in milliseconds from the epoch.\\n  This field may be approximate.\",\"default\":null},{\"name\":\"diseases\",\"type\":{\"type\":\"array\",\"items\":\"GAOntologyTerm\"},\"doc\":\"Diseases with which the individual has been diagnosed.\\n  TODO: Is this the right representation?\",\"default\":[]},{\"name\":\"phenotypes\",\"type\":{\"type\":\"array\",\"items\":\"GAOntologyTerm\"},\"doc\":\"Phenotypes for this individual.\\n  TODO: Is this the right representation?\",\"default\":[]},{\"name\":\"stagingSystem\",\"type\":[\"null\",\"string\"],\"doc\":\"Disease area specific classification (e.g. classification of cancer samples\\n  such as Dukes)\",\"default\":null},{\"name\":\"clinicalTreatment\",\"type\":[\"null\",\"string\"],\"doc\":\"A description of the clinical treatment used for this individual.\",\"default\":null},{\"name\":\"strain\",\"type\":[\"null\",\"string\"],\"doc\":\"The strain of this individual, for non-humans.\",\"default\":null},{\"name\":\"info\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"array\",\"items\":\"string\"}},\"doc\":\"A map of additional individual information.\",\"default\":{}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** The individual UUID. This is globally unique. */
  @Deprecated public java.lang.CharSequence id;
  /** The IDs of the individual groups this individual belongs to. */
  @Deprecated public java.util.List<java.lang.CharSequence> groupIds;
  /** The name of the individual. */
  @Deprecated public java.lang.CharSequence name;
  /** A description of the individual. */
  @Deprecated public java.lang.CharSequence description;
  /** The time at which this individual was created in milliseconds from the epoch. */
  @Deprecated public java.lang.Long created;
  /** The time at which this individual was last updated in milliseconds
  from the epoch. */
  @Deprecated public java.lang.Long updated;
  /** The species of this individual. Using
  [NCBI taxonomy](http://www.ncbi.nlm.nih.gov/taxonomy) is recommended.
  TODO: Is this the right representation? */
  @Deprecated public org.ga4gh.GAOntologyTerm species;
  /** The biological sex of this individual. Use `null` when unknown. */
  @Deprecated public org.ga4gh.GASex sex;
  /** The developmental stage of this individual. Using Uberon is recommended.
  TODO: Add link to uberon */
  @Deprecated public org.ga4gh.GAOntologyTerm developmentalStage;
  /** The date of birth of this individual in milliseconds from the epoch.
  This field may be approximate. */
  @Deprecated public java.lang.Long dateOfBirth;
  /** Diseases with which the individual has been diagnosed.
  TODO: Is this the right representation? */
  @Deprecated public java.util.List<org.ga4gh.GAOntologyTerm> diseases;
  /** Phenotypes for this individual.
  TODO: Is this the right representation? */
  @Deprecated public java.util.List<org.ga4gh.GAOntologyTerm> phenotypes;
  /** Disease area specific classification (e.g. classification of cancer samples
  such as Dukes) */
  @Deprecated public java.lang.CharSequence stagingSystem;
  /** A description of the clinical treatment used for this individual. */
  @Deprecated public java.lang.CharSequence clinicalTreatment;
  /** The strain of this individual, for non-humans. */
  @Deprecated public java.lang.CharSequence strain;
  /** A map of additional individual information. */
  @Deprecated public java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> info;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public GAIndividual() {}

  /**
   * All-args constructor.
   */
  public GAIndividual(java.lang.CharSequence id, java.util.List<java.lang.CharSequence> groupIds, java.lang.CharSequence name, java.lang.CharSequence description, java.lang.Long created, java.lang.Long updated, org.ga4gh.GAOntologyTerm species, org.ga4gh.GASex sex, org.ga4gh.GAOntologyTerm developmentalStage, java.lang.Long dateOfBirth, java.util.List<org.ga4gh.GAOntologyTerm> diseases, java.util.List<org.ga4gh.GAOntologyTerm> phenotypes, java.lang.CharSequence stagingSystem, java.lang.CharSequence clinicalTreatment, java.lang.CharSequence strain, java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> info) {
    this.id = id;
    this.groupIds = groupIds;
    this.name = name;
    this.description = description;
    this.created = created;
    this.updated = updated;
    this.species = species;
    this.sex = sex;
    this.developmentalStage = developmentalStage;
    this.dateOfBirth = dateOfBirth;
    this.diseases = diseases;
    this.phenotypes = phenotypes;
    this.stagingSystem = stagingSystem;
    this.clinicalTreatment = clinicalTreatment;
    this.strain = strain;
    this.info = info;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return groupIds;
    case 2: return name;
    case 3: return description;
    case 4: return created;
    case 5: return updated;
    case 6: return species;
    case 7: return sex;
    case 8: return developmentalStage;
    case 9: return dateOfBirth;
    case 10: return diseases;
    case 11: return phenotypes;
    case 12: return stagingSystem;
    case 13: return clinicalTreatment;
    case 14: return strain;
    case 15: return info;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.CharSequence)value$; break;
    case 1: groupIds = (java.util.List<java.lang.CharSequence>)value$; break;
    case 2: name = (java.lang.CharSequence)value$; break;
    case 3: description = (java.lang.CharSequence)value$; break;
    case 4: created = (java.lang.Long)value$; break;
    case 5: updated = (java.lang.Long)value$; break;
    case 6: species = (org.ga4gh.GAOntologyTerm)value$; break;
    case 7: sex = (org.ga4gh.GASex)value$; break;
    case 8: developmentalStage = (org.ga4gh.GAOntologyTerm)value$; break;
    case 9: dateOfBirth = (java.lang.Long)value$; break;
    case 10: diseases = (java.util.List<org.ga4gh.GAOntologyTerm>)value$; break;
    case 11: phenotypes = (java.util.List<org.ga4gh.GAOntologyTerm>)value$; break;
    case 12: stagingSystem = (java.lang.CharSequence)value$; break;
    case 13: clinicalTreatment = (java.lang.CharSequence)value$; break;
    case 14: strain = (java.lang.CharSequence)value$; break;
    case 15: info = (java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * The individual UUID. This is globally unique.   */
  public java.lang.CharSequence getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * The individual UUID. This is globally unique.   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'groupIds' field.
   * The IDs of the individual groups this individual belongs to.   */
  public java.util.List<java.lang.CharSequence> getGroupIds() {
    return groupIds;
  }

  /**
   * Sets the value of the 'groupIds' field.
   * The IDs of the individual groups this individual belongs to.   * @param value the value to set.
   */
  public void setGroupIds(java.util.List<java.lang.CharSequence> value) {
    this.groupIds = value;
  }

  /**
   * Gets the value of the 'name' field.
   * The name of the individual.   */
  public java.lang.CharSequence getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * The name of the individual.   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'description' field.
   * A description of the individual.   */
  public java.lang.CharSequence getDescription() {
    return description;
  }

  /**
   * Sets the value of the 'description' field.
   * A description of the individual.   * @param value the value to set.
   */
  public void setDescription(java.lang.CharSequence value) {
    this.description = value;
  }

  /**
   * Gets the value of the 'created' field.
   * The time at which this individual was created in milliseconds from the epoch.   */
  public java.lang.Long getCreated() {
    return created;
  }

  /**
   * Sets the value of the 'created' field.
   * The time at which this individual was created in milliseconds from the epoch.   * @param value the value to set.
   */
  public void setCreated(java.lang.Long value) {
    this.created = value;
  }

  /**
   * Gets the value of the 'updated' field.
   * The time at which this individual was last updated in milliseconds
  from the epoch.   */
  public java.lang.Long getUpdated() {
    return updated;
  }

  /**
   * Sets the value of the 'updated' field.
   * The time at which this individual was last updated in milliseconds
  from the epoch.   * @param value the value to set.
   */
  public void setUpdated(java.lang.Long value) {
    this.updated = value;
  }

  /**
   * Gets the value of the 'species' field.
   * The species of this individual. Using
  [NCBI taxonomy](http://www.ncbi.nlm.nih.gov/taxonomy) is recommended.
  TODO: Is this the right representation?   */
  public org.ga4gh.GAOntologyTerm getSpecies() {
    return species;
  }

  /**
   * Sets the value of the 'species' field.
   * The species of this individual. Using
  [NCBI taxonomy](http://www.ncbi.nlm.nih.gov/taxonomy) is recommended.
  TODO: Is this the right representation?   * @param value the value to set.
   */
  public void setSpecies(org.ga4gh.GAOntologyTerm value) {
    this.species = value;
  }

  /**
   * Gets the value of the 'sex' field.
   * The biological sex of this individual. Use `null` when unknown.   */
  public org.ga4gh.GASex getSex() {
    return sex;
  }

  /**
   * Sets the value of the 'sex' field.
   * The biological sex of this individual. Use `null` when unknown.   * @param value the value to set.
   */
  public void setSex(org.ga4gh.GASex value) {
    this.sex = value;
  }

  /**
   * Gets the value of the 'developmentalStage' field.
   * The developmental stage of this individual. Using Uberon is recommended.
  TODO: Add link to uberon   */
  public org.ga4gh.GAOntologyTerm getDevelopmentalStage() {
    return developmentalStage;
  }

  /**
   * Sets the value of the 'developmentalStage' field.
   * The developmental stage of this individual. Using Uberon is recommended.
  TODO: Add link to uberon   * @param value the value to set.
   */
  public void setDevelopmentalStage(org.ga4gh.GAOntologyTerm value) {
    this.developmentalStage = value;
  }

  /**
   * Gets the value of the 'dateOfBirth' field.
   * The date of birth of this individual in milliseconds from the epoch.
  This field may be approximate.   */
  public java.lang.Long getDateOfBirth() {
    return dateOfBirth;
  }

  /**
   * Sets the value of the 'dateOfBirth' field.
   * The date of birth of this individual in milliseconds from the epoch.
  This field may be approximate.   * @param value the value to set.
   */
  public void setDateOfBirth(java.lang.Long value) {
    this.dateOfBirth = value;
  }

  /**
   * Gets the value of the 'diseases' field.
   * Diseases with which the individual has been diagnosed.
  TODO: Is this the right representation?   */
  public java.util.List<org.ga4gh.GAOntologyTerm> getDiseases() {
    return diseases;
  }

  /**
   * Sets the value of the 'diseases' field.
   * Diseases with which the individual has been diagnosed.
  TODO: Is this the right representation?   * @param value the value to set.
   */
  public void setDiseases(java.util.List<org.ga4gh.GAOntologyTerm> value) {
    this.diseases = value;
  }

  /**
   * Gets the value of the 'phenotypes' field.
   * Phenotypes for this individual.
  TODO: Is this the right representation?   */
  public java.util.List<org.ga4gh.GAOntologyTerm> getPhenotypes() {
    return phenotypes;
  }

  /**
   * Sets the value of the 'phenotypes' field.
   * Phenotypes for this individual.
  TODO: Is this the right representation?   * @param value the value to set.
   */
  public void setPhenotypes(java.util.List<org.ga4gh.GAOntologyTerm> value) {
    this.phenotypes = value;
  }

  /**
   * Gets the value of the 'stagingSystem' field.
   * Disease area specific classification (e.g. classification of cancer samples
  such as Dukes)   */
  public java.lang.CharSequence getStagingSystem() {
    return stagingSystem;
  }

  /**
   * Sets the value of the 'stagingSystem' field.
   * Disease area specific classification (e.g. classification of cancer samples
  such as Dukes)   * @param value the value to set.
   */
  public void setStagingSystem(java.lang.CharSequence value) {
    this.stagingSystem = value;
  }

  /**
   * Gets the value of the 'clinicalTreatment' field.
   * A description of the clinical treatment used for this individual.   */
  public java.lang.CharSequence getClinicalTreatment() {
    return clinicalTreatment;
  }

  /**
   * Sets the value of the 'clinicalTreatment' field.
   * A description of the clinical treatment used for this individual.   * @param value the value to set.
   */
  public void setClinicalTreatment(java.lang.CharSequence value) {
    this.clinicalTreatment = value;
  }

  /**
   * Gets the value of the 'strain' field.
   * The strain of this individual, for non-humans.   */
  public java.lang.CharSequence getStrain() {
    return strain;
  }

  /**
   * Sets the value of the 'strain' field.
   * The strain of this individual, for non-humans.   * @param value the value to set.
   */
  public void setStrain(java.lang.CharSequence value) {
    this.strain = value;
  }

  /**
   * Gets the value of the 'info' field.
   * A map of additional individual information.   */
  public java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> getInfo() {
    return info;
  }

  /**
   * Sets the value of the 'info' field.
   * A map of additional individual information.   * @param value the value to set.
   */
  public void setInfo(java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> value) {
    this.info = value;
  }

  /** Creates a new GAIndividual RecordBuilder */
  public static org.ga4gh.GAIndividual.Builder newBuilder() {
    return new org.ga4gh.GAIndividual.Builder();
  }
  
  /** Creates a new GAIndividual RecordBuilder by copying an existing Builder */
  public static org.ga4gh.GAIndividual.Builder newBuilder(org.ga4gh.GAIndividual.Builder other) {
    return new org.ga4gh.GAIndividual.Builder(other);
  }
  
  /** Creates a new GAIndividual RecordBuilder by copying an existing GAIndividual instance */
  public static org.ga4gh.GAIndividual.Builder newBuilder(org.ga4gh.GAIndividual other) {
    return new org.ga4gh.GAIndividual.Builder(other);
  }
  
  /**
   * RecordBuilder for GAIndividual instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GAIndividual>
    implements org.apache.avro.data.RecordBuilder<GAIndividual> {

    private java.lang.CharSequence id;
    private java.util.List<java.lang.CharSequence> groupIds;
    private java.lang.CharSequence name;
    private java.lang.CharSequence description;
    private java.lang.Long created;
    private java.lang.Long updated;
    private org.ga4gh.GAOntologyTerm species;
    private org.ga4gh.GASex sex;
    private org.ga4gh.GAOntologyTerm developmentalStage;
    private java.lang.Long dateOfBirth;
    private java.util.List<org.ga4gh.GAOntologyTerm> diseases;
    private java.util.List<org.ga4gh.GAOntologyTerm> phenotypes;
    private java.lang.CharSequence stagingSystem;
    private java.lang.CharSequence clinicalTreatment;
    private java.lang.CharSequence strain;
    private java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> info;

    /** Creates a new Builder */
    private Builder() {
      super(org.ga4gh.GAIndividual.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.ga4gh.GAIndividual.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.groupIds)) {
        this.groupIds = data().deepCopy(fields()[1].schema(), other.groupIds);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.description)) {
        this.description = data().deepCopy(fields()[3].schema(), other.description);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.created)) {
        this.created = data().deepCopy(fields()[4].schema(), other.created);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.updated)) {
        this.updated = data().deepCopy(fields()[5].schema(), other.updated);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.species)) {
        this.species = data().deepCopy(fields()[6].schema(), other.species);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.sex)) {
        this.sex = data().deepCopy(fields()[7].schema(), other.sex);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.developmentalStage)) {
        this.developmentalStage = data().deepCopy(fields()[8].schema(), other.developmentalStage);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.dateOfBirth)) {
        this.dateOfBirth = data().deepCopy(fields()[9].schema(), other.dateOfBirth);
        fieldSetFlags()[9] = true;
      }
      if (isValidValue(fields()[10], other.diseases)) {
        this.diseases = data().deepCopy(fields()[10].schema(), other.diseases);
        fieldSetFlags()[10] = true;
      }
      if (isValidValue(fields()[11], other.phenotypes)) {
        this.phenotypes = data().deepCopy(fields()[11].schema(), other.phenotypes);
        fieldSetFlags()[11] = true;
      }
      if (isValidValue(fields()[12], other.stagingSystem)) {
        this.stagingSystem = data().deepCopy(fields()[12].schema(), other.stagingSystem);
        fieldSetFlags()[12] = true;
      }
      if (isValidValue(fields()[13], other.clinicalTreatment)) {
        this.clinicalTreatment = data().deepCopy(fields()[13].schema(), other.clinicalTreatment);
        fieldSetFlags()[13] = true;
      }
      if (isValidValue(fields()[14], other.strain)) {
        this.strain = data().deepCopy(fields()[14].schema(), other.strain);
        fieldSetFlags()[14] = true;
      }
      if (isValidValue(fields()[15], other.info)) {
        this.info = data().deepCopy(fields()[15].schema(), other.info);
        fieldSetFlags()[15] = true;
      }
    }
    
    /** Creates a Builder by copying an existing GAIndividual instance */
    private Builder(org.ga4gh.GAIndividual other) {
            super(org.ga4gh.GAIndividual.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.groupIds)) {
        this.groupIds = data().deepCopy(fields()[1].schema(), other.groupIds);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.description)) {
        this.description = data().deepCopy(fields()[3].schema(), other.description);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.created)) {
        this.created = data().deepCopy(fields()[4].schema(), other.created);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.updated)) {
        this.updated = data().deepCopy(fields()[5].schema(), other.updated);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.species)) {
        this.species = data().deepCopy(fields()[6].schema(), other.species);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.sex)) {
        this.sex = data().deepCopy(fields()[7].schema(), other.sex);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.developmentalStage)) {
        this.developmentalStage = data().deepCopy(fields()[8].schema(), other.developmentalStage);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.dateOfBirth)) {
        this.dateOfBirth = data().deepCopy(fields()[9].schema(), other.dateOfBirth);
        fieldSetFlags()[9] = true;
      }
      if (isValidValue(fields()[10], other.diseases)) {
        this.diseases = data().deepCopy(fields()[10].schema(), other.diseases);
        fieldSetFlags()[10] = true;
      }
      if (isValidValue(fields()[11], other.phenotypes)) {
        this.phenotypes = data().deepCopy(fields()[11].schema(), other.phenotypes);
        fieldSetFlags()[11] = true;
      }
      if (isValidValue(fields()[12], other.stagingSystem)) {
        this.stagingSystem = data().deepCopy(fields()[12].schema(), other.stagingSystem);
        fieldSetFlags()[12] = true;
      }
      if (isValidValue(fields()[13], other.clinicalTreatment)) {
        this.clinicalTreatment = data().deepCopy(fields()[13].schema(), other.clinicalTreatment);
        fieldSetFlags()[13] = true;
      }
      if (isValidValue(fields()[14], other.strain)) {
        this.strain = data().deepCopy(fields()[14].schema(), other.strain);
        fieldSetFlags()[14] = true;
      }
      if (isValidValue(fields()[15], other.info)) {
        this.info = data().deepCopy(fields()[15].schema(), other.info);
        fieldSetFlags()[15] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.CharSequence getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public org.ga4gh.GAIndividual.Builder setId(java.lang.CharSequence value) {
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
    public org.ga4gh.GAIndividual.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'groupIds' field */
    public java.util.List<java.lang.CharSequence> getGroupIds() {
      return groupIds;
    }
    
    /** Sets the value of the 'groupIds' field */
    public org.ga4gh.GAIndividual.Builder setGroupIds(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[1], value);
      this.groupIds = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'groupIds' field has been set */
    public boolean hasGroupIds() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'groupIds' field */
    public org.ga4gh.GAIndividual.Builder clearGroupIds() {
      groupIds = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.CharSequence getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public org.ga4gh.GAIndividual.Builder setName(java.lang.CharSequence value) {
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
    public org.ga4gh.GAIndividual.Builder clearName() {
      name = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'description' field */
    public java.lang.CharSequence getDescription() {
      return description;
    }
    
    /** Sets the value of the 'description' field */
    public org.ga4gh.GAIndividual.Builder setDescription(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.description = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'description' field has been set */
    public boolean hasDescription() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'description' field */
    public org.ga4gh.GAIndividual.Builder clearDescription() {
      description = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'created' field */
    public java.lang.Long getCreated() {
      return created;
    }
    
    /** Sets the value of the 'created' field */
    public org.ga4gh.GAIndividual.Builder setCreated(java.lang.Long value) {
      validate(fields()[4], value);
      this.created = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'created' field has been set */
    public boolean hasCreated() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'created' field */
    public org.ga4gh.GAIndividual.Builder clearCreated() {
      created = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'updated' field */
    public java.lang.Long getUpdated() {
      return updated;
    }
    
    /** Sets the value of the 'updated' field */
    public org.ga4gh.GAIndividual.Builder setUpdated(java.lang.Long value) {
      validate(fields()[5], value);
      this.updated = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'updated' field has been set */
    public boolean hasUpdated() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'updated' field */
    public org.ga4gh.GAIndividual.Builder clearUpdated() {
      updated = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'species' field */
    public org.ga4gh.GAOntologyTerm getSpecies() {
      return species;
    }
    
    /** Sets the value of the 'species' field */
    public org.ga4gh.GAIndividual.Builder setSpecies(org.ga4gh.GAOntologyTerm value) {
      validate(fields()[6], value);
      this.species = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'species' field has been set */
    public boolean hasSpecies() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'species' field */
    public org.ga4gh.GAIndividual.Builder clearSpecies() {
      species = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /** Gets the value of the 'sex' field */
    public org.ga4gh.GASex getSex() {
      return sex;
    }
    
    /** Sets the value of the 'sex' field */
    public org.ga4gh.GAIndividual.Builder setSex(org.ga4gh.GASex value) {
      validate(fields()[7], value);
      this.sex = value;
      fieldSetFlags()[7] = true;
      return this; 
    }
    
    /** Checks whether the 'sex' field has been set */
    public boolean hasSex() {
      return fieldSetFlags()[7];
    }
    
    /** Clears the value of the 'sex' field */
    public org.ga4gh.GAIndividual.Builder clearSex() {
      sex = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /** Gets the value of the 'developmentalStage' field */
    public org.ga4gh.GAOntologyTerm getDevelopmentalStage() {
      return developmentalStage;
    }
    
    /** Sets the value of the 'developmentalStage' field */
    public org.ga4gh.GAIndividual.Builder setDevelopmentalStage(org.ga4gh.GAOntologyTerm value) {
      validate(fields()[8], value);
      this.developmentalStage = value;
      fieldSetFlags()[8] = true;
      return this; 
    }
    
    /** Checks whether the 'developmentalStage' field has been set */
    public boolean hasDevelopmentalStage() {
      return fieldSetFlags()[8];
    }
    
    /** Clears the value of the 'developmentalStage' field */
    public org.ga4gh.GAIndividual.Builder clearDevelopmentalStage() {
      developmentalStage = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    /** Gets the value of the 'dateOfBirth' field */
    public java.lang.Long getDateOfBirth() {
      return dateOfBirth;
    }
    
    /** Sets the value of the 'dateOfBirth' field */
    public org.ga4gh.GAIndividual.Builder setDateOfBirth(java.lang.Long value) {
      validate(fields()[9], value);
      this.dateOfBirth = value;
      fieldSetFlags()[9] = true;
      return this; 
    }
    
    /** Checks whether the 'dateOfBirth' field has been set */
    public boolean hasDateOfBirth() {
      return fieldSetFlags()[9];
    }
    
    /** Clears the value of the 'dateOfBirth' field */
    public org.ga4gh.GAIndividual.Builder clearDateOfBirth() {
      dateOfBirth = null;
      fieldSetFlags()[9] = false;
      return this;
    }

    /** Gets the value of the 'diseases' field */
    public java.util.List<org.ga4gh.GAOntologyTerm> getDiseases() {
      return diseases;
    }
    
    /** Sets the value of the 'diseases' field */
    public org.ga4gh.GAIndividual.Builder setDiseases(java.util.List<org.ga4gh.GAOntologyTerm> value) {
      validate(fields()[10], value);
      this.diseases = value;
      fieldSetFlags()[10] = true;
      return this; 
    }
    
    /** Checks whether the 'diseases' field has been set */
    public boolean hasDiseases() {
      return fieldSetFlags()[10];
    }
    
    /** Clears the value of the 'diseases' field */
    public org.ga4gh.GAIndividual.Builder clearDiseases() {
      diseases = null;
      fieldSetFlags()[10] = false;
      return this;
    }

    /** Gets the value of the 'phenotypes' field */
    public java.util.List<org.ga4gh.GAOntologyTerm> getPhenotypes() {
      return phenotypes;
    }
    
    /** Sets the value of the 'phenotypes' field */
    public org.ga4gh.GAIndividual.Builder setPhenotypes(java.util.List<org.ga4gh.GAOntologyTerm> value) {
      validate(fields()[11], value);
      this.phenotypes = value;
      fieldSetFlags()[11] = true;
      return this; 
    }
    
    /** Checks whether the 'phenotypes' field has been set */
    public boolean hasPhenotypes() {
      return fieldSetFlags()[11];
    }
    
    /** Clears the value of the 'phenotypes' field */
    public org.ga4gh.GAIndividual.Builder clearPhenotypes() {
      phenotypes = null;
      fieldSetFlags()[11] = false;
      return this;
    }

    /** Gets the value of the 'stagingSystem' field */
    public java.lang.CharSequence getStagingSystem() {
      return stagingSystem;
    }
    
    /** Sets the value of the 'stagingSystem' field */
    public org.ga4gh.GAIndividual.Builder setStagingSystem(java.lang.CharSequence value) {
      validate(fields()[12], value);
      this.stagingSystem = value;
      fieldSetFlags()[12] = true;
      return this; 
    }
    
    /** Checks whether the 'stagingSystem' field has been set */
    public boolean hasStagingSystem() {
      return fieldSetFlags()[12];
    }
    
    /** Clears the value of the 'stagingSystem' field */
    public org.ga4gh.GAIndividual.Builder clearStagingSystem() {
      stagingSystem = null;
      fieldSetFlags()[12] = false;
      return this;
    }

    /** Gets the value of the 'clinicalTreatment' field */
    public java.lang.CharSequence getClinicalTreatment() {
      return clinicalTreatment;
    }
    
    /** Sets the value of the 'clinicalTreatment' field */
    public org.ga4gh.GAIndividual.Builder setClinicalTreatment(java.lang.CharSequence value) {
      validate(fields()[13], value);
      this.clinicalTreatment = value;
      fieldSetFlags()[13] = true;
      return this; 
    }
    
    /** Checks whether the 'clinicalTreatment' field has been set */
    public boolean hasClinicalTreatment() {
      return fieldSetFlags()[13];
    }
    
    /** Clears the value of the 'clinicalTreatment' field */
    public org.ga4gh.GAIndividual.Builder clearClinicalTreatment() {
      clinicalTreatment = null;
      fieldSetFlags()[13] = false;
      return this;
    }

    /** Gets the value of the 'strain' field */
    public java.lang.CharSequence getStrain() {
      return strain;
    }
    
    /** Sets the value of the 'strain' field */
    public org.ga4gh.GAIndividual.Builder setStrain(java.lang.CharSequence value) {
      validate(fields()[14], value);
      this.strain = value;
      fieldSetFlags()[14] = true;
      return this; 
    }
    
    /** Checks whether the 'strain' field has been set */
    public boolean hasStrain() {
      return fieldSetFlags()[14];
    }
    
    /** Clears the value of the 'strain' field */
    public org.ga4gh.GAIndividual.Builder clearStrain() {
      strain = null;
      fieldSetFlags()[14] = false;
      return this;
    }

    /** Gets the value of the 'info' field */
    public java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> getInfo() {
      return info;
    }
    
    /** Sets the value of the 'info' field */
    public org.ga4gh.GAIndividual.Builder setInfo(java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>> value) {
      validate(fields()[15], value);
      this.info = value;
      fieldSetFlags()[15] = true;
      return this; 
    }
    
    /** Checks whether the 'info' field has been set */
    public boolean hasInfo() {
      return fieldSetFlags()[15];
    }
    
    /** Clears the value of the 'info' field */
    public org.ga4gh.GAIndividual.Builder clearInfo() {
      info = null;
      fieldSetFlags()[15] = false;
      return this;
    }

    @Override
    public GAIndividual build() {
      try {
        GAIndividual record = new GAIndividual();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.groupIds = fieldSetFlags()[1] ? this.groupIds : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[1]);
        record.name = fieldSetFlags()[2] ? this.name : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.description = fieldSetFlags()[3] ? this.description : (java.lang.CharSequence) defaultValue(fields()[3]);
        record.created = fieldSetFlags()[4] ? this.created : (java.lang.Long) defaultValue(fields()[4]);
        record.updated = fieldSetFlags()[5] ? this.updated : (java.lang.Long) defaultValue(fields()[5]);
        record.species = fieldSetFlags()[6] ? this.species : (org.ga4gh.GAOntologyTerm) defaultValue(fields()[6]);
        record.sex = fieldSetFlags()[7] ? this.sex : (org.ga4gh.GASex) defaultValue(fields()[7]);
        record.developmentalStage = fieldSetFlags()[8] ? this.developmentalStage : (org.ga4gh.GAOntologyTerm) defaultValue(fields()[8]);
        record.dateOfBirth = fieldSetFlags()[9] ? this.dateOfBirth : (java.lang.Long) defaultValue(fields()[9]);
        record.diseases = fieldSetFlags()[10] ? this.diseases : (java.util.List<org.ga4gh.GAOntologyTerm>) defaultValue(fields()[10]);
        record.phenotypes = fieldSetFlags()[11] ? this.phenotypes : (java.util.List<org.ga4gh.GAOntologyTerm>) defaultValue(fields()[11]);
        record.stagingSystem = fieldSetFlags()[12] ? this.stagingSystem : (java.lang.CharSequence) defaultValue(fields()[12]);
        record.clinicalTreatment = fieldSetFlags()[13] ? this.clinicalTreatment : (java.lang.CharSequence) defaultValue(fields()[13]);
        record.strain = fieldSetFlags()[14] ? this.strain : (java.lang.CharSequence) defaultValue(fields()[14]);
        record.info = fieldSetFlags()[15] ? this.info : (java.util.Map<java.lang.CharSequence,java.util.List<java.lang.CharSequence>>) defaultValue(fields()[15]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
