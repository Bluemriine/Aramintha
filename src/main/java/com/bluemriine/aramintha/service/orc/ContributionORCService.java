package com.bluemriine.aramintha.service.orc;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatContributionMembreDto;
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
 *
 * @author BlueM
 */
public class ContributionORCService extends AbstractORCService {

    /**
     * Logger
     **/
    private static final Logger logger = Logger.getLogger("MyLog");

    /**
     * The constructor !!!
     */
    public ContributionORCService() {
        configureTesseract(4);
    }

    /**
     * Lance l'analyse
     *
     * @param file - Le fichier à analyser.
     */
    @Override
    public void doOCR(File file, Consumer<String> consumerOk, Consumer<String> consumerKo) {
        try {
            List<String> lines = getLines(file);
            List<String> cleanLinesStep1 = lines.stream().filter(line -> StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "Rank")).collect(Collectors.toList());
            List<String> cleanLinesStep2 = cleanLinesStep1.stream().map(line -> {
                if (StringUtils.startsWith(line, "Contribution d'attaque :")) {
                    line = StringUtils.trim(line.substring("Contribution d'attaque :".length(), line.length()));
                }
                if (line.equalsIgnoreCase("o")) {
                    line = "0";
                }
                return line;
            })
                    .filter(line -> StringUtils.isNotBlank(line))
                    .collect(Collectors.toList());

            StringJoiner sj = new StringJoiner("\r\n");
            cleanLinesStep2.forEach(sj::add);
            logger.log(Level.INFO, () -> "Lignes trouvées dans l'image : \r\n" + sj.toString());

            if (cleanLinesStep1.size() % 2 == 0) {
                Map<String, String> mapResult = new HashMap<>();
                List<String> listePseudo = cleanLinesStep2.stream().filter(line -> !canParseToInteger(line)).collect(Collectors.toList());
                List<String> listePoints = cleanLinesStep2.stream().filter(line -> canParseToInteger(line)).collect(Collectors.toList());

                if (listePseudo.size() != listePoints.size()) {
                    logger.log(Level.WARNING, () -> "Erreur, le nombre de lignes est impaire.");
                    consumerKo.accept("Erreur lors de l'analyse de l'image.");
                } else {
                    for (int i = 0; i < listePseudo.size(); i++) {
                        ResultatContributionMembreDto dto = new ResultatContributionMembreDto();
                        dto.setPseudo(listePseudo.get(i));
                        dto.setPoints(Integer.parseInt(listePoints.get(i)));
                        DataHolder.getInstance().getListResultatContributionMembre().add(dto);
                    }
                    consumerOk.accept("Prêt pour la prochaine extraction.");
                }
            } else {
                logger.log(Level.WARNING, () -> "Erreur, le nombre de lignes est impaire.");
                consumerKo.accept("Erreur lors de l'analyse de l'image.");
            }

        } catch (TesseractException ex) {
            logger.log(Level.SEVERE, () -> "Erreur lors de l'analyse de l'image :" + ExceptionUtils.getStackTrace(ex));
            consumerKo.accept("Erreur lors de l'analyse de l'image.");
        }
    }

    /**
     * Retourne si oui ou non il est possible de convertir la chaîne de texte en nombre
     *
     * @param text - La chaîne de texte à tester
     * @return un booléen
     */
    public static boolean canParseToInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Extract only number
     *
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
