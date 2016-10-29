/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.music.RelativeNote;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NotesTrack extends GraphicTrack
{
	private int octave;
	private int index;

	public NotesTrack(int octave, int index)
	{
		super(ClearComposer.constants.getChord().getNote(index).getPitch().getColor());

		this.octave = octave;
		this.index = index;

		Text text = new Text(String.format("%4s: ", getNote().getPitch().toString()));
		text.getStyleClass().add("text");
		getTrack().getChildren().add(0, text);
	}

	public void updateColor()
	{
		changeColor(getNote().getPitch().getColor());
	}

	private RelativeNote getNote()
	{
		return ClearComposer.constants.getChord().getNote(index);
	}

	public int playNote(int index)
	{
		GraphicNote note = (GraphicNote) getTrack().getChildren().get(index);

		if (note.isOn())
			return getNote().getAbsolutePitch(octave);
		return -1;
	}
}
