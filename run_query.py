import vcf

vcf_reader = vcf.Reader(open('test.vcf', 'r'))

for record in vcf_reader:
    print(record)
