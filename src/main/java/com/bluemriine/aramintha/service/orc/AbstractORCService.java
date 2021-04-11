package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service ORC
 * @author BlueM
 */
public abstract class AbstractORCService {

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");

	/** Tesseract ORC */
	protected Tesseract tesseract;

	/** Configure Tesseract */
	protected void configureTesseract(Integer pageSegmentation) {
		tesseract = new Tesseract();
		if (StringUtils.isBlank(DataHolder.getInstance().getPathTesseract())) {
			File tessDataFolder = LoadLibs.extractTessResources("tessdata");
			DataHolder.getInstance().setPathTesseract(tessDataFolder.getAbsolutePath());
		}
		tesseract.setDatapath(DataHolder.getInstance().getPathTesseract());
		tesseract.setLanguage("fra");
		tesseract.setPageSegMode(pageSegmentation);
		tesseract.setOcrEngineMode(1);
		tesseract.setTessVariable("user_defined_dpi", "70");
	}

	/** Extrait le texte d'une image */
	protected List<String> getLines(File file) throws TesseractException {
		String result = tesseract.doOCR(file);
		logger.log(Level.INFO, () -> "Untouch text : \r\n" + result);
		List<String> lines = new LinkedList<>();
		Collections.addAll(lines, result.split("\\r?\\n"));
		return lines;
	}

	/** Lance la reconnaissance de texte */
	public abstract void doOCR(File file, Consumer<String> consumerOk, Consumer<String> consumerKo);
}
