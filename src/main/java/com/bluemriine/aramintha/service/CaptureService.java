package com.bluemriine.aramintha.service;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.service.orc.AbstractORCService;
import com.bluemriine.aramintha.service.orc.AtkDefORCService;
import com.bluemriine.aramintha.service.orc.ContributionORCService;
import com.bluemriine.aramintha.service.orc.util.ImageUtils;
import com.bluemriine.aramintha.view.interactor.MainViewComponentInteractor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Service de capture d'image */
public class CaptureService {

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");

	/** Image originale */
	private File screenshot;
	/** Image avec les couleurs inversées */
	private File screenshotInvert;
	/** Image redimensionnée */
	private File screenshotResize;

	/** The constructor */
	public CaptureService() {
		// Nothing to do.
	}

	/**
	 * Capture l'image et lance l'analyse de la zone de capture
	 * @param service - Service d'extraction des données
	 */
	private void captureEtAnalyse(AbstractORCService service) {
		logger.log(Level.INFO, () -> "Début du traitement de l'image .");
		MainViewComponentInteractor.getInstance().setMessage("Traitement en cours ...");
		try {
			Thread.sleep(500);
			Optional<File> optScreenshot = ImageUtils.screenshot(msg -> MainViewComponentInteractor.getInstance().setMessage(msg));
			analyse(service, optScreenshot);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, () -> "Erreur lors de la capture de l'image" + ExceptionUtils.getStackTrace(ex));
			MainViewComponentInteractor.getInstance().setMessage("Erreur lors de la capture de l'image.");
		}
	}

	/**
	 * Lance l'analyse de la zone de capture
	 * @param service       - Service d'extraction des données
	 * @param optScreenshot - L'optional contenant possiblement un screenshot
	 */
	public void analyse(AbstractORCService service, Optional<File> optScreenshot) {
		if (optScreenshot.isPresent()) {
			screenshot = optScreenshot.get();
			Optional<File> optInvertedScreenshot = ImageUtils.invertImage(screenshot, msg -> MainViewComponentInteractor.getInstance().setMessage(msg));
			if (optInvertedScreenshot.isPresent()) {
				screenshotInvert = optInvertedScreenshot.get();

				Optional<File> optResizedScreenshot = ImageUtils.simpleResizeImage(screenshotInvert, msg -> MainViewComponentInteractor.getInstance().setMessage(msg));
				if (optResizedScreenshot.isPresent()) {
					screenshotResize = optResizedScreenshot.get();
					Consumer<String> consumerOk = msg -> {
						DataHolder.getInstance().getAnalyseBouton().setEnabled(true);
						MainViewComponentInteractor.getInstance().setMessage(msg);
					};
					logger.log(Level.INFO, () -> "Traitement de l'image terminé, début de l'OCR.");
					service.doOCR(screenshotResize, consumerOk, msg -> MainViewComponentInteractor.getInstance().setMessage(msg));
				}
			}
			ImageUtils.deleteFiles(screenshot);
			screenshot = null;
			ImageUtils.deleteFiles(screenshotInvert);
			screenshotInvert = null;
			ImageUtils.deleteFiles(screenshotResize);
			screenshotResize = null;
		}
	}


	/**
	 * Lance l'analyse de la zone de capture
	 */
	public void analyseZoneCaptureAtkDef() {
		captureEtAnalyse(new AtkDefORCService());
	}

	/**
	 * Lance l'analyse de la zone de capture
	 */
	public void analyseZoneCaptureContribution() {
		captureEtAnalyse(new ContributionORCService());
	}
}
