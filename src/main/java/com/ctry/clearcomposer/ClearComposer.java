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

import com.ctry.clearcomposer.music.Chord;
import com.ctry.clearcomposer.music.MusicConstants;
import com.ctry.clearcomposer.music.MusicPlayer;
import com.ctry.clearcomposer.music.TrackPlayer;
import com.ctry.clearcomposer.sequencer.BeatTrack;
import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.NotesTrack;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClearComposer extends Application
{
	public static final int DEFAULT_WIDTH = 780;
	public static final int DEFAULT_HEIGHT = 720;

	/**
	 * Constants
	 */
	public static MusicConstants constants = new MusicConstants();

	/**
	 * if true, change on to off and off to on
	 */
	private static boolean toggle = true;
	/**
	 * what kind of on? true = permanent, false = temporary
	 */
	private static boolean perma = true;

	/**
	 * the buttons to change chords
	 */
	private StackPane chordButtons;

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

		//test button to test midi note playing TODO: remove
		//Button root = createChordButton("undo", () -> MusicPlayer.playNote(new Random().nextInt(24) + 60));
		//pane.setRight(root);

		//music sequencer
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
		pane.setCenter(tracksDisplay);

		//chord buttons
		HBox primaryChords = new HBox(10);
		HBox secondaryChords = new HBox(10);
		VBox chordRows = new VBox(10);
		chordRows.getChildren().addAll(primaryChords, secondaryChords);
		chordRows.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		chordButtons = new StackPane();
		chordButtons.setPadding(new Insets(10));
		chordButtons.getStyleClass().add("panel");
		chordButtons.getChildren().add(chordRows);
		List<CCButton> chords = new ArrayList<>();
		for (Chord c : Chord.values())
		{
			CCButton button = new CCButton(c.toString(), Color.AQUA);
			button.setMinSize(100, 35);
			button.setPrefSize(100, 35);
			button.setMaxSize(100, 35);
			button.setOnMousePressed(evt -> {
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

		for (int i = 1; i < player.getTracks().size(); i++)
		{
			((NotesTrack) player.getTracks().get(i)).updateTrack();
		}
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
			player.loadTracks(fis);
		} catch (IOException e)
		{
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
			//TODO: show error while saving.
		}
	}



	/**
	 * Getter for property 'toggle'.
	 *
	 * @return Value for property 'toggle'.
	 */
	public static boolean isToggle()
	{
		return toggle;
	}

	/**
	 * Setter for property 'toggle'.
	 *
	 * @param toggle Value to set for property 'toggle'.
	 */
	public static void setToggle(boolean toggle)
	{
		ClearComposer.toggle = toggle;
	}

	/**
	 * Getter for property 'perma'.
	 *
	 * @return Value for property 'perma'.
	 */
	public static boolean isPerma()
	{
		return perma;
	}

	/**
	 * Setter for property 'perma'.
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