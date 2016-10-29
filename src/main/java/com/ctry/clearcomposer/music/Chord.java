/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import static com.ctry.clearcomposer.music.RelativeNote.*;

public enum Chord
{
	I(DO, RE, MI, SOL, LA),
	ii(RE, MI, FA, SOL, LA),
	iii(RE, MI, SOL, LA, TI),
	IV(DO, RE, FA, SOL, LA),
	V(RE, FA, SOL, LA, TI),
	vi(DO, RE, MI, SOL, LA),
	vii(RE, FA, SOL, LA, TI);

	private RelativeNote[] notes;

	Chord(RelativeNote n0, RelativeNote n1, RelativeNote n2, RelativeNote n3, RelativeNote n4)
	{
		notes = new RelativeNote[]{n0, n1, n2, n3, n4};
	}

	public RelativeNote[] getNotes()
	{
		return notes;
	}

	public RelativeNote getNote(int index)
	{
		return notes[index];
	}
}
