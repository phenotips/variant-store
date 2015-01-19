package org.phenotips.variantstore.writers;

import htsjdk.variant.variantcontext.VariantContext;
import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.hadoop.fs.Path;
import parquet.avro.AvroParquetWriter;

/**
 * Created by meatcar on 1/19/15.
 */
public abstract class AbstractParquetWriter {
    AvroParquetWriter avroWriter;

    public void setParquetWriter(String filename, String outdir, Schema schema) throws IOException {
        File f = new File(outdir, filename + ".parquet");

        // Delete/overwrite the file if exists. Parquet doesn't support appends..
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }

        avroWriter = new AvroParquetWriter(new Path(f.getAbsolutePath()), schema);

    }

    public abstract void write(VariantContext vcfRow) throws IOException;

    public void writeAvro(Object model) throws IOException {
        avroWriter.write(model);
    }

    public void close() throws IOException {
        avroWriter.close();
    };
}
