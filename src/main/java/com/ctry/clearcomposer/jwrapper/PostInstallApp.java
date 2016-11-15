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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class PostInstallApp extends Application {
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	public void start(Stage primaryStage) throws IOException
	{
		OS userOS = Util.getOS();
		//TODO: show end user license agreement for user to accept.
		//TODO: and prompt user whether if he/she wants a desktop icon/ start menu. (Windows only)

		//Create a shortcut on user's desktop. (Windows)
		if (userOS == OS.WINDOWS) {
			JWSystem.saveLauncherShortcutForVirtualApp(new File(System.getProperty("user.home"), "Desktop"),
					"ClearComposer", "ClearComposer", new Properties(), false);
		}

		//TODO: put start-up run analytics right here!

		JWInstallApp.setupAllStandardShortcuts(true);
		JWInstallApp.exitJvm_ContinueAndPerformStandardSetup();
	}
}
