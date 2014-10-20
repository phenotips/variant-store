/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.ga4gh;

@SuppressWarnings("all")
/** This protocol defines common types used in the other GA4GH protocols. It does
not have any methods; it is merely a library of types. */
@org.apache.avro.specific.AvroGenerated
public interface GACommon {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"GACommon\",\"namespace\":\"org.ga4gh\",\"doc\":\"This protocol defines common types used in the other GA4GH protocols. It does\\nnot have any methods; it is merely a library of types.\",\"types\":[{\"type\":\"error\",\"name\":\"GAException\",\"doc\":\"A general exception type.\",\"fields\":[{\"name\":\"message\",\"type\":\"string\",\"doc\":\"The error message\"},{\"name\":\"errorCode\",\"type\":\"int\",\"doc\":\"The numerical error code\",\"default\":-1}]},{\"type\":\"record\",\"name\":\"GAPosition\",\"doc\":\"An abstraction for referring to a genomic position, in relation to some\\nalready known reference. For now, represents a genomic position as a reference\\nname, a base number on that reference (0-based), and a flag to say if it's the\\nforward or reverse strand that we're talking about.\",\"fields\":[{\"name\":\"referenceName\",\"type\":\"string\",\"doc\":\"The name of the reference (or, more technically, the scaffold) in whatever\\n  reference set is being used. Does not generally include a \\\"chr\\\" prefix, so for\\n  example \\\"X\\\" would be used for the X chromosome.\"},{\"name\":\"position\",\"type\":\"long\",\"doc\":\"The 0-based offset from the start of the forward strand for that reference.\\n  Genomic positions are non-negative integers less than reference length.\"},{\"name\":\"reverseStrand\",\"type\":\"boolean\",\"doc\":\"A flag to indicate if we are on the forward strand (`false`) or reverse\\n  strand (`true`).\"}]},{\"type\":\"enum\",\"name\":\"GACigarOperation\",\"doc\":\"An enum for the different types of CIGAR alignment operations that exist.\\nUsed wherever CIGAR alignments are used. The different enumerated values\\nhave the following usage:\\n\\n* `ALIGNMENT_MATCH`: An alignment match indicates that a sequence can be\\n  aligned to the reference without evidence of an INDEL. Unlike the\\n  `SEQUENCE_MATCH` and `SEQUENCE_MISMATCH` operators, the `ALIGNMENT_MATCH`\\n  operator does not indicate whether the reference and read sequences are an\\n  exact match. This operator is equivalent to SAM's `M`.\\n* `INSERT`: The insert operator indicates that the read contains evidence of\\n  bases being inserted into the reference. This operator is equivalent to\\n  SAM's `I`.\\n* `DELETE`: The delete operator indicates that the read contains evidence of\\n  bases being deleted from the reference. This operator is equivalent to\\n  SAM's `D`.\\n* `SKIP`: The skip operator indicates that this read skips a long segment of\\n  the reference, but the bases have not been deleted. This operator is\\n  commonly used when working with RNA-seq data, where reads may skip long\\n  segments of the reference between exons. This operator is equivalent to\\n  SAM's 'N'.\\n* `CLIP_SOFT`: The soft clip operator indicates that bases at the start/end\\n  of a read have not been considered during alignment. This may occur if the\\n  majority of a read maps, except for low quality bases at the start/end of\\n  a read. This operator is equivalent to SAM's 'S'. Bases that are soft clipped\\n  will still be stored in the read.\\n* `CLIP_HARD`: The hard clip operator indicates that bases at the start/end of\\n  a read have been omitted from this alignment. This may occur if this linear\\n  alignment is part of a chimeric alignment, or if the read has been trimmed\\n  (e.g., during error correction, or to trim poly-A tails for RNA-seq). This\\n  operator is equivalent to SAM's 'H'.\\n* `PAD`: The pad operator indicates that there is padding in an alignment.\\n  This operator is equivalent to SAM's 'P'.\\n* `SEQUENCE_MATCH`: This operator indicates that this portion of the aligned\\n  sequence exactly matches the reference (e.g., all bases are equal to the\\n  reference bases). This operator is equivalent to SAM's '='.\\n* `SEQUENCE_MISMATCH`: This operator indicates that this portion of the\\n  aligned sequence is an alignment match to the reference, but a sequence\\n  mismatch (e.g., the bases are not equal to the reference). This can\\n  indicate a SNP or a read error. This operator is equivalent to SAM's 'X'.\",\"symbols\":[\"ALIGNMENT_MATCH\",\"INSERT\",\"DELETE\",\"SKIP\",\"CLIP_SOFT\",\"CLIP_HARD\",\"PAD\",\"SEQUENCE_MATCH\",\"SEQUENCE_MISMATCH\"]},{\"type\":\"record\",\"name\":\"GACigarUnit\",\"doc\":\"A structure for an instance of a CIGAR operation.\",\"fields\":[{\"name\":\"operation\",\"type\":\"GACigarOperation\",\"doc\":\"The operation type.\"},{\"name\":\"operationLength\",\"type\":\"long\",\"doc\":\"The number of bases that the operation runs for.\"},{\"name\":\"referenceSequence\",\"type\":[\"null\",\"string\"],\"doc\":\"`referenceSequence` is only used at mismatches (`SEQUENCE_MISMATCH`)\\n  and deletions (`DELETE`). Filling this field replaces the MD tag.\\n  If the relevant information is not available, leave this field as `null`.\",\"default\":null}]}],\"messages\":{}}");

  @SuppressWarnings("all")
  /** This protocol defines common types used in the other GA4GH protocols. It does
not have any methods; it is merely a library of types. */
  public interface Callback extends GACommon {
    public static final org.apache.avro.Protocol PROTOCOL = org.ga4gh.GACommon.PROTOCOL;
  }
}