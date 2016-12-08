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

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.jwrapper.Util.OS;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import jwrapper.jwutils.JWInstallApp;
import jwrapper.jwutils.JWSystem;
import jwrapper.jwutils.JWWindowsOS;
import jwrapper.jwutils.JWWindowsRegistry;
import jwrapper.updater.GenericUpdater;

import java.io.*;
import java.util.prefs.Preferences;

/**
 * This virtual app is called by JWrapper after the
 * first time the user runs this app on a computer.
 */
public class PostInstallApp extends Application {

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	private static void copy(File source, File dest) throws IOException {
		try (InputStream is = new FileInputStream(source);
		     OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
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

	//UI Components
	private VBox root;
	private Pane main;
	private Button btnPrev;
	private Button btnNext;

	private Pane[] paneSteps;

	private Stage primaryStage;

	//States
	private int step;
	private BooleanProperty fileAssociation = new SimpleBooleanProperty();
	private BooleanProperty appShortcut = new SimpleBooleanProperty();
	private BooleanProperty desktopShortcut = new SimpleBooleanProperty();
	private BooleanProperty analytics = new SimpleBooleanProperty();
	private BooleanProperty autoUpdate = new SimpleBooleanProperty();

	public void init()
	{

		//Main Panel
		main = new StackPane();
		main.setPrefSize(600, 400);
		main.getChildren().add(new Pane());

		//Button Panel
		Separator buttonSep = new Separator();
		buttonSep.setPadding(new Insets(5));

		btnPrev = new Button("< Back");
		btnPrev.setPrefWidth(100);
		btnPrev.setOnAction(evt -> {
			step--;
			updatePosition();
		});
		btnNext = new Button("Next >");
		btnNext.setPrefWidth(100);
		btnNext.setOnAction(evt -> {
			step++;
			updatePosition();
		});
		Pane padding = new Pane();
		padding.setPadding(new Insets(10));
		Button btnCancel = new Button("Cancel");
		btnCancel.setPrefWidth(100);
		btnCancel.setOnAction(evt -> cancelCommand());

		HBox buttonsPane = new HBox(2);
		buttonsPane.setPadding(new Insets(0, 10, 0, 10));
		buttonsPane.setAlignment(Pos.CENTER_RIGHT);
		buttonsPane.getChildren().addAll(btnPrev, btnNext, padding, btnCancel);

		//Root
		root = new VBox();
		//root.setPadding(new Insets(10));
		root.getChildren().addAll(main, buttonSep, buttonsPane);

		initSteps();
		updatePosition();
	}

	public void start(Stage primaryStage)
	{
		this.primaryStage = primaryStage;

		Scene s = new Scene(root);
		primaryStage.setTitle("ClearComposer Wizard");
		primaryStage.setScene(s);
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(evt -> {
			cancelCommand();
			evt.consume();
		});

		btnNext.requestFocus();
	}

	public void updatePosition()
	{
		if (step == paneSteps.length)
		{
			try {
				primaryStage.close();
				finished();
			} catch (IOException e) {
				e.printStackTrace();
				Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while installing...", ButtonType.OK);
				dlg.setHeaderText(null);
				dlg.setTitle("ClearComposer");
				dlg.initOwner(primaryStage);
				dlg.showAndWait();
			}
		}
		else
		{
			btnNext.setText(step >= paneSteps.length - 1 ? "Finish" : "Next >");
			btnPrev.setDisable(step <= 0);
			main.getChildren().set(0,paneSteps[step]);
			main.layout();
		}

	}

	public void finished() throws IOException
	{
		if (appShortcut.get())
			JWInstallApp.setupAllStandardShortcuts(true);

		if (desktopShortcut.get()) {
			//Create a shortcut on user's desktop.
			JWWindowsOS command = new JWWindowsOS();
			File desktop = new File(System.getProperty("user.home"), "Desktop");
			File[] shortcuts = command.getAppStartMenuFolder().listFiles();
			if (shortcuts == null)
				shortcuts = new File[0];
			for (File f : shortcuts)
			{
				if (!f.getName().toLowerCase().contains("uninstall"))
					copy(f, new File(desktop, f.getName()));
			}
		}

		//Create file association.
		if (fileAssociation.get())
			addFileAssociation(".ccp", "ClearComposer Piece", "JWVAPP", "ClearComposer", "JW_OpenFile=%1");

		Preferences settings = Preferences.userNodeForPackage(ClearComposer.class);
		settings.putBoolean("Analytics", analytics.get());
		settings.putBoolean("AutoUpdate", autoUpdate.get());

		JWInstallApp.exitJvm_ContinueAndPerformStandardSetup();
	}

	private void cancelCommand()
	{
		Alert dlg = new Alert(Alert.AlertType.WARNING, "This would cancel the installation of ClearComposer. Are you sure you want to cancel?",
				ButtonType.YES, ButtonType.NO);
		dlg.setHeaderText(null);
		dlg.setTitle("ClearComposer");
		dlg.initOwner(primaryStage);
		ButtonType resp = dlg.showAndWait().orElse(ButtonType.CANCEL);
		if (resp == ButtonType.YES)
			JWInstallApp.exitJvm_QuitInstallAndRollBack();
	}

	private void initSteps()
	{
		//Step 1
		HBox pnlWelcome = new HBox();
		{
			ImageView imgSideBar = new ImageView(ClearComposer.loadImage("jwrapper/SideBar.png"));
			StackPane pnlSideBar = new StackPane(imgSideBar);
			pnlSideBar.setBackground(colorBack(Color.WHITE));
			pnlSideBar.setMaxHeight(Region.USE_PREF_SIZE);

			Label lblWelcome = new Label("Welcome to the ClearComposer Installation Wizard!");
			lblWelcome.setWrapText(true);
			lblWelcome.setMaxWidth(Double.MAX_VALUE);
			lblWelcome.setPadding(new Insets(10, 10, 20, 10));
			lblWelcome.setFont(Font.font("", FontWeight.BOLD, 18));

			Label lblGreetings = new Label("This setup wizard will configure the various options for ClearComposer. "
					+ "We hope you will enjoy this little program, and we thank you for your interest!");
			lblGreetings.setWrapText(true);
			lblGreetings.setPadding(new Insets(0, 10, 10, 10));

			Label lblInstructions = new Label("Click the 'Next' button to continue or 'Cancel' to exit setup.");
			lblInstructions.setWrapText(true);
			lblInstructions.setPadding(new Insets(0, 10, 10, 10));

			VBox pnlStart = new VBox(lblWelcome, lblGreetings, lblInstructions);
			pnlStart.setBackground(colorBack(Color.WHITE));

			pnlWelcome.getChildren().addAll(pnlSideBar, new Separator(Orientation.VERTICAL), pnlStart);
		}

		//Step 2
		VBox pnlLicense = new VBox();
		{
			VBox pnlMain = new VBox(10);
			Label lblInstructions = new Label("Please read the following license, and select 'I agree' if you agree "
					+ "with the following.");
			lblInstructions.setWrapText(true);
			TextArea txtLicense = new TextArea(loadLicense());
			txtLicense.setEditable(false);
			VBox.setVgrow(txtLicense, Priority.ALWAYS);

			ToggleGroup tgpLicense = new ToggleGroup();
			RadioButton optAgree = new RadioButton("I agree the license.");
			RadioButton optDisagree = new RadioButton("I disagree the license.");
			VBox pnlLicenseAgree = new VBox(5, optAgree, optDisagree);
			pnlLicenseAgree.setPadding(new Insets(5));

			optAgree.setToggleGroup(tgpLicense);
			optDisagree.setToggleGroup(tgpLicense);
			tgpLicense.selectToggle(optDisagree);

			btnNext.disableProperty().bind(tgpLicense.selectedToggleProperty().isEqualTo(optDisagree)
					.and(pnlLicense.parentProperty().isEqualTo(main)));

			pnlMain.getChildren().addAll(lblInstructions, txtLicense, pnlLicenseAgree);
			initStep(pnlLicense, pnlMain);
		}

		//Step 3
		VBox pnlOptions = new VBox();
		{
			OS userOS = Util.getOS();
			VBox pnlMain = new VBox(10);

			Label lblInstructions = new Label("Please read the following options and select those you want:");
			lblInstructions.setWrapText(true);

			VBox pnlChecks = new VBox(10);
			pnlChecks.setPadding(new Insets(10));

			CheckBox chkFileAssociation = new CheckBox("Associate .cpp files to open with ClearComposer.");
			CheckBox chkShortcut = new CheckBox("Include shortcuts.");
			CheckBox chkDesktopShortcut = new CheckBox("Include Desktop shortcut.");
			CheckBox chkAnalytics = new CheckBox("Allow us to collect some anonymous data to better your experience.");
			CheckBox chkUpdates = new CheckBox("Automatically check and install any updates. (If not checked, "
					+ "we will prompt you for each new update.)");

			chkUpdates.setWrapText(true);
			chkUpdates.setAlignment(Pos.TOP_LEFT);
			chkAnalytics.setSelected(true);
			chkDesktopShortcut.disableProperty().bind(chkShortcut.selectedProperty().not());

			VBox.setMargin(chkDesktopShortcut, new Insets(0, 0, 0, 20));

			fileAssociation.bind(chkFileAssociation.selectedProperty());
			appShortcut.bind(chkShortcut.selectedProperty());
			desktopShortcut.bind(chkDesktopShortcut.selectedProperty().and(chkShortcut.selectedProperty()));
			analytics.bind(chkAnalytics.selectedProperty());
			autoUpdate.bind(chkUpdates.selectedProperty());

			pnlChecks.getChildren().addAll(chkFileAssociation, chkShortcut, chkDesktopShortcut, chkAnalytics, chkUpdates);
			if (userOS != OS.WINDOWS) {
				pnlChecks.getChildren().remove(chkDesktopShortcut); //TODO: need desktop shortcut on Mac?
				pnlChecks.getChildren().remove(chkFileAssociation);
			}

			pnlMain.getChildren().addAll(lblInstructions, pnlChecks);
			initStep(pnlOptions, pnlMain);
		}

		paneSteps = new Pane[] {pnlWelcome, pnlLicense, pnlOptions};
	}

	private String loadLicense()
	{
		try (InputStream is = ClearComposer.class.getResourceAsStream("jwrapper/LICENSE.txt"))
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(is.available());
			byte[] buffer = new byte[8192];
			int read;
			while ((read = is.read(buffer)) != -1)
				baos.write(buffer, 0, read);
			return baos.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Alert dlg = new Alert(AlertType.ERROR, "Unable to load license. Canceling installation now.", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(primaryStage);
			dlg.showAndWait();
			JWInstallApp.exitJvm_QuitInstallAndRollBack();
			throw new Error();
		}
	}

	private static void initStep(VBox pnlStep, Pane pnlMain)
	{
		ImageView imgBanner = new ImageView(ClearComposer.loadImage("jwrapper/Banner-Small.png"));
		StackPane pnlBanner = new StackPane(imgBanner);
		pnlBanner.setBackground(colorBack(Color.WHITE));
		pnlBanner.setMaxHeight(Region.USE_PREF_SIZE);
		pnlBanner.setPadding(new Insets(10));
		VBox.setMargin(pnlMain, new Insets(10));
		VBox.setVgrow(pnlMain, Priority.ALWAYS);
		pnlStep.getChildren().addAll(pnlBanner, new Separator(), pnlMain);
	}

	private static Background colorBack(Color fill) {
		return new Background(new BackgroundFill(fill, null, null));
	}
}
