package com.bluemriine.aramintha;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.view.MainFrame;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.*;

/** Classe principale de l'application. */
public class Main {

	/** Logger **/
	private static final Logger logger = Logger.getLogger("MyLog");

	/** User directory */
	private static final String USER_DIR = "user.dir";

	/**
	 * Launcher de l'application.
	 * @param args - Nothing special
	 */
	public static void main(String[] args) {
		DataHolder.getInstance().setOutputFolder(System.getProperty(USER_DIR));
		configLogger();
		logger.log(Level.INFO, () -> "Application démarrée dans le dossier : " + System.getProperty(USER_DIR));
		new MainFrame();
	}

	/** Configure le logger. */
	private static void configLogger() {
		try {
			SimpleFormatter simpleFormatter = new SimpleFormatter() {
				private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

				@Override
				public synchronized String format(LogRecord lr) {
					return String.format(FORMAT, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
				}
			};

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			FileHandler fh = new FileHandler(System.getProperty(USER_DIR) + "/Log-" + formatter.format(new Date()) + "-" + UUID.randomUUID().toString() + ".log");
			ConsoleHandler ch = new ConsoleHandler();
			ch.setFormatter(simpleFormatter);
			fh.setFormatter(simpleFormatter);
			logger.setUseParentHandlers(false);
			logger.addHandler(fh);
			logger.addHandler(ch);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
}
