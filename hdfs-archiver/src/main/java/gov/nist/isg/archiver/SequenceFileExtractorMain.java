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
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author Antoine Vandecreme
 */
public class SequenceFileExtractorMain extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int run(String[] args) throws Exception {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void extractSequenceFile(Path inputFile, Path outputDirectory) throws IOException {
        System.out.println("Extracting sequence file " + inputFile);
        SequenceFileExtractor extr = new SequenceFileExtractor(inputFile, getConf());
        try {
            extr.extractAll(outputDirectory);
        } finally {
            extr.close();
        }
    }

    private void extractDirectory(Path inputDirectory, Path outputDirectory) throws IOException {
        FileSystem fs = inputDirectory.getFileSystem(getConf());
        RemoteIterator<LocatedFileStatus> files = fs.listFiles(inputDirectory, false);
        while (files.hasNext()) {
            Path file = files.next().getPath();
            String fileName = file.getName();
            if ("seq".equalsIgnoreCase(FilenameUtils.getExtension(fileName))) {
                extractSequenceFile(file, outputDirectory);
            }
        }
    }
}
