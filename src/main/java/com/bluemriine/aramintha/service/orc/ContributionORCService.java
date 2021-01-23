package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatContributionMembreDto;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Extraction des données ATK / DEF
 * @author BlueM
 */
public class ContributionORCService extends AbstractORCService {

	/**
	 * The constructor !!!
	 */
	public ContributionORCService() {
		configureTesseract();
	}

	/**
	 * Lance l'analyse
	 * @param file - Le fichier à analyser.
	 */
	@Override
	public void doOCR(File file, Consumer<String> consumerOk, Consumer<String> consumerKo) {
		try {
			String result = tesseract.doOCR(file);
			List<String> lines = Arrays.asList(result.split("\\r?\\n"));
			List<String> cleanLines = lines.stream().filter(line -> StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "Rank")).collect(Collectors.toList());

			if (cleanLines.size() % 2 == 0) {
				for (int i = 0; i < cleanLines.size() - 1; i++) {
					ResultatContributionMembreDto dto = new ResultatContributionMembreDto();
					dto.setPseudo(cleanLines.get(i));
					dto.setPoints(cleanData(cleanLines.get(++i)));
					DataHolder.getInstance().getListResultatContributionMembre().add(dto);
				}
				consumerOk.accept("Prêt pour la prochaine extraction.");
			}
			else {
				consumerKo.accept("Erreur lors de l'analyse de l'image.");
			}

		} catch (TesseractException ex) {
			System.out.println(ExceptionUtils.getStackTrace(ex));
			consumerKo.accept("Erreur lors de l'analyse de l'image.");
		}
	}

	/**
	 * Extract only number
	 * @param input - La chaîne de texte à nettoyer.
	 * @return Un nombre
	 */
	private static Integer cleanData(String input) {
		String temp = input.replaceAll("[^0-9]", "");
		temp = StringUtils.isBlank(temp) ? "0" : temp;
		return Integer.valueOf(temp);
	}

}
