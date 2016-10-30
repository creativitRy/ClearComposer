/**
 * Plays a specific pitch
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer
{
	private static final int VOLUME = 100; // between 0 et 127
	private static final int CHANNEL = 0; // 0 is a piano, 9 is percussion, other channels are for other instruments
	private static final Duration DURATION = Duration.millis(1500); // in milliseconds

	private static Map<Integer, Timeline> times = new HashMap<>();
	private static Synthesizer synth;

	static
	{
		try
		{
			synth = MidiSystem.getSynthesizer();
		} catch (MidiUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	public static void playNote(int pitch)
	{
		try
		{
			if (!synth.isOpen())
				synth.open();

			MidiChannel[] channels = synth.getChannels();

			channels[CHANNEL].noteOn(pitch, VOLUME);

			if (times.containsKey(pitch))
				times.get(pitch).stop();

			times.put(pitch, new Timeline(new KeyFrame(DURATION, ae -> turnOffNote(pitch))));
			times.get(pitch).play();


		} catch (MidiUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	private static void turnOffNote(int pitch)
	{
		System.out.println(pitch);
		MidiChannel[] channels = synth.getChannels();

		channels[CHANNEL].noteOff(pitch);

		times.remove(pitch);

		if (times.isEmpty())
			synth.close();
	}

}
