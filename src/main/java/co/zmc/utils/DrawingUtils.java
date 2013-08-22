package co.zmc.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
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
}
