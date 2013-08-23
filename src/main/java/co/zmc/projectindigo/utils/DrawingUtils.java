/*
 * This file is part of ProjectIndigo.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * ProjectIndigo is licensed under the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package co.zmc.projectindigo.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Kernel;
import java.awt.image.RGBImageFilter;
import java.lang.ref.SoftReference;

public class DrawingUtils {

    public static BlurUtils getBlurUtils() {
        return new BlurUtils();
    }

    public static class BlurUtils {
        private static SoftReference<BufferedImage> _buffer0;
        private static SoftReference<BufferedImage> _buffer1;

        public BufferedImage applyGaussianBlur(BufferedImage image, int filterRadius, float alphaFactor, boolean useOriginalImageAsDestination) {
            if (filterRadius < 1) { throw new IllegalArgumentException("Illegal filter radius: expected to be >= 1, was " + filterRadius); }

            float[] kernel = new float[2 * filterRadius + 1];

            float sigma = filterRadius / 3.0F;
            float alpha = 2.0F * sigma * sigma;
            float rootAlphaPI = (float) Math.sqrt(alpha * 3.141592653589793D);
            float sum = 0.0F;
            for (int i = 0; i < kernel.length; i++) {
                int d = -((i - filterRadius) * (i - filterRadius));
                kernel[i] = (float) (Math.exp(d / alpha) / rootAlphaPI);
                sum += kernel[i];
            }

            for (int i = 0; i < kernel.length; i++) {
                kernel[i] /= sum;
                kernel[i] *= alphaFactor;
            }

            Kernel horizontalKernel = new Kernel(kernel.length, 1, kernel);
            Kernel verticalKernel = new Kernel(1, kernel.length, kernel);

            synchronized (BlurUtils.class) {
                int blurredWidth = useOriginalImageAsDestination ? image.getWidth() : image.getWidth() + 4 * filterRadius;
                int blurredHeight = useOriginalImageAsDestination ? image.getHeight() : image.getHeight() + 4 * filterRadius;

                BufferedImage img0 = ensureBuffer0Capacity(blurredWidth, blurredHeight);
                Graphics2D graphics0 = img0.createGraphics();
                graphics0.drawImage(image, null, useOriginalImageAsDestination ? 0 : 2 * filterRadius, useOriginalImageAsDestination ? 0
                        : 2 * filterRadius);
                graphics0.dispose();

                BufferedImage img1 = ensureBuffer1Capacity(blurredWidth, blurredHeight);
                Graphics2D graphics1 = img1.createGraphics();
                graphics1.drawImage(img0, new ConvolveOp(horizontalKernel, 1, null), 0, 0);
                graphics1.dispose();

                BufferedImage destination = useOriginalImageAsDestination ? image : new BufferedImage(blurredWidth, blurredHeight, 2);
                Graphics2D destGraphics = destination.createGraphics();
                destGraphics.drawImage(img1, new ConvolveOp(verticalKernel, 1, null), 0, 0);
                destGraphics.dispose();

                return destination;
            }
        }

        private static BufferedImage ensureBuffer0Capacity(int width, int height) {
            BufferedImage img0 = _buffer0 != null ? (BufferedImage) _buffer0.get() : null;
            img0 = ensureBufferCapacity(width, height, img0);
            _buffer0 = new SoftReference(img0);
            return img0;
        }

        private static BufferedImage ensureBuffer1Capacity(int width, int height) {
            BufferedImage img1 = _buffer1 != null ? (BufferedImage) _buffer0.get() : null;
            img1 = ensureBufferCapacity(width, height, img1);
            _buffer1 = new SoftReference(img1);
            return img1;
        }

        private static BufferedImage ensureBufferCapacity(int width, int height, BufferedImage img) {
            if ((img == null) || (img.getWidth() < width) || (img.getHeight() < height)) {
                img = new BufferedImage(width, height, 2);
            } else {
                Graphics2D g2 = img.createGraphics();
                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, width, height);
                g2.dispose();
            }
            return img;
        }
    }

    public static BufferedImage roundCorners(BufferedImage src, int radius) {
        int w = src.getWidth();
        int h = src.getHeight();
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        RoundRectangle2D r2 = new RoundRectangle2D.Double(0, 0, w, h, radius, radius);
        g2.setClip(r2);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dst;
    }

    public static BufferedImage overlayImage(BufferedImage under, BufferedImage over) {
        BufferedImage dest = under;
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(over.getScaledInstance(under.getWidth(), under.getHeight(), 4), 0, 0, null);
        g2.dispose();
        return dest;
    }

    public static BufferedImage makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFFFF0000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return getBI(Toolkit.getDefaultToolkit().createImage(ip));
    }

    private static BufferedImage getBI(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;

    }
}
