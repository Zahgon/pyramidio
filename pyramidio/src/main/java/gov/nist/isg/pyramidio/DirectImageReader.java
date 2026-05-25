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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author plitvak
 */
public class DirectImageReader implements PartialImageReader {

    private final File imageFile;

    private final Dimension dimension;

    public DirectImageReader(File imageFile) throws IOException {
        this.imageFile = imageFile;
        this.dimension = getImageDimension();
    }

    @Override
    public BufferedImage read() throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BufferedImage read(Rectangle rectangle) throws IOException {
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

    private Dimension getImageDimension() throws IOException {
        return executeWithImageReader(new Function<Dimension>() {

            @Override
            public Dimension apply(ImageReader imageReader) throws IOException {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        });
    }

    private BufferedImage readRegion(final Rectangle rectangle) throws IOException {
        return executeWithImageReader(new Function<BufferedImage>() {

            @Override
            public BufferedImage apply(ImageReader imageReader) throws IOException {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        });
    }

    private <T> T executeWithImageReader(Function<T> f) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(imageFile).getChannel())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    return f.apply(reader);
                } finally {
                    reader.dispose();
                }
            } else {
                throw new IOException("No codec found for image " + imageFile);
            }
        }
    }

    // for compatibility with pre Java 8 source
    private interface Function<T> {

        T apply(ImageReader imageReader) throws IOException;
    }
}
