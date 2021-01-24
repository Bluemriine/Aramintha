package com.bluemriine.aramintha.view;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.view.interactor.MainViewComponentInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JFrame pour la capture d'écran.
 * @author BlueM
 */
public class CaptureFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");

	/** Coordonnée X du 1er point de la zone de capture (haut à gauche) */
	private int squareX1 = 0;
	/** Coordonnée Y du 1er point de la zone de capture (haut à gauche) */
	private int squareY1 = 0;
	/** Coordonnée X du 2ème point de la zone de capture (bas à droite) */
	private int squareX2 = 0;
	/** Coordonnée Y du 2ème point de la zone de capture (bas à droite) */
	private int squareY2 = 0;
	/** La popup de validation de la zone */
	JDialog popupValidation;
	/** Le bouton pour valider la zone */
	private JButton validationBouton;
	/** Le bouton pour annuler la zone */
	private JButton annulationBouton;

	/** The constructor !!! */
	public CaptureFrame() {
		super("Définir la zone de capture");
		initLayout();
		initAction();
	}

	/** Initialisation du layout. */
	private void initLayout() {
		Font police = new Font("Arial", Font.PLAIN, 10);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBackground(new Color(0, 0, 0, 0));
		setSize(new Dimension(screenSize.width, screenSize.height));
		setLocationRelativeTo(null);

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				if (g instanceof Graphics2D) {
					final int R = 240;
					final int G = 240;
					final int B = 240;
					Paint p = new GradientPaint(0.0f, 0.0f, new Color(R, G, B, 1), 0.0f, getHeight(), new Color(R, G, B, 5), true);
					Graphics2D g2d = (Graphics2D) g;
					g2d.setPaint(p);
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}
			}
		};

		this.setContentPane(panel);
		this.setLayout(new GridBagLayout());

		popupValidation = new JDialog();
		popupValidation.setTitle("Veuillez confirmer la zone de capture :");
		popupValidation.setLocationRelativeTo(null);
		popupValidation.setVisible(false);
		popupValidation.setModal(true);
		popupValidation.setAlwaysOnTop(true);
		popupValidation.setSize(300, 70);
		popupValidation.setResizable(false);
		popupValidation.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel panelPopupValidation = new JPanel();
		validationBouton = new JButton("Valider");
		validationBouton.setFont(police);
		validationBouton.setPreferredSize(new Dimension(130, 20));

		annulationBouton = new JButton("Annuler");
		annulationBouton.setFont(police);
		annulationBouton.setPreferredSize(new Dimension(130, 20));

		panelPopupValidation.add(validationBouton);
		panelPopupValidation.add(annulationBouton);
		popupValidation.add(panelPopupValidation);

		this.setVisible(true);
	}

	/** Initialisation des actions. */
	private void initAction() {
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (squareX1 == 0) {
						squareX1 = e.getX();
						squareY1 = e.getY();
						MainViewComponentInteractor.getInstance().setMessage("Choisir le second point de la zone.");
					}
					else {
						squareX2 = e.getX();
						squareY2 = e.getY();
						if (squareX1 > squareX2) {
							int tmp = squareX2;
							squareX2 = squareX1;
							squareX1 = tmp;
						}
						if (squareY1 > squareY2) {
							int tmp = squareY2;
							squareY2 = squareY1;
							squareY1 = tmp;
						}
						logger.log(Level.INFO, () ->"Zone de capture définie.");
						MainViewComponentInteractor.getInstance().setMessage("Zone de capture définie.");
						popupValidation.setVisible(true);
					}
				}
				else if (SwingUtilities.isRightMouseButton(e)) {
					squareX1 = squareY1 = squareX2 = squareY2 = 0;
					logger.log(Level.INFO, () ->"Zone de capture précédente restaurée.");
					MainViewComponentInteractor.getInstance().setMessage("Zone de capture précédente restaurée.");
				}
				repaint();
			}
		});

		validationBouton.addActionListener(click -> {
			popupValidation.dispose();
			this.dispose();
			feedViewInteractor();
			MainViewComponentInteractor.getInstance().activeAnalyseBouton();
		});

		annulationBouton.addActionListener(click -> {
			popupValidation.dispose();
			squareX1 = squareY1 = squareX2 = squareY2 = 0;
			repaint();
		});
	}

	/** Ajoute les composants de la vue avec lesquels on peut interagir. */
	private void feedViewInteractor() {
		DataHolder.getInstance().setSquareX1(squareX1);
		DataHolder.getInstance().setSquareY1(squareY1);
		DataHolder.getInstance().setSquareX2(squareX2);
		DataHolder.getInstance().setSquareY2(squareY2);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.RED);
		if (squareX1 > 0 && squareX2 == 0) {
			g.fillRect(squareX1, squareY1, 5, 5);
		}
		if (squareX2 > 0) {
			g.drawRect(squareX1, squareY1, squareX2 - squareX1, squareY2 - squareY1);
		}
	}
}
