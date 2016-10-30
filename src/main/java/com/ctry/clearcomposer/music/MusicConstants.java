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
 * Holds various constants
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import java.io.Serializable;

public class MusicConstants implements Serializable
{
	/**
	 * Lowest midi pitch that is A where 60 is C
	 * Between 0 and 127
	 */
	public static final int LOWEST_PITCH = 57;
	/**
	 * Amount of tracks
	 */
	public static final int TRACK_AMOUNT = 11;

	public static final Key DEFAULT_KEY = Key.C;
	public static final double DEFAULT_TEMPO = 200;
	public static final Chord DEFAULT_CHORD = Chord.V_V;
	public static final int DEFAULT_NOTE_AMOUNT = 16;

	private Key key;
	private double tempo;
	private Chord chord;
	private int noteAmount;

	public MusicConstants()
	{
		key = DEFAULT_KEY;
		tempo = DEFAULT_TEMPO;
		chord = DEFAULT_CHORD;
		noteAmount = DEFAULT_NOTE_AMOUNT;
	}

	/**
	 * Getter for property 'key'.
	 *
	 * @return Value for property 'key'.
	 */
	public Key getKey()
	{
		return key;
	}

	/**
	 * Setter for property 'key'.
	 *
	 * @param key Value to set for property 'key'.
	 */
	public void setKey(Key key)
	{
		this.key = key;
	}

	/**
	 * Getter for property 'tempo'.
	 *
	 * @return Value for property 'tempo'.
	 */
	public double getTempo()
	{
		return tempo;
	}

	/**
	 * Setter for property 'tempo'.
	 *
	 * @param tempo Value to set for property 'tempo'.
	 */
	public void setTempo(double tempo)
	{
		this.tempo = tempo;
	}

	/**
	 * Getter for property 'chord'.
	 *
	 * @return Value for property 'chord'.
	 */
	public Chord getChord()
	{
		return chord;
	}

	/**
	 * Setter for property 'chord'.
	 *
	 * @param chord Value to set for property 'chord'.
	 */
	public void setChord(Chord chord)
	{
		this.chord = chord;
	}

	/**
	 * Getter for property 'noteAmount'.
	 *
	 * @return Value for property 'noteAmount'.
	 */
	public int getNoteAmount()
	{
		return noteAmount;
	}

	/**
	 * Setter for property 'noteAmount'.
	 *
	 * @param noteAmount Value to set for property 'noteAmount'.
	 */
	public void setNoteAmount(int noteAmount)
	{
		this.noteAmount = noteAmount;
	}
}
