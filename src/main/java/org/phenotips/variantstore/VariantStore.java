package org.phenotips.variantstore;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.storage.DrillManager;
import org.phenotips.variantstore.storage.StorageManager;

/**
 * The Variant Store enables the storage of many many variants.
 */
public class VariantStore {
    private String vcfDir;
    private String outDir;
    private Logger logger = Logger.getLogger(VariantStore.class);

    DrillManager drillManager;
    StorageManager storageManager;

    /**
     *
     * @param drillPath Drill's configuration string, same as you would pass to sqlline
     */
    public VariantStore(String drillPath, String vcfDir, String outDir) throws VariantStoreException {
        this.vcfDir = vcfDir;
        this.outDir = outDir;

        storageManager = new StorageManager(this.outDir);

        try {
            drillManager = new DrillManager(drillPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new VariantStoreException(e.getMessage());
        }
    }

    public VariantStore(String vcfDir, String outDir) throws VariantStoreException {
        this("jdbc:drill:zk=local", vcfDir, outDir);
    }

    public void addAllInDirectory(String vcfDir) throws VariantStoreException {
        try {
            this.storageManager.addAllInDirectory(vcfDir);
        } catch (InterruptedException e) {
            throw new VariantStoreException(e.getMessage());
        }
    }

    public static void main(String[] args){
        try {
            VariantStore store = new VariantStore("/home/meatcar/dev/drill/vcf/",
                    "/home/meatcar/dev/drill/parquet/"
            );

            store.addAllInDirectory("/home/meatcar/dev/drill/vcf/");


        } catch (VariantStoreException e) {
            e.printStackTrace();
        }
    }
}
