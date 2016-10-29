/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

public class MusicConstants
{
	/**
	 * Lowest pitch that is A where 60 is C
	 * Between 0 and 127
	 */
	public static int LOWEST_PITCH = 57;

	public static final AbsoluteNote DEFAULT_KEY = AbsoluteNote.C;
	public static final int DEFAULT_TEMPO = 120;
	public static final Chord DEFAULT_CHORD = Chord.I;

	private AbsoluteNote key;
	private int tempo;
	private Chord chord;

	public MusicConstants()
	{
		key = DEFAULT_KEY;
		tempo = DEFAULT_TEMPO;
		chord = DEFAULT_CHORD;
	}

	/**
	 * Getter for property 'key'.
	 *
	 * @return Value for property 'key'.
	 */
	public AbsoluteNote getKey()
	{
		return key;
	}

	/**
	 * Setter for property 'key'.
	 *
	 * @param key Value to set for property 'key'.
	 */
	public void setKey(AbsoluteNote key)
	{
		this.key = key;
	}

	/**
	 * Getter for property 'tempo'.
	 *
	 * @return Value for property 'tempo'.
	 */
	public int getTempo()
	{
		return tempo;
	}

	/**
	 * Setter for property 'tempo'.
	 *
	 * @param tempo Value to set for property 'tempo'.
	 */
	public void setTempo(int tempo)
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
}
