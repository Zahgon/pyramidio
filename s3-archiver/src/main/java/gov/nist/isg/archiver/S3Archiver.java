/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.isg.archiver;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

/**
 * @author Gulam D. Shakir
 */
public class S3Archiver implements FilesArchiver {

    private final String bucket;

    private final String prefix;

    public S3Archiver(URI outputURI) {
        bucket = outputURI.getHost();
        prefix = outputURI.getPath().substring(1);
        AmazonS3Client client = new AmazonS3Client();
        ObjectListing listing = client.listObjects(bucket, prefix);
        if (!listing.getObjectSummaries().isEmpty()) {
            throw new IllegalStateException("The bucket path exists: " + bucket + "/" + prefix);
        }
    }

    @Override
    public <T> T appendFile(String path, FileAppender<T> appender) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public <T> T appendBigFile(String path, FileAppender<T> appender) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void appendFile(String path, File file) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
