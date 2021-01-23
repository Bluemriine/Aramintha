package com.bluemriine.aramintha;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.view.MainFrame;

import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/** Classe principale de l'application. */
public class Main {

	/**
	 * Launcher de l'application.
	 * @param args - Nothing special
	 */
	public static void main(String[] args) throws URISyntaxException {
		DataHolder.getInstance().setOutputFolder(System.getProperty("user.dir"));
		new MainFrame();
	}
}
