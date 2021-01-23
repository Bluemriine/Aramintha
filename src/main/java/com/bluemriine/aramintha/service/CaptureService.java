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

/** Service de capture d'image */
public class CaptureService {

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
	 * Lance l'analyse de la zone de capture
	 */
	private void analyse(AbstractORCService service) {
		MainViewComponentInteractor.getInstance().setMessage("Traitement en cours ...");
		try {
			Thread.sleep(1000);
			Optional<File> optScreenshot = ImageUtils.screenshot(msg -> MainViewComponentInteractor.getInstance().setMessage(msg));

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
						service.doOCR(screenshotResize, consumerOk, msg -> MainViewComponentInteractor.getInstance().setMessage(msg));
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ExceptionUtils.getStackTrace(ex));
			MainViewComponentInteractor.getInstance().setMessage("Erreur lors de la capture de l'image.");
		}

		ImageUtils.deleteFiles(screenshot);
		screenshot = null;
		ImageUtils.deleteFiles(screenshotInvert);
		screenshotInvert = null;
		ImageUtils.deleteFiles(screenshotResize);
		screenshotResize = null;
	}


	/**
	 * Lance l'analyse de la zone de capture
	 */
	public void analyseZoneCaptureAtkDef() {
		analyse(new AtkDefORCService());
	}

	/**
	 * Lance l'analyse de la zone de capture
	 */
	public void analyseZoneCaptureContribution() {
		analyse(new ContributionORCService());
	}
}