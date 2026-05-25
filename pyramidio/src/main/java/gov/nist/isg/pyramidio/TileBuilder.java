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

import gov.nist.isg.archiver.FilesArchiver;
import gov.nist.isg.pyramidio.tools.BufferedImageHelper;
import gov.nist.isg.pyramidio.tools.ImageResizingHelper;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

/**
 * Tile builder class which is scalable for very large images not fitting in
 * memory while minimizing the read accesses.
 *
 * Each region forming a tile at the maximum zoom level is read only once. A
 * tile at level n is computed as soon as enough tiles at level n+1 have been
 * computed. (A tile at level n is composed of 4 tiles at level n+1).
 *
 * @author Antoine Vandecreme
 */
class TileBuilder {

    private final int tileSize;

    private final int overlap;

    private final String tileFormat;

    private final PartialImageReader imageReader;

    private final FilesArchiver archiver;

    private final int nbLevels;

    private final String imgDir;

    private final int originalWidth;

    private final int originalHeight;

    TileBuilder(int tileSize, int overlap, String tileFormat, String descriptorExt, PartialImageReader imageReader, String fileName, FilesArchiver archiver) throws IOException {
        this.tileSize = tileSize;
        this.overlap = overlap;
        this.tileFormat = tileFormat;
        this.imageReader = imageReader;
        this.archiver = archiver;
        originalWidth = imageReader.getWidth();
        originalHeight = imageReader.getHeight();
        String nameWithoutExtension = FilenameUtils.getBaseName(fileName);
        String descriptorName = nameWithoutExtension + '.' + descriptorExt;
        DziFile dziFile = new DziFile(tileSize, overlap, tileFormat, originalWidth, originalHeight);
        dziFile.write(descriptorName, archiver);
        int maxDim = Math.max(originalWidth, originalHeight);
        nbLevels = (int) Math.ceil(Math.log(maxDim) / Math.log(2));
        imgDir = fileName + "_files";
    }

    void build(int parallelism, float maxImageCachePercentage) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private int getCacheLevel(float maxImageCachePercentage) {
        if (maxImageCachePercentage >= 1) {
            return 0;
        }
        if (maxImageCachePercentage <= 0) {
            return nbLevels;
        }
        int imageWidth = imageReader.getWidth();
        int imageHeight = imageReader.getHeight();
        long area = (long) imageWidth * (long) imageHeight;
        float maxCachedArea = area * maxImageCachePercentage;
        double maxCachedSize = Math.floor(Math.sqrt(maxCachedArea));
        for (int i = 0; i < nbLevels; i++) {
            Rectangle tileRegion = getTileRegionInEntireImage(i, 0, 0);
            if (tileRegion.width <= maxCachedSize && tileRegion.height <= maxCachedSize) {
                return i;
            }
        }
        return nbLevels;
    }

    private class TileBuilderTask extends RecursiveTask<BufferedImage> {

        private final int level;

        private final int tileRow;

        private final int tileColumn;

        private final boolean useFork;

        private final boolean useCache;

        private final int cacheLevel;

        private final ImageReaderCache imageReaderCache;

        private TileBuilderTask(int level, int tileRow, int tileColumn, boolean useFork, boolean useCache, int cacheLevel, ImageReaderCache imageReaderCache) {
            this.level = level;
            this.tileRow = tileRow;
            this.tileColumn = tileColumn;
            this.useFork = useFork;
            this.useCache = useCache;
            this.cacheLevel = cacheLevel;
            if (useCache && level == cacheLevel) {
                Rectangle tileRegion = getTileRegionInEntireImage(level, tileRow, tileColumn);
                if (tileRegion != null) {
                    try {
                        imageReaderCache = new ImageReaderCache(imageReader, tileRegion);
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot cache region " + tileRegion, e);
                    }
                }
            }
            this.imageReaderCache = imageReaderCache;
        }

        @Override
        protected BufferedImage compute() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        private TileBuilderTask getTask(int level, int tileRow, int tileColumn) {
            return new TileBuilderTask(level, tileRow, tileColumn, useFork, useCache, cacheLevel, imageReaderCache);
        }

        private BufferedImage getTile(int row, int col) throws IOException {
            int x = col * tileSize - (col == 0 ? 0 : overlap);
            int y = row * tileSize - (row == 0 ? 0 : overlap);
            int w = tileSize + (col == 0 ? 1 : 2) * overlap;
            int h = tileSize + (row == 0 ? 1 : 2) * overlap;
            if (x + w > originalWidth) {
                w = originalWidth - x;
            }
            if (y + h > originalHeight) {
                h = originalHeight - y;
            }
            Rectangle region = new Rectangle(x, y, w, h);
            if (region.isEmpty()) {
                return null;
            }
            return useCache ? imageReaderCache.read(region) : imageReader.read(region);
        }
    }

    private Rectangle getTileRegionInEntireImage(int level, int row, int col) {
        Rectangle tileRegionAtLevel = getTileRegionAtLevel(level, row, col);
        if (tileRegionAtLevel == null) {
            return null;
        }
        double factor = Math.pow(2, nbLevels - level);
        int scaledX = (int) Math.ceil(tileRegionAtLevel.x * factor);
        int scaledY = (int) Math.ceil(tileRegionAtLevel.y * factor);
        int scaledWidth = (int) Math.ceil(tileRegionAtLevel.width * factor);
        int scaledHeight = (int) Math.ceil(tileRegionAtLevel.height * factor);
        if (scaledX + scaledWidth > originalWidth) {
            scaledWidth = originalWidth - scaledX;
        }
        if (scaledY + scaledHeight > originalHeight) {
            scaledHeight = originalHeight - scaledY;
        }
        return new Rectangle(scaledX, scaledY, scaledWidth, scaledHeight);
    }

    private Rectangle getTileRegionAtLevel(int level, int row, int col) {
        double factor = Math.pow(2, nbLevels - level);
        int levelWidth = (int) Math.ceil(originalWidth / factor);
        int levelHeight = (int) Math.ceil(originalHeight / factor);
        int nbCols = (int) Math.ceil((double) levelWidth / tileSize);
        int nbRows = (int) Math.ceil((double) levelHeight / tileSize);
        if (col >= nbCols || row >= nbRows) {
            return null;
        }
        int x = col * tileSize - (col == 0 ? 0 : overlap);
        int y = row * tileSize - (row == 0 ? 0 : overlap);
        int w = tileSize + (col == 0 ? 1 : 2) * overlap;
        int h = tileSize + (row == 0 ? 1 : 2) * overlap;
        if (x + w > levelWidth) {
            w = levelWidth - x;
        }
        if (y + h > levelHeight) {
            h = levelHeight - y;
        }
        return new Rectangle(x, y, w, h);
    }

    private Dimension getTileDimensions(int level, int row, int col) {
        Rectangle tileRegionAtLevel = getTileRegionAtLevel(level, row, col);
        if (tileRegionAtLevel == null) {
            return new Dimension(0, 0);
        }
        return new Dimension(tileRegionAtLevel.width, tileRegionAtLevel.height);
    }

    /**
     * Write an image to the archiver.
     *
     * @param image the image to write
     * @param format the image file format to use (png, jpeg...)
     * @param fileName the file where the image must be written to without the
     * file extension
     * @param archiver the FilesArchiver to use
     * @throws IOException
     */
    private static void writeImage(final BufferedImage image, final String format, String fileName, FilesArchiver archiver) throws IOException {
        fileName = fileName + "." + format;
        boolean write = archiver.appendFile(fileName, new FilesArchiver.FileAppender<Boolean>() {

            @Override
            public Boolean append(OutputStream outputStream) throws IOException {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        });
        if (!write) {
            throw new IOException("No " + format + " image writer found.");
        }
    }
}
