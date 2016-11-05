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

package com.ctry.clearcomposer;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class NumberKeyCombination extends KeyCombination {
	private int num;

	/**
	 * Constructs a {@code NumberKeyCombination} for the specified main number
	 * key and with an explicit specification of all modifier keys. Each modifier
	 * key can be set to {@code PRESSED}, {@code RELEASED} or {@code IGNORED}.
	 *
	 * @param num the main number key (0-9)
	 * @param shift the value of the {@code shift} modifier key
	 * @param control the value of the {@code control} modifier key
	 * @param alt the value of the {@code alt} modifier key
	 * @param meta the value of the {@code meta} modifier key
	 * @param shortcut the value of the {@code shortcut} modifier key
	 */
	public NumberKeyCombination(int num, ModifierValue shift, ModifierValue control,  
	                            ModifierValue alt, ModifierValue meta, ModifierValue shortcut) {
		super(shift, control, alt, meta, shortcut);

		validateNumber(num);
		this.num = num;
	}

	/**
	 * Constructs a {@code KeyCharacterCombination} for the specified main number
	 * key and the specified list of modifiers. All modifier keys which are not
	 * explicitly listed are set to the default {@code RELEASED} value.
	 * <p>
	 * All possible modifiers which change the default modifier value are
	 * defined as constants in the {@code KeyCombination} class.
	 *
	 * @param num the main number key (0-9)
	 * @param modifiers the list of modifier keys and their corresponding values
	 */
	public NumberKeyCombination(int num, Modifier... modifiers) {
		super(modifiers);
		validateNumber(num);
		this.num = num;
	}

	private static void validateNumber(int num) {
		if (num < 0 || num > 9)
			throw new IllegalArgumentException("num must be single digit number");
	}

	@Override
	public boolean match(KeyEvent event) {
		int code = event.getCode().ordinal();
		if (code >= KeyCode.NUMPAD0.ordinal() && code <= KeyCode.NUMPAD9.ordinal())
			code -= KeyCode.NUMPAD0.ordinal();
		else if (code >= KeyCode.DIGIT0.ordinal() && code <= KeyCode.DIGIT9.ordinal())
			code -= KeyCode.DIGIT0.ordinal();
		else
			return false;
		return code == num && super.match(event);
	}

	/**
	 * Returns a string representation of this {@code NumberKeyCombination}.
	 * <p>
	 * The string representation consists of sections separated by plus
	 * characters. Each section specifies either a modifier key or the main key.
	 * <p>
	 * A modifier key section contains the {@code KeyCode} name of a modifier
	 * key. It can be prefixed with the {@code Ignored} keyword. A non-prefixed
	 * modifier key implies its {@code PRESSED} value while the prefixed version
	 * implies the {@code IGNORED} value. If some modifier key is not specified
	 * in the string at all, it means it has the default {@code RELEASED} value.
	 * <p>
	 * The main key section contains the number key enclosed in single quotes
	 * and is the last section in the returned string.
	 *
	 * @return the string representation of this {@code KeyCharacterCombination}
	 */
	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getName());

		if (sb.length() > 0) {
			sb.append("+");
		}

		return sb.append('\'').append(num).append('\'').toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getDisplayText() {
		return super.getDisplayText() + num;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NumberKeyCombination)) return false;
		if (!super.equals(o)) return false;

		NumberKeyCombination that = (NumberKeyCombination) o;

		return num == that.num;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + num;
		return result;
	}
}
