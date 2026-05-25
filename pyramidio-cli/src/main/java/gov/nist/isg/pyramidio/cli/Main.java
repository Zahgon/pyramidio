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
package gov.nist.isg.pyramidio.cli;

import gov.nist.isg.archiver.FilesArchiver;
import gov.nist.isg.pyramidio.DirectImageReader;
import gov.nist.isg.pyramidio.ScalablePyramidBuilder;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Antoine Vandecreme
 */
public class Main {

    public static void main(String[] args) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static void printHelp(Options options) {
        new HelpFormatter().printHelp("pyramidio", options);
    }
}
