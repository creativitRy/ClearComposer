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
 * Plays the tracks
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.sequencer.GraphicTrack;

import javafx.animation.Animation.Status;
import javafx.animation.AnimationTimer;

public class TrackPlayer
{
	private double lastUpdate = -1;
	private double elapsed = 0;
	private int index;
	private List<GraphicTrack> tracks;
	private AnimationTimer tmr;
	private Status playState = Status.STOPPED;

	private Chord prevChord;

	private static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... params)
	{
		try
		{
			return type.getConstructor(params);
		} catch (NoSuchMethodException e)
		{
			return null;
		}
	}

	/**
	 * Constructs a new track player with multiple blank tracks
	 * Note: this will have the TrackPlayer not running by default
	 */
	public TrackPlayer()
	{
		index = 0;

		tmr = new AnimationTimer() {
			@Override
			public void handle(long xxx) {
				double now = System.currentTimeMillis() * .001;
				if (lastUpdate != -1) {
					elapsed += now - lastUpdate;
					if (elapsed >= 60.0 / ClearComposer.cc.getTempo())
					{
						elapsed = 0;
						playNotes();
					}
				}
				else
				{
					elapsed = 0;
					playNotes();
				}
				lastUpdate = now;
			}
		};

		tracks = new ArrayList<>();
	}

	/**
	 * Saves track data to an data stream.
	 * Note that this will not close the file.
	 *
	 * @param oos data stream to write to.
	 * @throws IOException when an I/O error occurs while writing
	 */
	public void saveTracks(ObjectOutputStream oos) throws IOException
	{
		oos.writeInt(tracks.size());
		for (GraphicTrack track : tracks)
		{
			oos.writeObject(track.getClass());
			track.saveTrackData(oos);
		}
		oos.flush();
	}

	/**
	 * Loads track data from an data stream.
	 * Note that this will not close the file.
	 *
	 * @param ois data stream to read from.
	 * @throws IOException when an I/O error occurs while reading.
	 */
	public void loadTracks(ObjectInputStream ois) throws IOException
	{
		int trackNum = ois.readInt();

		for (int i = 0; i < trackNum; i++)
		{
			try
			{
				GraphicTrack track = getTracks().get(i);
				Class<?> trackClass = (Class<?>) ois.readObject();
				if (!GraphicTrack.class.isAssignableFrom(trackClass))
					throw new FileCorruptionException("File Corruption: Track not instanceof GraphicsTrack");
				if (trackClass != track.getClass())
					throw new FileCorruptionException("File Corruption: Tracks mismatch");
				track.loadTrackData(ois);
			} catch (ClassNotFoundException e)
			{
				throw new FileCorruptionException("File Corruption: Unable to locate class");
			}
		}
	}

	/**
	 * plays all the notes in the current position
	 */
	private void playNotes()
	{
		//Update tracks when we can shift chords.
		Chord curChord = ClearComposer.cc.getChord();
		if (index % ClearComposer.cc.getChordInterval() == 0 && (prevChord == null || prevChord != curChord))
		{
			prevChord = curChord;
			tracks.forEach(GraphicTrack::updateChord);
			ClearComposer.cc.updateChordOutlines();
		}

		for (GraphicTrack track : tracks)
		{
			int temp = track.playNote(index);
			if (temp != -1)
				MusicPlayer.playNote(temp);
		}

		index++;
		while (index >= ClearComposer.cc.getNumNotes())
			index -= ClearComposer.cc.getNumNotes();
	}


	public Status getPlayState()
	{
		return playState;
	}

	/**
	 * play
	 */
	public void play()
	{
		playState = Status.RUNNING;
		tmr.start();
	}

	/**
	 * stop at current position
	 */
	public void pause()
	{
		playState = Status.PAUSED;
		lastUpdate = -1;
		tmr.stop();
	}

	/**
	 * stop and reset position
	 */
	public void stop()
	{
		index = 0;
		lastUpdate = -1;
		pause();
	}

	/**
	 * Getter for property 'tracks'.
	 *
	 * @return Value for property 'tracks'.
	 */
	public List<GraphicTrack> getTracks()
	{
		return tracks;
	}
}
