/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer;

import com.ctry.clearcomposer.music.MusicConstants;
import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.GraphicTrack;
import com.ctry.clearcomposer.sequencer.MasterTrack;
import com.ctry.clearcomposer.sequencer.NotesTrack;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

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

	@Override
	public void start(Stage primaryStage) throws Exception
	{

		Button root = createButton("undo", this::test);

		BorderPane pane = new BorderPane();
		pane.setBottom(root);

		VBox tracks = new VBox();
		tracks.getStyleClass().add("bg");
		for (int i = 7; i >= 0; i--)
		{
			tracks.getChildren().add(new NotesTrack(i / 5, i % 5).getTrack());
		}
		tracks.getChildren().add(new MasterTrack().getTrack());
		pane.setCenter(tracks);
		pane.getStyleClass().add("bg");


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

	public void test()
	{
		int channel = 0; // 0 is a piano, 9 is percussion, other channels are for other instruments

		int volume = 80; // between 0 et 127
		int duration = 200; // in milliseconds

		try
		{
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			MidiChannel[] channels = synth.getChannels();

			// --------------------------------------
			// Play a few notes.
			// The two arguments to the noteOn() method are:
			// "MIDI note number" (pitch of the note),
			// and "velocity" (i.e., volume, or intensity).
			// Each of these arguments is between 0 and 127.
			channels[channel].noteOn(60, volume); // C note
			Thread.sleep(duration);
			channels[channel].noteOff(60);
			channels[channel].noteOn(62, volume); // D note
			Thread.sleep(duration);
			channels[channel].noteOff(62);
			channels[channel].noteOn(64, volume); // E note
			Thread.sleep(duration);
			channels[channel].noteOff(64);

			Thread.sleep(500);

			// --------------------------------------
			// Play a C major chord.
			channels[channel].noteOn(60, volume); // C
			channels[channel].noteOn(64, volume); // E
			channels[channel].noteOn(67, volume); // G
			Thread.sleep(3000);
			channels[channel].allNotesOff();
			Thread.sleep(500);


			synth.close();
		} catch (Exception e)
		{
			e.printStackTrace();
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