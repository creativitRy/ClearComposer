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

import java.util.ArrayList;
import java.util.List;

public class MultipleEntry extends AbstractEntry
{
	private List<AbstractEntry> entries;

	public MultipleEntry(List<AbstractEntry> entries)
	{
		this.entries = entries;
	}

	/**
	 * Tooltip when undo / redo button has mouse cursor on top
	 *
	 * @return tooltip (will be formatted as "undo tooltip" or "redo tooltip")
	 */
	@Override
	public String toString()
	{
		return entries.toString();
	}

	/**
	 * Method called when undo button clicked
	 */
	@Override
	public void undo()
	{
		for (AbstractEntry entry : entries)
		{
			entry.undo();
		}
	}

	/**
	 * Method called when redo button clicked
	 */
	@Override
	public void redo()
	{
		for (AbstractEntry entry : entries)
		{
			entry.redo();
		}
	}
}
