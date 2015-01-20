variant-store
=============

Genomic Variant Store prototype implementation. Store VCFs in Parquet files, to be queried with Apache Drill

# Installation

    mvn install

# Running

Change the hardcoded paths in java, to point at a different VCF folder.

    mvn exec:java

## Query data

1. Set up apache drill as described [here](https://cwiki.apache.org/confluence/display/DRILL/Apache+Drill+in+10+Minutes).
2. In the sqlline, run `USE phenotips.root;`
3. Query away! For example: `select alternateBases from test.vcf.parquet where referenceBase='C'`.

