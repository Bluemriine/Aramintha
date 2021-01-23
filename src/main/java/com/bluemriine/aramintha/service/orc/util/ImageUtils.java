package com.bluemriine.aramintha.service.orc.util;

import com.bluemriine.aramintha.data.DataHolder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/** Classe utilitaire pour la manipulation d'images */
public final class ImageUtils {

	/**
	 * Inverse les couleurs d'une image.
	 * @param originalImage - Le fichier d'origine.
	 * @param consumerKo    - Action à faire en cas d'erreur.
	 * @return L'image avec les couleurs en négatif.
	 */
	public static Optional<File> invertImage(File originalImage, Consumer<String> consumerKo) {
		Optional<File> outputFile = Optional.empty();
		try {
			BufferedImage img = ImageIO.read(originalImage);
			int width = img.getWidth();
			int height = img.getHeight();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int p = img.getRGB(x, y);
					int a = (p >> 24) & 0xff;
					int r = (p >> 16) & 0xff;
					int g = (p >> 8) & 0xff;
					int b = p & 0xff;
					r = 255 - r;
					g = 255 - g;
					b = 255 - b;
					p = (a << 24) | (r << 16) | (g << 8) | b;
					img.setRGB(x, y, p);
				}
			}
			String screenshotInvertName = UUID.randomUUID() + ".png";
			File inverted = new File(screenshotInvertName);
			outputFile = Optional.of(inverted);

			ImageIO.write(img, "png", inverted);

		} catch (IOException ex) {
			consumerKo.accept("Erreur de manipulation du négatif.");
			System.out.println(ExceptionUtils.getStackTrace(ex));
		}
		return outputFile;
	}

	/**
	 * Redimensionne une image.
	 * @param originalImage - Le fichier d'origine.
	 * @param consumerKo    - Action à faire en cas d'erreur.
	 * @return L'image redimensionnée
	 */
	public static Optional<File> simpleResizeImage(File originalImage, Consumer<String> consumerKo) {
		Optional<File> outputFile = Optional.empty();
		try {
			BufferedImage img = ImageIO.read(originalImage);
			String screenshotResizeName = UUID.randomUUID() + ".png";
			File resized = new File(screenshotResizeName);
			outputFile = Optional.of(resized);
			ImageIO.write(Scalr.resize(img, 1940), "png", resized);
		} catch (Exception ex) {
			consumerKo.accept("Erreur du redimensionnement de l'image.");
			System.out.println(ExceptionUtils.getStackTrace(ex));
		}
		return outputFile;
	}

	/**
	 * Supprime une image
	 * @param image- Le fichier à supprimer.
	 */
	public static void deleteFiles(File image) {
		if (image != null) {
			if (image.delete()) {
				System.out.println("Suppression du fichier Ok.");
			}
			else {
				System.out.println("Suppression du fichier Ko.");
			}
		}
	}

	/**
	 * Prend une capture d'écran
	 * @param consumerKo - Action à faire en cas d'erreur.
	 * @return La capture.
	 */
	public static Optional<File> screenshot(Consumer<String> consumerKo) {
		Optional<File> outputFile = Optional.empty();
		try {
			Rectangle screenRect = new Rectangle(DataHolder.getInstance().getSquareX1(), DataHolder.getInstance().getSquareY1(), DataHolder.getInstance().getSquareX2() - DataHolder.getInstance().getSquareX1(), DataHolder.getInstance().getSquareY2() - DataHolder.getInstance().getSquareY1());
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			String pathname = UUID.randomUUID() + ".png";
			ImageIO.write(capture, "png", new File(pathname));
			outputFile = Optional.of(new File(pathname));
		} catch (Exception ex) {
			consumerKo.accept("Erreur de la capture de l'image.");
			System.out.println(ExceptionUtils.getStackTrace(ex));
		}
		return outputFile;
	}

	/**
	 * Duplique l'image et la colle en dessous
	 * @param file - L'image
	 */
	public static void duplicateImage(File file) {
		try {
			BufferedImage img1 = ImageIO.read(file);
			if (img1 != null) {
				int hMax = img1.getHeight(null);
				int wMax = img1.getWidth(null);
				BufferedImage buf = new BufferedImage(wMax, hMax * 2, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = buf.createGraphics();
				g2.drawImage(img1, 0, 0, null);
				g2.drawImage(img1, 0, hMax, null);
				g2.dispose();
				ImageIO.write(buf, "png", file);
			}
		} catch (Exception ex) {
			System.out.println(ExceptionUtils.getStackTrace(ex));
		}
	}
}
