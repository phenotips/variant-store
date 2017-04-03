Variant Store
=============

The Variant Store is a Java library that wraps the complexity of processing and
handling large number of variants, exposing an interface for other applications to query
the collected content of the VCF files. 

This repo provides two projects:

- `./variant-store` -- the actual Variant Store
- `./integration` -- the PhenoTips - Variant Store integration

### Supported Annotations

* [Exomiser](http://www.sanger.ac.uk/resources/software/exomiser/)
	is used for variant harmfulness.
* [ExAC](ftp://ftp.broadinstitute.org/pub/ExAC_release/release0.3/)
	allele frequencies are provided by Exomiser.

### Usage

#### Installation

    mvn install

#### Running

```java
VariantStore vs = new VariantStore(
		new Exomiser6TSVManager(),
		new SolrController()
);

vs.init(Paths.get("/where/to/store/data/");
vs.addIndividual("IndividualId", true, Path.get("/path/to/variant/file"));
vs.removeIndividual("IndividualId");
```

# Architecture

The input-file handling and the variant storage are decoupled. This allows us to support multiple file 
types, and allow for the possibility of switching out the underlying database. 

Internally, we use ga4gh-style representation of variants instead of VCF. Some differences include 0-based variant 
`start` instead of 1-based `position`. The input and query interfaces, however, are VCF-style, and the conversion is 
done before accessing the database.

Queries return `GAVariants`, which are objects generated from the ga4gh schemas. 
`org.phenotips.variantstore.shared.VariantUtils` provides utilities for working with the objects.

#### Design Goals

* Provide a self-contained abstraction for dealing with genomic variants.
* Automatic deploy from a single jar with no manual actions by the user.
* Fast inserts and queries on single-node and multi-node installations
* Be flexible w.r.t. input file types and storage backends.

#### Startup

1. Configure `VariantStore` with the desired input manager and DB.
2. `VariantStore.init(path)`
3. Input manager makes it's folder inside of `path`
4. DB unpacks the bundled resources in the jar into `path`

#### Inserting a VCF

This is the primary use-case for the Variant Store. This flow is used by PhenoTips' 
[patient-network](https://github.com/phenotips/patient-network);

![Inserting a VCF](https://cdn.rawgit.com/phenotips/variant-store/master/doc/inserting-diagram.svg)

1. VCFs are processed into TSVs by Exomiser externally (not handled by the Variant Store)
2. Exomiser output TSV files are passed to the Variant Store
3. The Variant Store
	1. Passes the TSV to the `TSVManager`
	2. the `SolrController` spins up a task to add the individual
	3. the `AddIndividualTask` 

#### Scaling to multiple nodes

Solr can be scaled to multiple nodes using SolrCloud. The setup and deployment of a SolrCloud cluster is outside the
scope of this project. 

Assuming you have a cluster up and running, you can add a new `DatabaseController`, following `SolrController` as a guidance. 
The new contoller would configure SolrJ to connect to the SolrCloud zookeeper instance. See the 
[Solr docs](https://cwiki.apache.org/confluence/display/solr/Using+SolrJ) for more info.

# Future steps

* auto-detect the desired input manager to use based on the file's file path.
* Integrate jannovar or exomiser as a pre-processing step for files.
