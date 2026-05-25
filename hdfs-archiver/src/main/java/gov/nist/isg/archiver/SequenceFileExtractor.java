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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * @author Antoine Vandecreme
 */
public class SequenceFileExtractor {

    private final Configuration conf;

    private final SequenceFile.Reader reader;

    private final Text currentFile;

    private final BytesWritable currentContent;

    public SequenceFileExtractor(Path sequenceFile, Configuration conf) throws IOException {
        this.conf = conf;
        reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(sequenceFile));
        currentFile = (Text) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
        currentContent = (BytesWritable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
    }

    public List<String> getFilesList() throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void extractAll(Path outputPath) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void close() throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
