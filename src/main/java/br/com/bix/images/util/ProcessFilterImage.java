package br.com.bix.images.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class ProcessFilterImage {

    private ProcessFilterImage() {}

    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        if (newWidth < 1 || newHeight < 1) {
            return null;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage negative(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        BufferedImage neg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int rgb = image.getRGB(col,row);
                Color c = new Color(rgb);
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                Color colorRGB = new Color((255 - red),(255 - green),(255 - blue));
                neg.setRGB(col,row,colorRGB.getRGB());
            }
        }

        return neg;
    }

    public static BufferedImage gray(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        BufferedImage imageGray = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int rgb = image.getRGB(col,row);
                Color c = new Color(rgb);
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                int gray = (red + green + blue) / 3;
                Color colorGray = new Color(gray,gray,gray);
                imageGray.setRGB(col,row,colorGray.getRGB());
            }
        }

        return imageGray;
    }
}
