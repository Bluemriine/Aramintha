package com.bluemriine.aramintha.view.interactor;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.swing.*;

/** Singleton permettant l'interaction avec les composants de la view MainFrame. */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@EqualsAndHashCode
public class MainViewComponentInteractor {

	/** Bouton pour lancer l'analyse de la zone. */
	private JButton doAnalyseBouton;
	/** Box de message de l'application. */
	private JButton doExportBouton;
	/** Bouton pour lancer l'export des données. */
	private JTextArea boxMessage;

	/** Le constructeur qui joue à cache-cache. */
	private MainViewComponentInteractor() {
		// Je me cache car je suis un singleton
	}

	/** Classe interne contenant la seule instance de la classe. */
	private static class SingletonHolder {
		private final static MainViewComponentInteractor instance = new MainViewComponentInteractor();
	}

	/** @return l'instance de la classe. */
	public static MainViewComponentInteractor getInstance() {
		return MainViewComponentInteractor.SingletonHolder.instance;
	}

	/**
	 * Définit un nouveau message dans la box de message.
	 * @param message - Le nouveau message.
	 */
	public void setMessage(String message) {
		if (boxMessage != null) {
			boxMessage.setText(message);
		}
	}

	/**
	 * Ajoute un nouveau message dans la box de message.
	 * @param message - Le message à ajouter.
	 */
	public void addMessage(String message) {
		if (message != null) {
			boxMessage.setText(boxMessage.getText() + "\r\n" + message);
		}
	}

	/** Active le bouton pour l'extraction des données. */
	public void activeExtractionBouton() {
		if (doExportBouton != null) {
			doExportBouton.setEnabled(true);
		}
	}

	/** Active le bouton pour l'analyse de la zone de capture. */
	public void activeAnalyseBouton() {
		if (doAnalyseBouton != null) {
			doAnalyseBouton.setEnabled(true);
		}
	}
}
