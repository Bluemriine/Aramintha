package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatAtkDefMembreDto;
import com.bluemriine.aramintha.service.orc.util.ImageUtils;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Extraction des données ATK / DEF
 * @author BlueM
 */
public class AtkDefORCService extends AbstractORCService {

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");
	private static final String INPUT = "Input : ";
	private static final String OUTPUT = " Output : ";

	/**
	 * The constructor !!!
	 */
	public AtkDefORCService() {
		configureTesseract();
	}

	/**
	 * Lance l'analyse
	 * @param file       - Le fichier à analyser.
	 * @param consumerOk - Action en cas de succès
	 * @param consumerKo - Action en cas d'erreur
	 */
	@Override
	public void doOCR(File file, Consumer<String> consumerOk, Consumer<String> consumerKo) {
		try {
			List<String> lines = getLines(file);
			// Si il n'y a qu'un seul membre de sélectionné la lecture des données est bordélique.
			if (lines.size() < 14) {
				ImageUtils.duplicateImage(file);
				doOCR(file, consumerOk, consumerKo);
			}
			else {
				long nombreMembre = lines.stream().filter(line -> StringUtils.contains(line, "Rank.")).count();
				List<String> cleanLinesStep1 = lines.stream().filter(line -> StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "Rank")).collect(Collectors.toList());
				List<String> names = cleanLinesStep1.subList(0, (int) nombreMembre);
				List<String> datasLines = cleanLinesStep1.subList((int) nombreMembre, cleanLinesStep1.size());
				List<String> cleanLinesStep2 = datasLines.stream().filter(line -> StringUtils.contains(line, "(s)") || StringUtils.contains(line, "(s") || StringUtils.contains(line, "s)")).collect(Collectors.toList());
				List<String> cleanLines = new LinkedList<>();
				cleanLines.addAll(names);
				cleanLines.addAll(cleanLinesStep2);

				StringJoiner sj = new StringJoiner("\r\n");
				cleanLines.forEach(sj::add);
				logger.log(Level.INFO, () -> "Lignes trouvées dans l'image : \r\n" + sj.toString());

				readLines(consumerOk, consumerKo, cleanLines);
			}
		} catch (TesseractException ex) {
			logger.log(Level.SEVERE, () -> "Erreur lors de l'analyse de l'image :" + ExceptionUtils.getStackTrace(ex));
			consumerKo.accept("Erreur lors de l'analyse de l'image.");
		}
	}

	/**
	 * Analyse les lignes extraites
	 * @param consumerOk - Action en cas de succès
	 * @param consumerKo - Action en cas d'erreur
	 * @param cleanLines - Les lignes à analyser
	 */
	private void readLines(Consumer<String> consumerOk, Consumer<String> consumerKo, List<String> cleanLines) {
		switch (cleanLines.size()) {
			case 14: {
				ResultatAtkDefMembreDto mb1 = createMember(cleanLines, Arrays.asList(0, 2, 3, 4, 8, 9, 10));
				ResultatAtkDefMembreDto mb2 = createMember(cleanLines, Arrays.asList(1, 5, 6, 7, 11, 12, 13));
				if (mb1.getPseudo().equalsIgnoreCase(mb2.getPseudo())) {
					DataHolder.getInstance().getListResultatAtkDefMembre().add(mb1);
				}
				else {
					DataHolder.getInstance().getListResultatAtkDefMembre().addAll(Arrays.asList(mb1, mb2));
				}
				consumerOk.accept("Prêt pour la prochaine extraction.");
				break;
			}
			case 21: {
				ResultatAtkDefMembreDto mb1 = createMember(cleanLines, Arrays.asList(0, 3, 4, 5, 12, 13, 14));
				ResultatAtkDefMembreDto mb2 = createMember(cleanLines, Arrays.asList(1, 6, 7, 8, 15, 16, 17));
				ResultatAtkDefMembreDto mb3 = createMember(cleanLines, Arrays.asList(2, 9, 10, 11, 18, 19, 20));
				DataHolder.getInstance().getListResultatAtkDefMembre().addAll(Arrays.asList(mb1, mb2, mb3));
				consumerOk.accept("Prêt pour la prochaine extraction.");
				break;
			}
			default:
				StringJoiner sj = new StringJoiner("\r\n");
				cleanLines.forEach(sj::add);
				logger.log(Level.SEVERE, () -> "Erreur dans la lecture des lines : " + sj.toString());
				consumerKo.accept("Erreur lors de l'analyse de l'image.");
				break;
		}
	}


	/**
	 * Transforme les données en objet.
	 * @param cleanLines - Le tas de données .
	 * @param numLinesJ1 - La liste des lignes à extraire.
	 * @return Les données extraites.
	 */
	private static ResultatAtkDefMembreDto createMember(List<String> cleanLines, List<Integer> numLinesJ1) {
		ResultatAtkDefMembreDto mb = new ResultatAtkDefMembreDto();
		mb.setPseudo(cleanName(cleanLines.get(numLinesJ1.get(0))));
		mb.setAtkWin(cleanData(cleanLines.get(numLinesJ1.get(1))));
		mb.setAtkDraw(cleanData(cleanLines.get(numLinesJ1.get(2))));
		mb.setAtkLoose(cleanData(cleanLines.get(numLinesJ1.get(3))));
		mb.setDefWin(cleanData(cleanLines.get(numLinesJ1.get(4))));
		mb.setDefDraw(cleanData(cleanLines.get(numLinesJ1.get(5))));
		mb.setDefLoose(cleanData(cleanLines.get(numLinesJ1.get(6))));
		return mb;
	}

	/**
	 * Extraction du nom
	 * @param uncleanPseudo - Le pseudo à nettoyer.
	 * @return Le pseudo
	 */
	private static String cleanName(String uncleanPseudo) {
		String trimPseudo = StringUtils.trim(uncleanPseudo);
		String output = Arrays.stream(trimPseudo.split(" ")).max(Comparator.comparing(String::length)).orElse(trimPseudo);
		logger.log(Level.INFO, () -> INPUT + uncleanPseudo + OUTPUT + output);
		return output;
	}

	/**
	 * Extract only number
	 * @param input - La chaîne de texte à nettoyer.
	 * @return Un nombre
	 */
	private static Integer cleanData(String input) {
		int debutMotCle = StringUtils.indexOf(input, "Victoire");
		if (debutMotCle == -1) {
			debutMotCle = StringUtils.indexOf(input, "Nul");
			if (debutMotCle == -1) {
				debutMotCle = StringUtils.indexOf(input, "Défaite");
				if (debutMotCle == -1) {
					debutMotCle = StringUtils.indexOf(input, "Defaite");
				}
			}
		}
		if (debutMotCle != -1) {
			String extract = input.substring(0, debutMotCle);
			extract = StringUtils.trim(extract);

			final StringBuilder possibleNumber = new StringBuilder();
			boolean digitTrouve = false;

			for (int it = extract.length() - 1; it >= 0; it--) {
				if (' ' == extract.charAt(it) && StringUtils.isNotEmpty(possibleNumber)) {
					break;
				}
				if ('O' == extract.charAt(it)) {
					possibleNumber.insert(0, "0");
					digitTrouve = true;
				}
				else if (digitTrouve && !Character.isDigit(extract.charAt(it))) {
					break;
				}
				else if (Character.isDigit(extract.charAt(it))) {
					possibleNumber.insert(0, "" + extract.charAt(it));
					digitTrouve = true;
				}
			}

			if (StringUtils.isNotBlank(possibleNumber)) {
				logger.log(Level.INFO, () -> INPUT + input + OUTPUT + possibleNumber.toString());
				return Integer.valueOf(possibleNumber.toString());
			}
			else {
				logger.log(Level.WARNING, () -> "Determination impossible de la valeur avec un premier algorithme, tentative avec un second.");
			}
		}

		String temp = input.replaceAll("[^0-9]", "");
		final Integer result = StringUtils.isBlank(temp) ? 0 : Integer.parseInt(temp);
		logger.log(Level.INFO, () -> INPUT + input + OUTPUT + result);

		return result;
	}
}
