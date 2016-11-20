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
 * Description
 *
 * @author creativitRy
 * Date: 11/1/2016.
 */
package com.ctry.clearcomposer.history;

import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.NotePlayState;
import com.ctry.clearcomposer.sequencer.NotePrevState;

import java.util.HashMap;
import java.util.Map;

public class NotesEntry extends AbstractEntry
{
	private HashMap<GraphicNote, NotePrevState> notesState;

	/**
	 * new notes entry instance
	 *
	 * @param noteStates all CHANGED notes with their initial states
	 */
	public NotesEntry(HashMap<GraphicNote, NotePrevState> noteStates)
	{
		this.notesState = noteStates;
	}

	/**
	 * Tooltip when undo / redo button has mouse cursor on top
	 *
	 * @return tooltip (will be formatted as "undo tooltip" or "redo tooltip")
	 */
	@Override
	public String toString()
	{
		return "Note Edits";
	}

	/**
	 * Method called when undo button clicked
	 * toggles all notes
	 */
	@Override
	public void undo()
	{
		setNotes(false);
	}

	/**
	 * Method called when redo button clicked
	 */
	@Override
	public void redo()
	{
		setNotes(true);
	}

	private void setNotes(boolean invert)
	{
		for (Map.Entry<GraphicNote, NotePrevState> entry : notesState.entrySet())
		{
			GraphicNote note = entry.getKey();
			NotePrevState prev = entry.getValue();
			boolean turnOn = invert ^ prev.wasOn();

			//Only toggle if we need to change note state.
			if (turnOn ^ note.isOn())
				note.toggle(prev.wasPerma());
		}
	}
}
