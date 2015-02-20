package org.phenotips.variantstore;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Future;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
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

        logger.debug("starting");
        Path solrHome = Paths.get("/data/solr");
        SolrController controller = new SolrController(solrHome);

        try {
            List<Path> paths = Arrays.asList(
            );


            boolean add = true;
            for (Path path : paths) {
                String patient = FilenameUtils.removeExtension(path.getFileName().toString());

                if (add) {
                    logger.debug(new Date() + " Parsing " + path);
                    Reader in = new FileReader(path.toString());
                    CSVParser csv = CSVFormat.DEFAULT.parse(in);

                    for (CSVRecord record: csv) {
                        controller.add(record.iterator(), patient );
                    }

                    logger.debug("Committing");
                    controller.getConnection().commit();
                    logger.debug(new Date() + " DONE");
                }

                SolrQuery query = new SolrQuery();
                query.setQuery("*")
                        .addFilterQuery("exomiser_variant_score:[0.9999999 TO *]");
                controller.query("SELECT * FROM vcfs WHERE exomiser_variant_score > 0.9999999", query, true);

                query = new SolrQuery();
                query.setQuery("*");
                int fetchSize = 100;
                int offset = 0;
                long numFound = 1;
                while (offset < numFound) {
                    query.setStart(offset);
                    query.setRows(fetchSize);
                    numFound = controller.query("SELECT * FROM vcfs", query, true).getResults().getNumFound();
                    offset += fetchSize;
                    break;
                }



            }


        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (VariantStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.debug("Stopping");
            controller.stop();
        }
    }
}
