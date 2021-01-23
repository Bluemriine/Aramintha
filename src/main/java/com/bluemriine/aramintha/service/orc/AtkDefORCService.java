package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatAtkDefMembreDto;
import com.bluemriine.aramintha.service.orc.util.ImageUtils;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Extraction des données ATK / DEF
 * @author BlueM
 */
public class AtkDefORCService extends AbstractORCService {

	/**
	 * The constructor !!!
	 */
	public AtkDefORCService() {
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
				cleanLines.forEach(System.out::println);
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
						System.out.println("Erreur dans la lecture des lines :");
						cleanLines.forEach(System.out::println);
						consumerKo.accept("Erreur lors de l'analyse de l'image.");
						break;
				}
			}
		} catch (TesseractException ex) {
			System.out.println(ExceptionUtils.getStackTrace(ex));
			consumerKo.accept("Erreur lors de l'analyse de l'image.");
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
	 * @return
	 */
	private static String cleanName(String uncleanPseudo) {
		String trimPseudo = StringUtils.trim(uncleanPseudo);
		return Arrays.asList(trimPseudo.split(" ")).stream().max(Comparator.comparing(String::length)).orElse(trimPseudo);
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

			String possibleNumber = "";
			boolean digitTrouve = false;

			for (int it = extract.length() - 1; it >= 0; it--) {
				if (' ' == extract.charAt(it) && StringUtils.isNotEmpty(possibleNumber)) {
					break;
				}
				if ('O' == extract.charAt(it)) {
					possibleNumber = "0" + possibleNumber;
					digitTrouve = true;
				}
				else if (digitTrouve && !Character.isDigit(extract.charAt(it))) {
					break;
				}
				else if (Character.isDigit(extract.charAt(it))) {
					possibleNumber = "" + extract.charAt(it) + possibleNumber;
					digitTrouve = true;
				}
			}

			if (StringUtils.isNotBlank(possibleNumber)) {
				System.out.println("Input : " + input + " trouvé : " + possibleNumber);
				return Integer.valueOf(possibleNumber);
			}
			else {
				System.out.println("Impossible de trouver une data, tentative avec un second algorithme (moins fiable).");
			}
		}

		String temp;
		temp = input.replaceAll("[^0-9]", "");
		temp = StringUtils.isBlank(temp) ? "0" : temp;

		System.out.println("Input : " + input + " trouvé : " + temp);
		return Integer.valueOf(temp);
	}
}
