import avro
from avro import *
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter

import vcf

def get_avro_writer(path, schema):
    return DataFileWriter(open(path, "wb"), DatumWriter(), schema)

def get_avro_reader(path):
    return DataFileReader(open(path, "r"), DatumReader())


if __name__ == "__main__":
    with open("avro/schemata/GAVariant.avsc") as f:
        schema_GAVariant = avro.schema.Parse(f.read())

    with open("avro/schemata/GACall.avsc") as f:
        schema_GACall = avro.schema.Parse(f.read())

    ## Write
    with get_avro_writer("./data.avro", schema_GAVariant) as avro_writer, \
         open("./test.vcf") as vcf_file:

        vcf_reader = vcf.Reader(vcf_file)

        i = 0
        for record in vcf_reader:
            # Process calls.
            calls = []
            j = 0
            for call in record.samples:
                info_dict = dict(call.data.__dict__)
                c = {
                    #"callSetId": None,
                    #"callSetName": "{}_{}".format(call.sample, j),
                    "genotype": [int(i) for i in call.gt_alleles],
                    #"phaseset": None,
                    "genotypeLikelihood": [float(call.data.GL)] if hasattr(call.data, 'GL') else [],
                    "info": {k: str(info_dict[k]) for k in info_dict}
                }
                print(avro.io.Validate(schema_GACall, c), i, record.POS)
                calls.append(c)
                j += 1
            i += 1
            continue;

            # Process variants
            metadata = dict(vcf_reader.metadata)
            variant = {
                    "id": "{}_{}".format(vcf_reader.samples[0], i),
                    "variantSetId": "experimentSet",
                    "created": metadata['fileDate'],
                    "updated": None,
                    "referenceName": record.CHROM,
                    "start": record.start,
                    "end": record.end,
                    "referenceBases": record.REF,
                    "alternateBases": record.ALT,
                    "info": record.INFO,
                    "calls": calls
            }
            avro_writer.append(variant)
            i += 1

    ## Read
    with get_avro_reader("./data.avro") as reader:
        for variant in reader:
            print(variant)
