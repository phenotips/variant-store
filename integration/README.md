# Variant Store PhenoTips Integration

PhenoTips components to enable the usage of the VariantStore within PhenoTips.

#### Contains:

- `./api` 
- the `Components` to use the Variant Store from within the xwiki injection framework,
- the `ScriptServices` to allow the Variant Store to be used within velocity templates.
- `./ui` 
- an HTTP endpoint to upload files to the Variant Store. Used by `patient-network` cron scripts.
- the beginning of a rudimentary UI to manage running import jobs

#### Steps to build phenotips standalone with variant store:

Set up phenotips standalone for required version (1.3m1r2):
1. `wget https://nexus.phenotips.org/nexus/content/repositories/releases/org/phenotips/phenotips-standalone/1.3-milestone-1r2/phenotips-standalone-1.3-milestone-1r2.zip`
2. `unzip phenotips-standalone-1.3-milestone-1r2.zip`

Integrate patient-network on branch PN-111-use-vs:
1. `git clone https://github.com/phenotips/patient-network.git`
2. `git checkout PN-111-use-vs`
3. `mvn clean install`
4. `find . -name "*.jar" | xargs -I"{}" cp "{}" /path/to/phenotips-standalone-1.3-milestone-1r2/webapps/phenotips/WEB-INF/lib/`

Integrate variant-store on branch master:
1. `git clone https://github.com/phenotips/variant-store.git`
2. `mvn clean install`
3. `find . -name "*.jar" | xargs -I"{}" cp "{}" /path/to/phenotips-standalone-1.3-milestone-1r2/webapps/phenotips/WEB-INF/lib/`

Import UIs:
1. Start up phenotips
2. Import the two UIs through the admin interface:
a. patient-network/ui/target
b. variant-store/integration/ui/target
c. (optional) patient-network/matching-notification-ui/target

Load an Exomiser variant file for a patient:
1. Create a new PhenoTips patient record, e.g., P0000001
2. Get Exomiser variants.tsv file (see next post)
3. Tell Exomiser to load the tsv (in this case, `example-exomiser-results.variants.tsv`) for a particular patient, e.g.: `curl -X POST -s -S -u Admin:admin 'localhost:8080/bin/PhenoTips/VariantStoreUploadService?outputSyntax=plain&xpage=plain&path=/path/to/example-exomiser-results.variants.tsv&individualId=P0000001'`
