package com.bluemriine.aramintha.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.swing.*;
import java.util.*;

/**
 * Singleton qui permet de conserver les données lues par ORC.
 * @author BlueM
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DataHolder {
	/** OutputFolder */
	private String outputFolder;
	/** Liste des résultats de contribution */
	private Set<ResultatContributionMembreDto> listResultatContributionMembre = new HashSet<>();
	/** Liste des résultats ATK / DEF */
	private Set<ResultatAtkDefMembreDto> listResultatAtkDefMembre = new HashSet<>();
	/** Liste de pseudo ordonnés pour l'export des données */
	private List<String> listePseudo = new LinkedList<>();
	/** Chemin vers le données extraites de Tesseract */
	private String pathTesseract = "";
	/** Coordonnée X du 1er point de la zone de capture (haut à gauche) */
	private int squareX1 = 0;
	/** Coordonnée Y du 1er point de la zone de capture (haut à gauche) */
	private int squareY1 = 0;
	/** Coordonnée X du 2ème point de la zone de capture (bas à droite) */
	private int squareX2 = 0;
	/** Coordonnée Y du 2ème point de la zone de capture (bas à droite) */
	private int squareY2 = 0;
	/** Bouton pour lancer l'analyse de la zone. */
	private JButton analyseBouton;

	/** Le constructeur qui joue à cache-cache. */
	private DataHolder() {
		// Je me cache car je suis un singleton
	}

	/** Classe interne contenant la seule instance de la classe. */
	private static class SingletonHolder {
		private final static DataHolder instance = new DataHolder();
	}

	/** @return l'instance de la classe. */
	public static DataHolder getInstance() {
		return SingletonHolder.instance;
	}
}