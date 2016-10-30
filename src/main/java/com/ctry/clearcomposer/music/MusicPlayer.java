/*
 * MIT License
 *
 * Copyright (c) 2016 Gahwon "creativitRy" Lee
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

	/**
	 * Plays the given midi pitch
	 * @param pitch midi pitch
	 */
	public static void playNote(int pitch)
	{
		try
		{
			if (!synth.isOpen())
				synth.open();

			MidiChannel[] channels = synth.getChannels();

			channels[CHANNEL].noteOff(pitch);
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

	/**
	 * after a delay, the note is turned off
	 * @param pitch pitch to turn off
	 */
	private static void turnOffNote(int pitch)
	{
		MidiChannel[] channels = synth.getChannels();

		channels[CHANNEL].noteOff(pitch);

		times.remove(pitch);

		if (times.isEmpty())
			synth.close();
	}

}
