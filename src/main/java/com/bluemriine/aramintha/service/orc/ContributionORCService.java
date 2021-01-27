package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatContributionMembreDto;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Extraction des données ATK / DEF
 * @author BlueM
 */
public class ContributionORCService extends AbstractORCService {

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");

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
			List<String> lines = getLines(file);
			List<String> cleanLines = lines.stream().filter(line -> StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "Rank")).collect(Collectors.toList());

			StringJoiner sj = new StringJoiner("\r\n");
			cleanLines.forEach(sj::add);
			logger.log(Level.INFO, () -> "Lignes trouvées dans l'image : \r\n" + sj.toString());

			if (cleanLines.size() % 2 == 0) {
				for (int i = 0; i < cleanLines.size() - 1; i = i + 2) {
					ResultatContributionMembreDto dto = new ResultatContributionMembreDto();
					dto.setPseudo(cleanLines.get(i));
					dto.setPoints(cleanData(cleanLines.get(i + 1)));
					DataHolder.getInstance().getListResultatContributionMembre().add(dto);
				}
				consumerOk.accept("Prêt pour la prochaine extraction.");
			}
			else {
				logger.log(Level.WARNING, () -> "Erreur, le nombre de lignes est impaire.");
				consumerKo.accept("Erreur lors de l'analyse de l'image.");
			}

		} catch (TesseractException ex) {
			logger.log(Level.SEVERE, () -> "Erreur lors de l'analyse de l'image :" + ExceptionUtils.getStackTrace(ex));
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
		final Integer result = StringUtils.isBlank(temp) ? 0 : Integer.parseInt(temp);
		logger.log(Level.INFO, () -> "Input : " + input + " Output : " + result);

		return result;
	}

}
