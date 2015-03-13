package org.phenotips.variantstore.input.vcf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.xml.crypto.Data;
import org.apache.derby.database.Database;
import org.ga4gh.GAIndividual;
import org.ga4gh.GAOntologyTerm;
import org.phenotips.variantstore.Service;
import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.shared.ResourceManager;

/**
 * Manage the raw VCF files that we store.
 */
public class VCFManager implements Service {
    private Path path;

    public void init(Path path) throws DatabaseException {
        this.path = path;

        if (!Files.exists(this.path)) {
            try {
                Files.createDirectories(this.path);
            } catch (IOException e) {
                throw new DatabaseException("Unable to create directory.");
            }
        }
    }

    public void stop() {

    }

    /**
     * Store the individual's VCF.
     * @param id the id of the individual
     * @param path the path to the existing VCF file
     */
    public void addIndividual(String id, Path path) throws DatabaseException {
        try {
            Files.copy(path, this.getIndividual(id), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DatabaseException("Error copying VCF for storage.", e);
        }
    }

    public Path getIndividual(String id) {
        return this.path.resolve(id + ".vcf.gz");
    }

    public void removeIndividual(String id) throws DatabaseException {
        try {
            Files.delete(this.getIndividual(id));
        } catch (IOException e) {
            throw new DatabaseException("Error removing VCF", e);
        }
    }
}
