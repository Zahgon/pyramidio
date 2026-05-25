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
package gov.nist.isg.pyramidio.stitching;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author Antoine Vandecreme
 */
public class ImageTile {

    private final File file;

    private final Rectangle region;

    private final double correlation;

    public ImageTile(File file, Rectangle region, double correlation) {
        this.file = file;
        this.region = region;
        this.correlation = correlation;
    }

    public File getFile() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Rectangle getRegion() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public double getCorrelation() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Rectangle getIntersectionWithStitchedImageRegion(Rectangle rectangle) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public BufferedImage readStitchedImageRegion(Rectangle rectangle) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Read part of the tile in the specified region (in this tile coordinates)
     *
     * @param region
     * @return
     * @throws IOException
     */
    public BufferedImage readTileRegion(Rectangle region) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
