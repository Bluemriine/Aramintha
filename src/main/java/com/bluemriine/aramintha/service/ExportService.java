package com.bluemriine.aramintha.service;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatAtkDefMembreDto;
import com.bluemriine.aramintha.data.ResultatContributionMembreDto;
import com.bluemriine.aramintha.data.ResultatMembreDto;
import com.bluemriine.tenebria.ImportDataContribution;
import com.bluemriine.tenebria.ImportDataGvG;
import com.bluemriine.tenebria.ImportResultatConfrontationMembre;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service d'export des données.
 *
 * @author BlueM
 */
public class ExportService {

    /**
     * The constructor !!!
     */
    private ExportService() {
        // Je joue à cache cache
    }

    /**
     * Logger
     **/
    private static final Logger logger = Logger.getLogger("MyLog");


    /**
     * Exporte les données vers le presse-papier
     */
    public static void exportAtkDef() {
        Function<String, ? extends ResultatMembreDto> construct = param -> {
            ResultatAtkDefMembreDto vRet = new ResultatAtkDefMembreDto();
            vRet.setPseudo(param);
            return vRet;
        };
        export(DataHolder.getInstance().getListResultatAtkDefMembre(), construct, "Membre, Atk Win, Atk Draw, Atk Loose, Def Win, Def Draw, Def Loose");
    }

    /**
     * Exporte les données vers le presse-papier
     */
    public static void exportContribution() {
        Function<String, ? extends ResultatMembreDto> construct = param -> {
            ResultatContributionMembreDto vRet = new ResultatContributionMembreDto();
            vRet.setPseudo(param);
            return vRet;
        };
        export(DataHolder.getInstance().getListResultatContributionMembre(), construct, "Membre, Points");
    }

    /**
     * Exporte les données vers le presse-papier
     */
    private static void export(Set<? extends ResultatMembreDto> inputListe, Function<String, ? extends ResultatMembreDto> construct, String entete) {
        final List<ResultatMembreDto> listeAExtraire = new LinkedList<>();
        if (!CollectionUtils.isEmpty(DataHolder.getInstance().getListePseudo())) {
            DataHolder.getInstance().getListePseudo().forEach(nom -> {
                Optional<? extends ResultatMembreDto> first = inputListe.stream().filter(resultat -> StringUtils.equalsIgnoreCase(resultat.getPseudo().split(" ")[0], nom)).findFirst();
                ResultatMembreDto mb;
                if (first.isPresent()) {
                    mb = first.get();
                } else {
                    mb = construct.apply(nom);
                }
                listeAExtraire.add(mb);
            });
            inputListe.stream().filter(result -> !DataHolder.getInstance().getListePseudo().contains(result.getPseudo().split(" ")[0])).forEach(listeAExtraire::add);
        } else {
            listeAExtraire.addAll(inputListe);
        }
        List<ImportResultatConfrontationMembre> listeAtkDef = new ArrayList<>();
        List<Pair<String, Integer>> listeContribution = new ArrayList<>();

        StringBuilder sb = new StringBuilder(entete).append("\r\n");
        listeAExtraire.forEach(item -> {
            sb.append(item.toString()).append("\r\n");
            if (item instanceof ResultatAtkDefMembreDto) {
                ResultatAtkDefMembreDto result = (ResultatAtkDefMembreDto) item;
                listeAtkDef.add(ImportResultatConfrontationMembre.builder()
                        .pseudo(result.getPseudo())
                        .atkWin(result.getAtkWin())
                        .atkDraw(result.getAtkDraw())
                        .atkLoose(result.getAtkLoose())
                        .defWin(result.getDefWin())
                        .defDraw(result.getDefDraw())
                        .defLoose(result.getDefLoose())
                        .build());
            } else if (item instanceof ResultatContributionMembreDto) {
                ResultatContributionMembreDto result = (ResultatContributionMembreDto) item;
                listeContribution.add(Pair.of(result.getPseudo(), result.getPoints()));
            }
        });

        logger.log(Level.INFO, sb::toString);
        StringSelection selection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        String id = UUID.randomUUID().toString();
        final String pathCsv = DataHolder.getInstance().getOutputFolder() + "\\ExportDatas-" + id + ".csv";
        logger.log(Level.INFO, () -> "Fichier exporté : " + pathCsv);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathCsv, true))) {
            writer.append(sb.toString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        final String pathJson = DataHolder.getInstance().getOutputFolder() + "\\ExportDatas-" + id + ".json";
        logger.log(Level.INFO, () -> "Fichier exporté : " + pathJson);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathJson, true))) {
            ObjectMapper Obj = new ObjectMapper();
            if (CollectionUtils.isNotEmpty(listeAtkDef)) {
                ImportDataGvG importDataGvG = new ImportDataGvG();
                importDataGvG.setListResultatsIndividuel(listeAtkDef);
                writer.append(Obj.writerWithDefaultPrettyPrinter().writeValueAsString(importDataGvG));
            } else {
                ImportDataContribution importData = new ImportDataContribution();
                importData.setListContribution(listeContribution);
                writer.append(Obj.writerWithDefaultPrettyPrinter().writeValueAsString(importData));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
