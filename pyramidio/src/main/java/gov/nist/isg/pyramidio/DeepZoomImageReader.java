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
package gov.nist.isg.pyramidio;

import gov.nist.isg.pyramidio.tools.BufferedImageHelper;
import gov.nist.isg.pyramidio.tools.ImageResizingHelper;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.FilenameUtils;

/**
 * DZI pyramid reader. Thread safe.
 *
 * @author Antoine Vandecreme
 */
public class DeepZoomImageReader implements PartialImageReader {

    private final File dziFile;

    private final File filesFolder;

    private final int tileSize;

    private final int overlap;

    private final String format;

    private final int width;

    private final int height;

    private final int maxLevel;

    private final ImageTypeSpecifier rawImageType;

    public DeepZoomImageReader(File dziFile) throws IOException {
        this(dziFile, null);
    }

    public DeepZoomImageReader(File dziFile, File tileExample) throws IOException {
        this.dziFile = dziFile;
        String name = FilenameUtils.getBaseName(dziFile.getName());
        this.filesFolder = new File(dziFile.getParent(), name + "_files");
        if (!filesFolder.exists()) {
            throw new IOException("No files folder found: " + filesFolder);
        }
        DziFile df = new DziFile(dziFile);
        tileSize = df.getTileSize();
        overlap = df.getOverlap();
        format = df.getFormat();
        width = df.getWidth();
        height = df.getHeight();
        if (tileExample == null) {
            tileExample = getFilesOfLevel(0).get(0);
        }
        try (ImageInputStream iis = ImageIO.createImageInputStream(tileExample)) {
            ImageReader reader = getImageReader(iis);
            reader.setInput(iis);
            this.rawImageType = reader.getRawImageType(0);
        }
        int maxDim = Math.max(width, height);
        maxLevel = (int) Math.ceil(Math.log(maxDim) / Math.log(2));
    }

    public File getDziFile() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public File getFilesFolder() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public int getTileSize() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public int getOverlap() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public String getFormat() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int getWidth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int getHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BufferedImage read() throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BufferedImage read(Rectangle rectangle) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Get the whole image at the specified zoom level.
     *
     * @param zoom The desired zoom level
     * @return
     * @throws IOException
     */
    public BufferedImage getWholeImage(double zoom) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Get the region specified. Pixels outside of the image are filled in
     * black.
     *
     * @param region
     * @param zoom
     * @return
     * @throws IOException
     */
    public BufferedImage getRegion(Rectangle region, double zoom) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Get the sub image specified by the region. The region must be entirely
     * inside the image.
     *
     * @param region
     * @param zoom
     * @return
     * @throws IOException
     */
    public BufferedImage getSubImage(Rectangle region, double zoom) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public BufferedImage readRegionOfLevel(Rectangle region, int level) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private BufferedImage readRegionOfTile(Rectangle region, int level, int column, int row) throws IOException {
        File levelFolder = new File(filesFolder, Integer.toString(level));
        File tile = new File(levelFolder, column + "_" + row + "." + format);
        try (ImageInputStream iis = ImageIO.createImageInputStream(tile)) {
            ImageReader reader = getImageReader(iis);
            reader.setInput(iis);
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(region);
            return reader.read(0, param);
        }
    }

    private int getClosestLevel(double zoom) {
        if (zoom > 0.5) {
            return maxLevel;
        }
        return maxLevel + (int) Math.ceil(Math.log(zoom) / Math.log(2));
    }

    private double getZoomOfLevel(int level) {
        return Math.pow(2, level - maxLevel);
    }

    private List<File> getFilesOfLevel(int level) {
        int widthOfLevel = 1;
        int heightOfLevel = 1;
        if (level != 0) {
            widthOfLevel = Math.min(2 * level, width);
            heightOfLevel = Math.min(2 * level, height);
        }
        int numColumns = (int) Math.ceil(widthOfLevel / (float) tileSize);
        int numRows = (int) Math.ceil(heightOfLevel / (float) tileSize);
        File levelFolder = new File(filesFolder, Integer.toString(level));
        ArrayList<File> result = new ArrayList<>(numColumns * numRows);
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                File file = new File(levelFolder, i + "_" + j + "." + format);
                result.add(file);
            }
        }
        return result;
    }

    private static ImageReader getImageReader(ImageInputStream iis) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (!readers.hasNext()) {
            throw new IOException("No compatible image reader found.");
        }
        return readers.next();
    }
}
