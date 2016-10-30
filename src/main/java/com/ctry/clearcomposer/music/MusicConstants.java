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
	public static final double DEFAULT_TEMPO = 200;
	public static final Chord DEFAULT_CHORD = Chord.I;
	public static final int DEFAULT_NOTE_AMOUNT = 16;

	private AbsoluteNote key;
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
