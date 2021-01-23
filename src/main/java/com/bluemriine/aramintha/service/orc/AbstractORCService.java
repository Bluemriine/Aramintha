package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service ORC
 * @author BlueM
 */
public abstract class AbstractORCService {

	/** Tesseract ORC */
	protected Tesseract tesseract;

	/** Configure Tesseract */
	protected void configureTesseract() {
		tesseract = new Tesseract();
		if (StringUtils.isBlank(DataHolder.getInstance().getPathTesseract())) {
			File tessDataFolder = LoadLibs.extractTessResources("tessdata");
			DataHolder.getInstance().setPathTesseract(tessDataFolder.getAbsolutePath());
		}
		tesseract.setDatapath(DataHolder.getInstance().getPathTesseract());
		tesseract.setLanguage("fra");
		tesseract.setPageSegMode(1);
		tesseract.setOcrEngineMode(1);
		tesseract.setTessVariable("user_defined_dpi", "300");
	}

	/** Extrait le texte d'une image */
	protected List<String> getLines(File file) throws TesseractException {
		String result = tesseract.doOCR(file);
		List<String> lines = new LinkedList<>();
		String[] tab = result.split("\\r?\\n");
		for (int i = 0; i < tab.length; i++) {
			lines.add(tab[i]);
		}
		return lines;
	}

	/** Lance la reconnaissance de texte */
	public abstract void doOCR(File file, Consumer<String> consumerOk, Consumer<String> consumerKo);
}
