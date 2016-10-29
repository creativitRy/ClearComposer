/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer;

import com.ctry.clearcomposer.music.MusicConstants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
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
	private static boolean toggle;
	/**
	 * when toggle is false, true = turn all on, false = turn all off
	 * will be true when you start dragging from turned off note; false otherwise
	 */
	private static boolean on;
	/**
	 * what kind of on? true = permanent, false = temporary
	 */
	private static boolean perma;

	@Override
	public void start(Stage primaryStage) throws Exception
	{

		Button root = createButton("undo", () -> test());

		BorderPane pane = new BorderPane(root);




		Scene scene = new Scene(pane, 960, 540);
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

		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			MidiChannel[] channels = synth.getChannels();

			// --------------------------------------
			// Play a few notes.
			// The two arguments to the noteOn() method are:
			// "MIDI note number" (pitch of the note),
			// and "velocity" (i.e., volume, or intensity).
			// Each of these arguments is between 0 and 127.
			channels[channel].noteOn( 60, volume ); // C note
			Thread.sleep( duration );
			channels[channel].noteOff( 60 );
			channels[channel].noteOn( 62, volume ); // D note
			Thread.sleep( duration );
			channels[channel].noteOff( 62 );
			channels[channel].noteOn( 64, volume ); // E note
			Thread.sleep( duration );
			channels[channel].noteOff( 64 );

			Thread.sleep( 500 );

			// --------------------------------------
			// Play a C major chord.
			channels[channel].noteOn( 60, volume ); // C
			channels[channel].noteOn( 64, volume ); // E
			channels[channel].noteOn( 67, volume ); // G
			Thread.sleep( 3000 );
			channels[channel].allNotesOff();
			Thread.sleep( 500 );



			synth.close();
		}
		catch (Exception e) {
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
	 * Getter for property 'on'.
	 *
	 * @return Value for property 'on'.
	 */
	public static boolean isOn()
	{
		return on;
	}

	/**
	 * Setter for property 'on'.
	 *
	 * @param on Value to set for property 'on'.
	 */
	public static void setOn(boolean on)
	{
		ClearComposer.on = on;
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

	public static void main(String[] args) {
		launch(args);
	}
}