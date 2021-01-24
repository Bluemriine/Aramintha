package com.bluemriine.aramintha.service;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.view.interactor.MainViewComponentInteractor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Service d'importation de la liste des membres.
 * @author BlueM
 */
public class ImportListePseudoService {

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");

	/**
	 * The constructor !!!
	 */
	private ImportListePseudoService() {
	}

	/**
	 * Importe la liste des pseudos
	 * @param absolutePathFile - Le chemin du fichier contenant les informations.
	 */
	public static void importListe(String absolutePathFile) {
		if (StringUtils.endsWith(absolutePathFile, ".txt") || StringUtils.endsWith(absolutePathFile, ".csv")) {
			try {
				Stream<String> lines = Files.lines(Paths.get(absolutePathFile));
				lines.forEachOrdered(item -> DataHolder.getInstance().getListePseudo().add(item));
				lines.close();
				MainViewComponentInteractor.getInstance().setMessage("Liste chargée.");
				logger.log(Level.INFO, () -> "Liste de pseudos chargée.");
			} catch (IOException ex) {
				logger.log(Level.SEVERE, () -> "Erreur lors de lecture de la liste." + ExceptionUtils.getStackTrace(ex));
				MainViewComponentInteractor.getInstance().setMessage("Erreur lors de lecture de la liste.");
			}
		}
		else {
			logger.log(Level.WARNING, () -> "Erreur lors de lecture de la liste, le fichier n'a pas le bon format !");
			MainViewComponentInteractor.getInstance().setMessage("Erreur de format du fichier.");
		}
	}
}
