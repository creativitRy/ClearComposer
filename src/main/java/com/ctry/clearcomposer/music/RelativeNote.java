/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import com.ctry.clearcomposer.ClearComposer;

public enum RelativeNote
{
	DO(0),
	RE(2),
	MI(4),
	FA(5),
	SOL(7),
	LA(9),
	TI(11);

	/**
	 * 0 = same as root of key, 1 = half note higher than root of key, etc
	 */
	private int steps;

	RelativeNote(int step)
	{
		steps = step;
	}

	public int getAbsolutePitch(int octave)
	{
		return ClearComposer.constants.getKey().getAbsolutePitch() + 12 * octave + steps;
	}

	public int getAbsolutePitch()
	{
		return getAbsolutePitch(0);
	}

	public AbsoluteNote getPitch()
	{
		return AbsoluteNote.values()[(ClearComposer.constants.getKey().getPitch() + steps) % 12];
	}

}
