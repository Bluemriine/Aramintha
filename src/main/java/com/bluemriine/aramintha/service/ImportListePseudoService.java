package com.bluemriine.aramintha.service;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.view.interactor.MainViewComponentInteractor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Service d'importation de la liste des membres.
 * @author BlueM
 */
public class ImportListePseudoService {

	/**
	 * The constructor !!!
	 * @param absolutePathFile - Le chemin du fichier contenant les informations.
	 */
	public ImportListePseudoService(String absolutePathFile) {
		if (StringUtils.endsWith(absolutePathFile, ".txt") || StringUtils.endsWith(absolutePathFile, ".csv")) {
			try {
				Stream<String> lines = Files.lines(Paths.get(absolutePathFile));
				lines.forEachOrdered(item -> DataHolder.getInstance().getListePseudo().add(item));
				lines.close();
				MainViewComponentInteractor.getInstance().setMessage("Liste chargée.");
			} catch (IOException ex) {
				System.out.println(ExceptionUtils.getStackTrace(ex));
				MainViewComponentInteractor.getInstance().setMessage("Erreur lors de lecture de la liste.");
			}
		}
		else {
			MainViewComponentInteractor.getInstance().setMessage("Erreur lors de lecture de la liste.");
		}
	}
}
