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
 * Stores chord suggestions
 *
 * @author creativitRy
 * Date: 11/6/2016.
 */
package com.ctry.clearcomposer.music;

import java.util.HashMap;
import java.util.Map;

public class ChordProgressionHelper
{
	/**
	 * Map of chord to map of possible chords with strength
	 * For each chord, a higher strength means it will sound the most pleasant
	 */
	private static Map<Chord, Map<Chord, Double>> maps;

	/**
	 * Instantiates maps
	 */
	static
	{
		maps = new HashMap<>();

		//traditional circle of fifth
		for (Chord chord : Chord.values())
		{
			add(chord, circleOfFifth(chord), 1);
		}

		//half
		add(Chord.vi, Chord.V, 0.75);
		add(Chord.IV, Chord.V, 0.75);

		//predominant extension
		add(Chord.IV, Chord.ii, 0.7);

		//iii chord
		add(Chord.IV, Chord.iii, 0.5);
		add(Chord.iii, Chord.IV, 0.75);
		add(Chord.ii, Chord.iii, 0.25);

		//jazz circle of fifth
		add(Chord.iii, Chord.V_ii, 0.25);
		add(Chord.V, Chord.V_IV, 0.25);
		add(Chord.vi, Chord.V_V, 0.25);
		add(Chord.vii$, Chord.V_vi, 0.25);

		//vii* to I
		add(Chord.vii$, Chord.I, 1);

		//deceptive
		add(Chord.V, Chord.vi, 0.6);
		add(Chord.vii$, Chord.vi, 0.6);
		add(Chord.V_iii, Chord.I, 0.5);
		add(Chord.V_iii, Chord.V_IV, 0.25);
		add(Chord.V_IV, Chord.ii, 0.5);
		add(Chord.V_IV, Chord.V_ii, 0.4);
		add(Chord.V_IV, Chord.V_V, 0.25);
		add(Chord.V_V, Chord.iii, 0.5);
		add(Chord.V_V, Chord.V_vi, 0.25);
		add(Chord.V_vi, Chord.IV, 0.5);

		//plagal and reverse circle of fifth
		for (Chord chord : Chord.getPrimaryChords())
		{
			add(chord, circleOfFifthRev(chord), 0.25);
		}
		add(Chord.ii, Chord.V_ii, 0.25);
		add(Chord.iii, Chord.V_iii, 0.25);
		add(Chord.IV, Chord.V_IV, 0.25);
		add(Chord.V, Chord.V_V, 0.25);
		add(Chord.vi, Chord.V_vi, 0.25);

		//minor to major and vice versa
		add(Chord.vi, Chord.V_ii, 0.25);
		add(Chord.ii, Chord.V_V, 0.25);
		add(Chord.iii, Chord.V_vi, 0.25);
		add(Chord.V_ii, Chord.vi, 0.1);
		add(Chord.V_V, Chord.ii, 0.1);
		add(Chord.V_vi, Chord.iii, 0.1);

		//chormatic inversion up thingy
		add(Chord.I, Chord.V_ii, 0.3);
		add(Chord.ii, Chord.V_iii, 0.3);
		add(Chord.iii, Chord.V_IV, 0.3);
		add(Chord.IV, Chord.V_V, 0.3);
		add(Chord.V, Chord.V_vi, 0.5);

		//Other retrogression
		add(Chord.V, Chord.IV, .25);

		//I can go to any chord
		for (int i = 1; i < Chord.values().length; i++)
		{
			add(Chord.I, Chord.values()[i], 1);
		}

		//vi can go to most chords?
		add(Chord.vi, Chord.IV, 0.6);
		add(Chord.vi, Chord.I, 0.3);
		add(Chord.vi, Chord.V_IV, 0.3);
	}

	/**
	 * Adds new chord to the maps
	 *
	 * @param key      what chord it is transitioning from
	 * @param value    what chord it is transitioning to
	 * @param strength how good it sounds where greater = more pleasant.
	 *                 If value already exists, strength is set as the largest.
	 *                 If this is 0, removes chord
	 */
	private static void add(Chord key, Chord value, double strength)
	{
		if (!maps.containsKey(key))
			maps.put(key, new HashMap<>());

		if (maps.get(key).containsKey(value))
		{
			if (strength == 0)
				maps.get(key).remove(value);
			else
				maps.get(key).put(value, Math.max(strength, maps.get(key).get(value)));
		}
		else if (strength != 0)
			maps.get(key).put(value, strength);
	}

	private static Chord circleOfFifth(Chord start)
	{
		if (start.isSecondary())
			return Chord.values()[start.getChordNumber() - 1];

		return Chord.values()[(start.ordinal() + 3) % 7];
	}

	private static Chord circleOfFifthRev(Chord start)
	{
		return Chord.values()[(start.ordinal() + 4) % 7];
	}

	/**
	 * Given a key, return a map of most possible chords to move to
	 *
	 * @param from where to move from
	 * @return where to move to
	 */
	public static Map<Chord, Double> getPossibleChordProgressions(Chord from)
	{
		return maps.get(from);
	}

	private ChordProgressionHelper()
	{

	}

}
