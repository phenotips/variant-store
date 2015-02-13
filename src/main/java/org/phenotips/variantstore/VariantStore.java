package org.phenotips.variantstore;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Future;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.phenotips.variantstore.storage.DrillManager;
import org.phenotips.variantstore.storage.InvalidFileFormatException;
import org.phenotips.variantstore.storage.SolrController;
import org.phenotips.variantstore.storage.StorageManager;

/**
 * The Variant Store enables the storage of many many variants.
 */
public class VariantStore {
    private Path outDir;
    private static Logger logger = Logger.getLogger(VariantStore.class);

    private DrillManager drillManager;
    private StorageManager storageManager;

    /**
     *
     * @param drillPath Drill's configuration string, same as you would pass to sqlline
     * @param outDir
     */
    public VariantStore(String drillPath, Path outDir) throws VariantStoreException {
        this.outDir = outDir;

        storageManager = new StorageManager(this.outDir);

        try {
            drillManager = new DrillManager(drillPath);
        } catch (Exception e) {
            throw new VariantStoreException(e.getMessage(), e);
        }
    }

    public VariantStore(Path vcfDir) throws VariantStoreException {
        this("jdbc:drill:zk=local", vcfDir);
    }

    public Connection connection() {
        return drillManager.connection();
    }

    public void stop() throws VariantStoreException {
        try {
            drillManager.stop();
        } catch (SQLException e) {
            throw new VariantStoreException(e.getMessage(), e);
        }
    }

    public Future addFile(Path filePath) throws InvalidFileFormatException {
        return storageManager.add(filePath);
    }

    /**
     * Add all the VCF files found in the given directory to the store.
     * If an invalid file is encountered, an exception will be thrown.
     * @param dirPath the path to the directory containing VCF Files
     * @return a List of Futures, each future bound to the completion of each file's progress.
     */
    public List<Future> addFilesFromDirectory(Path dirPath) throws InvalidFileFormatException {
        return addFilesFromDirectory(dirPath, false);
    }

    /**
     * Add all the VCF files found in the given directory to the store.
     * @param dirPath the path to the directory containing VCF Files
     * @param ignoreInvalidFiles whether to throw warnings when an invalid file is encountered.
     * @return a List of Futures, each future bound to the completion of each file's progress.
     */
    public List<Future> addFilesFromDirectory(Path dirPath, boolean ignoreInvalidFiles) throws InvalidFileFormatException {

        String[] directoryListing = dirPath.toFile().list(StorageManager.getSupportedFileFilter());
        List<Future> futures = new ArrayList<>();
        if (directoryListing != null) {
            for (String vcfFile : directoryListing) {
                logger.debug("Queueing " + dirPath.resolve(vcfFile));
                try {
                    futures.add(this.addFile(dirPath.resolve(vcfFile)));
                } catch (InvalidFileFormatException e) {
                    if (!ignoreInvalidFiles) {
                        throw e;
                    }
                }
            }
        } else {
            return null;
        }

        return futures;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /** DRILL JDBC headache **/
//        Class.forName("org.apache.drill.jdbc.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:drill:zk=local", null);
//        String query = "select N_NAME from dfs.`/home/meatcar/dev/drill/apache-drill-0.7.0/sample-data/nation.parquet`";
//
//        Statement statement = connection.createStatement();
//
//        // hangs here
//        System.out.println("About to hang..");
//        ResultSet rs = statement.executeQuery(query);
//        System.out.println("Didn't hang!!!");
        /**/

        logger.debug("IF THIS PRINTS THE LORD HEARD MY PRAYERS!");
        System.out.println("starting");
        Path solrHome = Paths.get("/data/solr");
        SolrController controller = new SolrController(solrHome);

        try {
            List<Path> paths = Arrays.asList(
                    Paths.get("/data/vcf/completegenomics/vcfBeta-HG00731-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-HG00732-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-HG00733-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA06985-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA06994-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA07357-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA10851-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12004-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12877-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12878-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12879-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12880-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12881-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12882-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12883-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12884-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12885-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12886-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12887-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12888-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12889-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12890-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12891-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12892-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA12893-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18501-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18502-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18504-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18505-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18508-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18517-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18526-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18537-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18555-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18558-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18940-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18942-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18947-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA18956-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19017-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19020-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19025-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19026-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19129-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19238-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19239-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19240-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19648-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19649-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19669-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19670-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19700-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19701-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19703-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19704-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19735-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA19834-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20502-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20509-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20510-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20511-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20845-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20846-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20847-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA20850-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA21732-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA21733-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA21737-200-37-ASM.csv"),
                    Paths.get("/data/vcf/completegenomics/vcfBeta-NA21767-200-37-ASM.csv")
            );

            for (Path path : paths) {
                System.out.println(new Date() + " Parsing " + path);
                Reader in = new FileReader(path.toString());
                CSVParser csv = CSVFormat.DEFAULT.parse(in);


                for (CSVRecord record: csv) {
                    controller.add(record.iterator());
                }

                System.out.println("Committing");
                controller.getConnection().commit();
                System.out.println(new Date() + " DONE");

            }

            SolrQuery query = new SolrQuery();
            query.setQuery("*")
                    .addFilterQuery("exomiser_variant_score:[0.9999999 TO *]");
            ;
            controller.query("SELECT * FROM vcfs WHERE exomiser_variant_score > 0.9999999", query, true);

            query = new SolrQuery();
            query.setQuery("*");
            controller.query("SELECT * FROM vcfs", query, true);


        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (VariantStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Stopping");
            controller.stop();
        }
    }
}
