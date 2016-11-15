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

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jwrapper.jwutils.JWUpdateApp;

/**
 * This virtual app is called by JWrapper before each update.
 * It prompts the user whether he/she wants to update.
 */
public class UpdateApp extends Application {
	public void start(Stage primaryStage)
	{
		ButtonType update = new ButtonType("Update now");
		ButtonType ignore = new ButtonType("Ignore this update");
		ButtonType later = new ButtonType("Remind me later");

		Alert query = new Alert(AlertType.CONFIRMATION, "A new update has been detected. Would you like to update now?",
				update, ignore, later);
		query.setHeaderText(null);
		query.setTitle("ClearComposer");

		ButtonType response = query.showAndWait().orElse(later);
		if (response == update) //Update now!
			JWUpdateApp.exitJvm_UpdateAppAndRun();
		else if (response == ignore) //Ignore this update
			JWUpdateApp.exitJvm_RunAppWithoutUpdateDontAskAgain();
		else //Remind me later
			JWUpdateApp.exitJvm_RunAppWithoutUpdate();
	}

	public static void main(String[] args)
	{
		Application.launch(args);
	}
}
