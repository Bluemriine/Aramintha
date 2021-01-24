package com.bluemriine.aramintha.view;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.service.CaptureService;
import com.bluemriine.aramintha.service.ExportService;
import com.bluemriine.aramintha.service.ImportListePseudoService;
import com.bluemriine.aramintha.view.interactor.MainViewComponentInteractor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ecran principal de l'application.
 * @author BlueM
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");
	/** Le service de capture */
	private final transient CaptureService captureService;

	/** Bouton pour lancer la sélection de la zone de capture. */
	private JButton defineCaptureZoneBouton;
	/** Box de message de l'application. */
	private JTextArea messageBox;
	/** Bouton pour lancer l'analyse de la zone. */
	private JButton analyseZoneBouton;
	/** Bouton pour lancer l'export des données. */
	private JButton exportBouton;
	/** Bouton pour charger la liste des noms de membres. */
	private JButton importBouton;
	/** Le radio bouton ATK/ DEF */
	private JRadioButton atkDefRadioBouton;

	/** The Constructor !!! */
	public MainFrame() {
		super("Aramintha Guild Wars Data Extractor");
		captureService = new CaptureService();
		initLayout();
		initAction();
		feedViewInteractor();
	}

	/** Initialisation du layout. */
	private void initLayout() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		Font police = new Font("Arial", Font.PLAIN, 11);
		Dimension preferredSize = new Dimension(190, 20);
		int xPos = 173;

		// Création du LayeredPanel.
		Dimension boardSize = new Dimension(390, 240);
		this.setSize(boardSize.width, boardSize.height);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setLocation(50, 40);
		this.setResizable(false);
		this.setVisible(true);
		this.setAlwaysOnTop(true);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(boardSize);
		this.add(layeredPane);

		JPanel background = new JPanel();
		JLabel picLabel = new JLabel(new ImageIcon(this.getClass().getResource("/Aramintha.png")));
		picLabel.setBounds(0, 0, boardSize.width, boardSize.height);
		background.add(picLabel);
		background.setPreferredSize(boardSize);
		background.setBounds(0, 0, boardSize.width, boardSize.height);
		layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

		// Type de capture.
		JLabel typeLabel = new JLabel("Type :");
		typeLabel.setFont(police);
		JPanel boardOption = new JPanel();
		atkDefRadioBouton = new JRadioButton("Atk/Def");
		atkDefRadioBouton.setMnemonic(KeyEvent.VK_A);
		atkDefRadioBouton.setActionCommand("Atk/Def");
		atkDefRadioBouton.setSelected(true);
		atkDefRadioBouton.setFont(police);
		JRadioButton contributionRadioBouton = new JRadioButton("Contribution");
		contributionRadioBouton.setMnemonic(KeyEvent.VK_C);
		contributionRadioBouton.setActionCommand("Contribution");
		contributionRadioBouton.setFont(police);
		contributionRadioBouton.setEnabled(true);
		ButtonGroup group = new ButtonGroup();
		group.add(atkDefRadioBouton);
		group.add(contributionRadioBouton);
		boardOption.add(typeLabel);
		boardOption.add(atkDefRadioBouton);
		boardOption.add(contributionRadioBouton);
		boardOption.setBounds(new Rectangle(new Point(177, 0), boardOption.getPreferredSize()));

		// Bouton "Définir la zone de capture"
		JPanel boardCapture = new JPanel();
		defineCaptureZoneBouton = new JButton("Définir la zone de capture");
		defineCaptureZoneBouton.setPreferredSize(preferredSize);
		defineCaptureZoneBouton.setFont(police);
		boardCapture.add(defineCaptureZoneBouton);
		boardCapture.setBounds(new Rectangle(new Point(xPos, 24), boardCapture.getPreferredSize()));

		// Bouton "Lancer l'analyse de la zone Capture"
		JPanel boardAnalyse = new JPanel();
		analyseZoneBouton = new JButton("Analyser la zone");
		analyseZoneBouton.setPreferredSize(preferredSize);
		analyseZoneBouton.setFont(police);
		analyseZoneBouton.setEnabled(false);
		DataHolder.getInstance().setAnalyseBouton(analyseZoneBouton);
		boardAnalyse.add(analyseZoneBouton);
		boardAnalyse.setBounds(new Rectangle(new Point(xPos, 50), boardAnalyse.getPreferredSize()));

		// Message box
		JPanel boardMessage = new JPanel();
		messageBox = new JTextArea("En attente ...");
		messageBox.setEditable(false);
		messageBox.setFont(police);
		messageBox.setMargin(new Insets(5, 5, 5, 5));
		messageBox.setBackground(Color.BLACK);
		messageBox.setPreferredSize(new Dimension(190, 39));
		messageBox.setForeground(Color.GREEN);
		boardMessage.add(messageBox);
		boardMessage.setBounds(new Rectangle(new Point(xPos, 76), boardMessage.getPreferredSize()));

		// Import
		JPanel boardImport = new JPanel();
		importBouton = new JButton("Importer la liste des membres");
		importBouton.setPreferredSize(preferredSize);
		importBouton.setFont(police);
		importBouton.setEnabled(true);
		boardImport.add(importBouton);
		boardImport.setBounds(new Rectangle(new Point(xPos, 122), boardImport.getPreferredSize()));

		// Export
		JPanel boardExport = new JPanel();
		exportBouton = new JButton("Exporter les données");
		exportBouton.setPreferredSize(preferredSize);
		exportBouton.setFont(police);
		exportBouton.setEnabled(true);
		boardExport.add(exportBouton);
		boardExport.setBounds(new Rectangle(new Point(xPos, 148), boardExport.getPreferredSize()));

		// Signature
		JPanel boardSignature = new JPanel();
		final JLabel signature = new JLabel("v1.1 By Bluem#2481");
		signature.setFont(police);
		signature.setPreferredSize(preferredSize);
		signature.setForeground(Color.GRAY);
		signature.setOpaque(false);
		boardSignature.add(signature);
		boardSignature.setBounds(new Rectangle(new Point(217, 174), boardSignature.getPreferredSize()));

		layeredPane.add(boardOption, Integer.valueOf(100));
		layeredPane.add(boardCapture, Integer.valueOf(200));
		layeredPane.add(boardAnalyse, Integer.valueOf(300));
		layeredPane.add(boardMessage, Integer.valueOf(400));
		layeredPane.add(boardImport, Integer.valueOf(500));
		layeredPane.add(boardExport, Integer.valueOf(600));
		layeredPane.add(boardSignature, Integer.valueOf(700));
	}

	/** Ajoute les composants de la vue avec lesquels on peut interagir. */
	private void feedViewInteractor() {
		MainViewComponentInteractor.getInstance().setBoxMessage(messageBox);
		MainViewComponentInteractor.getInstance().setDoAnalyseBouton(analyseZoneBouton);
		MainViewComponentInteractor.getInstance().setDoExportBouton(exportBouton);
	}

	/** Initialisation des actions. */
	private void initAction() {
		defineCaptureZoneBouton.addActionListener(event -> {
			CaptureFrame captureFrame = new CaptureFrame();
			captureFrame.setVisible(true);
			messageBox.setText("Définition de la zone de capture");
		});

		importBouton.addActionListener(event -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setDialogTitle("Choisir le fichier contenant la liste des membres : ");
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setMultiSelectionEnabled(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt et .csv", "txt", "csv");
			jfc.addChoosableFileFilter(filter);
			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				logger.log(Level.INFO, () ->"Fichier des pseudos sélectionné : " + selectedFile.getAbsolutePath());
				ImportListePseudoService.importListe(selectedFile.getAbsolutePath());
			}
		});

		analyseZoneBouton.addActionListener(event -> {
			messageBox.setText("Traitement en cours...");
			analyseZoneBouton.setEnabled(false);
			if (atkDefRadioBouton.isSelected()) {
				captureService.analyseZoneCaptureAtkDef();
			}
			else {
				captureService.analyseZoneCaptureContribution();
			}
			analyseZoneBouton.setEnabled(true);
		});

		exportBouton.addActionListener(event -> {
			if (atkDefRadioBouton.isSelected()) {
				ExportService.exportAtkDef();
			}
			else {
				ExportService.exportContribution();
			}
		});
	}
}