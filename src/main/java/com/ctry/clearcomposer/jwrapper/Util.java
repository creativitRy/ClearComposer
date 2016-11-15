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

package com.ctry.clearcomposer.jwrapper;

/**
 * Copied from stack-overflow: <a href="http://stackoverflow.com/a/31547504">http://stackoverflow.com/a/31547504</a>
 */
public class Util {
	public enum OS {
		// Operating systems
		WINDOWS, LINUX, MAC, SOLARIS
	}

	private static OS os = null;

	public static OS getOS() {
		if (os == null) {
			String operSys = System.getProperty("os.name").toLowerCase();
			if (operSys.contains("win")) {
				os = OS.WINDOWS;
			} else if (operSys.contains("nix") || operSys.contains("nux")
					|| operSys.contains("aix")) {
				os = OS.LINUX;
			} else if (operSys.contains("mac")) {
				os = OS.MAC;
			} else if (operSys.contains("sunos")) {
				os = OS.SOLARIS;
			}
		}
		return os;
	}
}
