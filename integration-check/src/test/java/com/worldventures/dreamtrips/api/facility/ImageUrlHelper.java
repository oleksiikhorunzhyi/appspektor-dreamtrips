package com.worldventures.dreamtrips.api.facility;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageUrlHelper {

    private ImageUrlHelper() {

    }

    public static String obtainSizedImageUrl(String imageUrl, int width, int height) {
        return String.format("%s?width=%d&height=%d", imageUrl, width, height);
    }

    public static Size obtainSizeOfImageFromUrlWithAssertion(String imageUrl) {
        try {
            return obtainSizeOfImageFromUrl(imageUrl);
        } catch (IOException e) {
            throw new AssertionError("Can't get image at " + imageUrl);
        }
    }

    public static Size obtainSizeOfImageFromUrl(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        int width = image.getWidth();
        int height = image.getHeight();

        return new Size(width, height);
    }

    public static final class Size {
        public int width;
        public int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Size() {
            this(0, 0);
        }

        @Override
        public String toString() {
            return "Size(" + width + ", " + height + ")";
        }
    }


}
