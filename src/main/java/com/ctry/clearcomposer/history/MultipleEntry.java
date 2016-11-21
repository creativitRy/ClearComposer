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
 * Date: 11/18/2016.
 */
package com.ctry.clearcomposer.history;

import sun.awt.image.ImageWatched.Link;

import java.util.*;

public class MultipleEntry extends AbstractEntry
{
	private String description;
	private ArrayList<AbstractEntry> entries;

	public MultipleEntry(String description)
	{
		this.description = description;
		this.entries = new ArrayList<>();
	}

	/**
	 * Adds another entry to the end of this multiple entry.
	 * @param ent entry to add
	 */
	public void pushEntry(AbstractEntry ent)
	{
		entries.add(ent);
	}

	/**
	 * Removes the last entry to this multiple entry
	 * @return entry removed.
	 */
	public AbstractEntry popEntry()
	{
		return entries.remove(entries.size() - 1);
	}

	/**
	 * Tooltip when undo / redo button has mouse cursor on top
	 *
	 * @return tooltip (will be formatted as "undo tooltip" or "redo tooltip")
	 */
	@Override
	public String toString()
	{
		return description == null ? entries.toString() : description;
	}

	/**
	 * Method called when undo button clicked
	 */
	@Override
	public void undo()
	{
		ListIterator<AbstractEntry> itr = entries.listIterator(entries.size());
		while (itr.hasPrevious())
			itr.previous().undo();
	}

	/**
	 * Method called when redo button clicked
	 */
	@Override
	public void redo()
	{
		entries.forEach(AbstractEntry::redo);
	}
}
