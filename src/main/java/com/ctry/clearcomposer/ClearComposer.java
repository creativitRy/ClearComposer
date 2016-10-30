/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer;

import com.ctry.clearcomposer.music.Chord;
import com.ctry.clearcomposer.music.MusicConstants;
import com.ctry.clearcomposer.music.MusicPlayer;
import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.GraphicTrack;
import com.ctry.clearcomposer.sequencer.MasterTrack;
import com.ctry.clearcomposer.sequencer.NotesTrack;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClearComposer extends Application
{
	public static MusicConstants constants = new MusicConstants();

	/**
	 * if true, change on to off and off to on
	 */
	private static boolean toggle = true;
	/**
	 * what kind of on? true = permanent, false = temporary
	 */
	private static boolean perma = true;

	private HBox chordButtons;

	private List<GraphicTrack> tracks;

	@Override
	public void start(Stage primaryStage) throws Exception
	{

		Button root = createButton("undo", () -> MusicPlayer.playNote(new Random().nextInt(24) + 60 ));

		BorderPane pane = new BorderPane();
		pane.setRight(root);

		tracks = new ArrayList<>();
		VBox tracksDisplay = new VBox();
		tracksDisplay.setAlignment(Pos.CENTER);
		tracksDisplay.getStyleClass().add("bg");
		for (int i = 7; i >= 0; i--)
		{
			tracks.add(0, new NotesTrack(i / 5, i % 5));
			tracksDisplay.getChildren().add(tracks.get(0).getTrack());
		}
		tracks.add(0, new MasterTrack());
		tracksDisplay.getChildren().add(tracks.get(0).getTrack());
		pane.setCenter(tracksDisplay);
		pane.getStyleClass().add("bg");

		chordButtons = new HBox(15);
		chordButtons.setAlignment(Pos.CENTER);
		for (Chord c : Chord.values())
		{
			Button button = createButton(c.name().toLowerCase(), () -> setChord(c));
			button.setText(c.name());
			chordButtons.getChildren().add(button);
		}
		setChord(constants.getChord());
		pane.setBottom(chordButtons);


		Scene scene = new Scene(pane, 960, 540);
		scene.setOnDragDetected(t -> scene.startFullDrag());
		scene.setOnMouseReleased(t ->
		{
			if (toggle)
				GraphicNote.stopToggle();
		});
		scene.getStylesheets().add(ClearComposer.class.getResource("clearcomposer.css").toExternalForm());

		primaryStage.setScene(scene);

		primaryStage.setTitle("Hello World");
		primaryStage.show();
	}

	public void setChord(Chord c)
	{
		constants.setChord(c);
		for (Node n : chordButtons.getChildren())
		{
			if (!(n instanceof Button))
				continue;

			Button b = (Button) n;
			b.setDisable(b.getStyleClass().get(0).equals(c.name().toLowerCase()));

		}

		for (int i = 1; i < tracks.size(); i++)
		{
			((NotesTrack) tracks.get(i)).updateNote();
		}
	}



	private Button createButton(String styleClass, Runnable action)
	{
		Button button = new Button();
		button.getStyleClass().add(styleClass);
		button.setOnAction((evt) ->
		{
			action.run();
		});
		button.setPrefWidth(20);
		button.setPrefHeight(20);
		return button;
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