/*
 * MIT License
 *
 * Copyright (c) 2016 Gahwon "creativitRy" Lee and Henry "theKidOfArcrania" Wang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ctry.clearcomposer.jwrapper;

import com.ctry.clearcomposer.jwrapper.Util.OS;
import javafx.application.Application;
import javafx.stage.Stage;
import jwrapper.jwutils.JWInstallApp;
import jwrapper.jwutils.JWSystem;
import jwrapper.jwutils.JWWindowsOS;
import jwrapper.jwutils.JWWindowsRegistry;
import jwrapper.updater.GenericUpdater;

import java.io.*;

public class PostInstallApp extends Application {
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	private static void copy(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	private static void addFileAssociation(String ext, String name, String... args)
	{
		StringBuilder argString = new StringBuilder();
		for (String a : args)
			argString.append("\" \"").append(a);
		argString.append("\"");

		File master = JWSystem.getAllAppVersionsSharedFolder().getParentFile();
		String app = JWSystem.getAppBundleName();
		File launcher = new File(master, GenericUpdater.getLauncherNameFor(app, false, false, false, false));
		String extKey = ext.substring(1) + "file";
		JWWindowsRegistry.deleteKey("HKEY_CURRENT_USER", "HKEY_CURRENT_USER\\Software\\Classes", ext);
		JWWindowsRegistry.deleteKey("HKEY_CURRENT_USER", "HKEY_CURRENT_USER\\Software\\Classes", extKey);
		JWWindowsRegistry.regCreateKey("HKEY_CURRENT_USER\\Software\\Classes\\" + ext);
		JWWindowsRegistry.regCreateKey("HKEY_CURRENT_USER\\Software\\Classes\\" + extKey + "\\DefaultIcon");
		JWWindowsRegistry.regCreateKey("HKEY_CURRENT_USER\\Software\\Classes\\" + extKey + "\\shell\\open\\command");
		JWWindowsRegistry.regSet("HKEY_CURRENT_USER", "Software\\Classes\\" + ext, "", extKey, "REG_SZ");
		JWWindowsRegistry.regSet("HKEY_CURRENT_USER", "Software\\Classes\\" + extKey, "", name, "REG_SZ");
		JWWindowsRegistry.regSet("HKEY_CURRENT_USER", "Software\\Classes\\" + extKey + "\\DefaultIcon", "",
				launcher.getAbsolutePath() + ",0", "REG_SZ");
		JWWindowsRegistry.regSet("HKEY_CURRENT_USER", "Software\\Classes\\" + extKey + "\\shell\\open\\command", "", "\""
				+ launcher.getAbsolutePath() + argString, "REG_SZ");
	}

	public void start(Stage primaryStage) throws IOException
	{
		OS userOS = Util.getOS();
		//TODO: show end user license agreement for user to accept.
		//TODO: and prompt user whether if he/she wants a desktop icon/ start menu. (Windows only)

		//TODO: put start-up run analytics right here!

		JWInstallApp.setupAllStandardShortcuts(true);

		if (userOS == OS.WINDOWS) {
			//Create a shortcut on user's desktop.
			JWWindowsOS command = new JWWindowsOS();
			File desktop = new File(System.getProperty("user.home"), "Desktop");
			File[] shortcuts = command.getAppStartMenuFolder().listFiles();
			for (File f : shortcuts)
			{
				if (!f.getName().toLowerCase().contains("uninstall"))
					copy(f, new File(desktop, f.getName()));
			}

			//Create file association.
			addFileAssociation(".ccp", "ClearComposer Piece", "JWVAPP", "ClearComposer", "JW_OpenFile=%1");
		}

		JWInstallApp.exitJvm_ContinueAndPerformStandardSetup();
	}
}
