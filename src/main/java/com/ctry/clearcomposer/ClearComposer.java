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

/**
 * Main class
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.ctry.clearcomposer.music.*;
import com.ctry.clearcomposer.sequencer.BassNotesTrack;
import com.ctry.clearcomposer.sequencer.BeatTrack;
import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.NotesTrack;

import javafx.animation.Animation.Status;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ClearComposer extends Application
{
	public static final int DEFAULT_WIDTH = 830;
	public static final int DEFAULT_HEIGHT = 720;

	public static String DEFAULT_FOLDER_HOME = System.getProperty("user.home");

	/**
	 * Constants
	 */
	public static MusicConstants constants = new MusicConstants();

	/**
	 * if true, change on to off and off to on. if false, left click is on and right click is off.
	 */
	private static boolean toggle = true;
	/**
	 * what kind of on? true = permanent, false = temporary
	 */
	private static boolean perma = true;

	/**
	 * file opened currently or null
	 */
	private static File openFile = null;

	/**
	 * the buttons to change chords
	 */
	private StackPane chordButtons;

	/**
	 * buttons of chords
	 */
	private List<CCButton> chords;

	/**
	 * true if shift is pressed
	 */
	private boolean isShift = false;

	/**
	 * plays music and keeps track of note/beat tracks
	 */
	private TrackPlayer player;

	/**
	 * Main pane
	 */
	private BorderPane pane;

	/**
	 * Main javafx method
	 *
	 * @param primaryStage main stage
	 * @throws Exception exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		pane = new BorderPane();
		pane.getStyleClass().add("bg");

		//Toolbar buttons
		Toolbar bar = new Toolbar();
		bar.addRegularButton("New", () ->
		{
			openFile = null;
			//TODO: load default music constants.
			createMusicSequencer();
		});
		bar.addRegularButton("Open", () ->
		{
			File open = showFileChooser(true);
			//TODO: ask if the user wants to save
			if (open != null)
			{
				loadData(open);
				openFile = open;
				((Stage)pane.getScene().getWindow()).setTitle("ClearComposer - " + openFile.getAbsolutePath());
			}
		});
		bar.addRegularButton("Save", () ->
		{
			File save;
			if (openFile == null)
			{
				save = showFileChooser(false);
				if (save == null)
					return;
				openFile = save;
				((Stage)pane.getScene().getWindow()).setTitle("ClearComposer - " + openFile.getAbsolutePath());
			}
			else
				save = openFile;

			saveData(openFile);
		});
		bar.addRegularButton("Save As", () ->
		{
			File save = showFileChooser(false);
			if (save == null)
				return;
			openFile = save;
			((Stage)pane.getScene().getWindow()).setTitle("ClearComposer - " + openFile.getAbsolutePath());
			saveData(openFile);

		});

		bar.addSeparator();
		bar.addRegularButton("Undo", () -> System.out.println("TODO"));
		bar.addRegularButton("Redo", () -> System.out.println("TODO"));
		bar.addSeparator();

		BooleanProperty pauseToggle = new SimpleBooleanProperty(false);
		ToolbarButton btnPlay = bar.addButton("Play");
		ToolbarButton btnPause = bar.addToggleButton("Pause", pauseToggle, (pressed) -> {
			if (pressed)
				player.play();
			else
			{
				btnPlay.setButtonPressed(true); //In case user presses pause first.
				if (player.getPlayState() == Status.RUNNING)
					player.pause();
			}
		});
		ToolbarButton btnStop = bar.addRegularButton("Stop", () -> {
			btnPlay.setButtonPressed(false);
			btnPause.setButtonPressed(false);
			pauseToggle.setValue(false);
			player.stop();
		});

		btnPlay.setOnMousePressed(evt -> btnPlay.setButtonPressed(true));
		btnPlay.setOnMouseClicked(evt -> {
			if (!btnPause.isButtonPressed() && player.getPlayState() != Status.RUNNING) //Only play if we are stopped
			{
				btnPlay.setButtonPressed(true);
				player.play();
			}
		});
		//btnPlay.setButtonPressed(true);

		bar.addSeparator();
		bar.addComboBox((observable, oldValue, newValue) -> setKey(newValue), "Key",
			constants.getKey().ordinal(), Key.values());
		bar.addComboBox((observable, oldValue, newValue) ->
			setNumNotes(Integer.parseInt(newValue.replaceAll("\\D", ""))),"Number of Notes", 1, new String[]{"12 Notes", "16 Notes"});

		pane.setTop(bar);

		//Music sequencer
		createMusicSequencer();

		//Chord buttons
		HBox primaryChords = new HBox(10);
		HBox secondaryChords = new HBox(10);
		VBox chordRows = new VBox(10);
		secondaryChords.setAlignment(Pos.CENTER);
		chordRows.getChildren().addAll(primaryChords, secondaryChords);
		chordRows.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		chordButtons = new StackPane();
		chordButtons.setPadding(new Insets(10));
		chordButtons.getStyleClass().add("panel");
		chordButtons.getChildren().add(chordRows);
		chords = new ArrayList<>();
		for (Chord c : Chord.values())
		{
			CCButton button = new CCButton(c.toString(), c.getColor());
			button.setMinSize(100, 35);
			button.setPrefSize(100, 35);
			button.setMaxSize(100, 35);
			button.setOnMousePressed(evt ->
			{
				if (button.isButtonPressed())
					return;
				button.setButtonPressed(true);
				chords.forEach(btn ->
				{
					if (btn != button)
						btn.setButtonPressed(false);
				});
				setChord(c);
			});
			if (c == constants.getChord())
				button.setButtonPressed(true);

			chords.add(button);
			if (c.isSecondary())
				secondaryChords.getChildren().add(button);
			else
				primaryChords.getChildren().add(button);
		}
		setChord(constants.getChord());
		pane.setBottom(chordButtons);

		//scene
		Scene scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		//allow dragging mouse to trigger notes
		scene.setOnDragDetected(t -> scene.startFullDrag());
		//keyboard shortcuts for chords
		scene.setOnKeyPressed(t ->
		{
			//secondary
			if (isShift)
			{
				if (t.getCode() == KeyCode.DIGIT2 || t.getCode() == KeyCode.NUMPAD2)
					setChord(Chord.V_ii);
				else if (t.getCode() == KeyCode.DIGIT3 || t.getCode() == KeyCode.NUMPAD3)
					setChord(Chord.V_iii);
				else if (t.getCode() == KeyCode.DIGIT4 || t.getCode() == KeyCode.NUMPAD4)
					setChord(Chord.V_IV);
				else if (t.getCode() == KeyCode.DIGIT5 || t.getCode() == KeyCode.NUMPAD5)
					setChord(Chord.V_V);
				else if (t.getCode() == KeyCode.DIGIT6 || t.getCode() == KeyCode.NUMPAD6)
					setChord(Chord.V_vi);
			}
			else
			{
				if (t.getCode() == KeyCode.SHIFT)
					isShift = true;
				else if (t.getCode() == KeyCode.DIGIT1 || t.getCode() == KeyCode.NUMPAD1)
					setChord(Chord.I);
				else if (t.getCode() == KeyCode.DIGIT2 || t.getCode() == KeyCode.NUMPAD2)
					setChord(Chord.ii);
				else if (t.getCode() == KeyCode.DIGIT3 || t.getCode() == KeyCode.NUMPAD3)
					setChord(Chord.iii);
				else if (t.getCode() == KeyCode.DIGIT4 || t.getCode() == KeyCode.NUMPAD4)
					setChord(Chord.IV);
				else if (t.getCode() == KeyCode.DIGIT5 || t.getCode() == KeyCode.NUMPAD5)
					setChord(Chord.V);
				else if (t.getCode() == KeyCode.DIGIT6 || t.getCode() == KeyCode.NUMPAD6)
					setChord(Chord.vi);
				else if (t.getCode() == KeyCode.DIGIT7 || t.getCode() == KeyCode.NUMPAD7)
					setChord(Chord.vii$);
			}
		});
		scene.setOnKeyReleased(t ->
		{
			if (t.getCode() == KeyCode.SHIFT)
				isShift = false;
		});
		//signal end of toggling notes
		scene.setOnMouseReleased(t ->
		{
			if (toggle)
				GraphicNote.stopToggle();
		});
		//css
		scene.getStylesheets().add(ClearComposer.class.getResource("clearcomposer.css").toExternalForm());

		//configure main stage
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("ClearComposer");
		primaryStage.setOnCloseRequest(e ->
		{
			MusicPlayer.turnOffNotes();
			Platform.exit();
		});
		primaryStage.show();
	}

	private void createMusicSequencer() {
		if (player != null)
			player.stop();
		player = new TrackPlayer();
		VBox tracksDisplay = new VBox();
		tracksDisplay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		tracksDisplay.setAlignment(Pos.CENTER);
		tracksDisplay.getStyleClass().add("bg");
		tracksDisplay.setPadding(new Insets(0, 100, 0, 0));
		for (int i = MusicConstants.TRACK_AMOUNT - 1; i >= 0; i--)
		{
			player.getTracks().add(0, new NotesTrack(i / 5, i % 5));
			tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		}
		player.getTracks().add(0, new BeatTrack());
		tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		player.getTracks().add(0, new BassNotesTrack());
		tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		pane.setCenter(tracksDisplay);
	}

	/**
	 * When a chord button is pressed,
	 *
	 * @param c chord chosen
	 */
	public void setChord(Chord c)
	{
		constants.setChord(c);
		for (Node n : chordButtons.getChildren())
		{
			if (!(n instanceof Button))
				continue;

			Button b = (Button) n;
			b.setDisable(b.getStyleClass().get(0).equals(c.name()));

		}

		updateTracks();
	}

	/**
	 * Sets key to new key
	 * @param key new key
	 */
	public void setKey(Key key)
	{
		constants.setKey(key);
		updateTracks();
	}

	/**
	 * Sets number of notes to new number of notes
	 * @param numNotes new number of notes
	 */
	public void setNumNotes(int numNotes)
	{
		//TODO
	}

	private void updateTracks()
	{
		// iterate over all note tracks
		for (int i = 2; i < player.getTracks().size(); i++)
		{
			((NotesTrack) player.getTracks().get(i)).updateTrack();
		}

		((BassNotesTrack) player.getTracks().get(0)).updateTrack();
	}

	/**
	 * Loads all track data from a data file.
	 *
	 * @param f file to load from.
	 */
	public void loadData(File f)
	{
		try (FileInputStream fis = new FileInputStream(f))
		{
			createMusicSequencer();
			player.loadTracks(fis);
		} catch (IOException e)
		{
			e.printStackTrace();
			//TODO: show error while loading.
		}
	}

	/**
	 * Saves all tracks to a data file.
	 *
	 * @param f file to save to.
	 */
	public void saveData(File f)
	{
		try (FileOutputStream fos = new FileOutputStream(f))
		{
			player.saveTracks(fos);
		} catch (IOException e)
		{
			e.printStackTrace();
			//TODO: show error while saving.
		}
	}

	/**
	 * Shows file choosing dialog
	 *
	 * @param open true if opening file, false if saving file
	 * @return opened file or null
	 */
	private File showFileChooser(boolean open)
	{
		boolean running = player.getPlayState() == Status.RUNNING;
		if (running)
			player.pause();

		FileChooser fileChooser = new FileChooser();

		if (!open && openFile != null)
			fileChooser.setInitialFileName(openFile.getName());
		fileChooser.setTitle(open ? "Open CC file" : "Save CC file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CC File", "*.ccp"));

		String defPath = Preferences.userRoot().get("CCDefaultPath", null);
		File defFilePath;
		if (defPath == null || !(defFilePath = new File(defPath)).exists())
			defFilePath = new File(DEFAULT_FOLDER_HOME);
		fileChooser.setInitialDirectory(defFilePath);

		File result;
		if (open)
			result = fileChooser.showOpenDialog(pane.getScene().getWindow());
		else
			result = fileChooser.showSaveDialog(pane.getScene().getWindow());

		if (result != null)
			Preferences.userRoot().put("CCDefaultPath", result.getParent());

		if (running)
			player.play();
		return result;
	}

	/**
	 * Getter for property 'toggle'.
	 * if true, change on to off and off to on. if false, left click is on and right click is off.
	 *
	 * @return Value for property 'toggle'.
	 */
	public static boolean isToggle()
	{
		return toggle;
	}

	/**
	 * Setter for property 'toggle'.
	 * if true, change on to off and off to on. if false, left click is on and right click is off.
	 *
	 * @param toggle Value to set for property 'toggle'.
	 */
	public static void setToggle(boolean toggle)
	{
		ClearComposer.toggle = toggle;
	}

	/**
	 * Getter for property 'perma'.
	 * what kind of on? true = permanent, false = temporary
	 *
	 * @return Value for property 'perma'.
	 */
	public static boolean isPerma()
	{
		return perma;
	}

	/**
	 * Setter for property 'perma'.
	 * what kind of on? true = permanent, false = temporary
	 *
	 * @param perma Value to set for property 'perma'.
	 */
	public static void setPerma(boolean perma)
	{
		ClearComposer.perma = perma;
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}