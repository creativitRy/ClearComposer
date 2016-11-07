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
 * Main class
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.prefs.Preferences;

import com.ctry.clearcomposer.history.AbstractEntry;
import com.ctry.clearcomposer.history.ChordEntry;
import com.ctry.clearcomposer.history.KeyEntry;
import com.ctry.clearcomposer.music.*;
import com.ctry.clearcomposer.sequencer.BassNotesTrack;
import com.ctry.clearcomposer.sequencer.BeatTrack;
import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.NotesTrack;

import com.sun.javafx.tk.Toolkit;
import javafx.animation.Animation.Status;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ClearComposer extends Application
{
	public static final int DEFAULT_WIDTH = 1250;
	public static final int DEFAULT_HEIGHT = 720;
	public static final int MAX_UNDOS = 1000;

	public static String DEFAULT_FOLDER_HOME = System.getProperty("user.home");

	/**
	 * Constants
	 */
	public static MusicConstants constants = new MusicConstants();

	/**
	 * Main entity
	 */
	public static ClearComposer cc;

	/**
	 * if true, change on to off and off to on. if false, left click is on and right click is off.
	 */
	private static boolean toggle = true;
	/**
	 * what kind of on? true = permanent, false = temporary
	 */
	private static boolean perma = true;

	/**
	 * file opened currently or null
	 */
	private static File openFile = null;
	private static boolean changed = false;


	/**
	 * the buttons to change chords
	 */
	private StackPane chordPane;

	/**
	 * buttons of chords
	 */
	private EnumMap<Chord, CCButton> chordButtons;
	private EnumMap<Chord, RadioMenuItem> chordMenus = new EnumMap<>(Chord.class);

	/**
	 * plays music and keeps track of note/beat tracks
	 */
	private TrackPlayer player;

	/**
	 * Main pane
	 */
	private BorderPane pane;
	private VBox top;

	private ToolbarButton btnPlay;
	private ToolbarButton btnPause;
	private BooleanProperty pauseToggle = new SimpleBooleanProperty();

	private ComboBox<Key> cmbKeys;
	private ComboBox<String> cmbNotes;
	private Slider tempoSlider;
	private Label tempoIndicator;

	private BooleanProperty permaToggle = new SimpleBooleanProperty(perma)
	{
		@Override
		protected void invalidated()
		{
			perma = get();
		}
	};
	private BooleanProperty noteToggle = new SimpleBooleanProperty(toggle)
	{
		@Override
		protected void invalidated()
		{
			toggle = get();
		}
	};

	private Deque<AbstractEntry> undoes = new LinkedList<>();
	private Deque<AbstractEntry> redoes = new LinkedList<>();

	/**
	 * Sets all ui stuff to match MusicConstants
	 */
	public void resetUI()
	{
		//Scale Key
		cmbKeys.setValue(constants.getKey());

		//cmbKeys.getSelectionModel().select(constants.getKey().ordinal());

		//Number of notes
		int numNotesInd = -1;
		List<String> numNotes = cmbNotes.getItems();
		for (int i = 0; i < numNotes.size(); i++)
		{
			if (parseNoteInt(numNotes.get(i)) == constants.getNoteAmount())
			{
				numNotesInd = i;
				break;
			}
		}

		if (numNotesInd == -1)
		{
			numNotesInd = numNotes.size();
			numNotes.add(constants.getNoteAmount() + " Notes");
		}

		cmbNotes.getSelectionModel().select(numNotesInd);

		//Chord
		setChord(constants.getChord());

		//Tempo
		tempoSlider.setValue(constants.getTempo());
	}

	/**
	 * Main javafx method
	 *
	 * @param primaryStage main stage
	 * @throws Exception exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		cc = this;

		pane = new BorderPane();
		pane.getStyleClass().add("bg");

		top = new VBox();
		pane.setTop(top);

		MenuBar menuBar = initMenuBar();

		/**********************
		 * Toolbar buttons
		 **********************/
		//File
		Toolbar bar = new Toolbar();
		bar.addRegularButton("New", this::newCommand);
		bar.addRegularButton("Open", this::openCommand);
		bar.addRegularButton("Save", this::saveCommand);
		bar.addRegularButton("Save As", this::saveAsCommand);

		//Edit
		bar.addSeparator();
		bar.addRegularButton("Undo", this::undo);
		bar.addRegularButton("Redo", this::redo);

		//Running
		bar.addSeparator();
		btnPlay = bar.addButton("Play");
		btnPlay.setOnMousePressed(evt -> btnPlay.setButtonPressed(true));
		btnPlay.setOnMouseClicked(evt -> playCommand());
		btnPause = bar.addToggleButton("Pause", pauseToggle, this::pausedCommand);
		bar.addRegularButton("Stop", this::stopCommand);

		//Note config
		bar.addSeparator();
		cmbKeys = bar.addComboBox("Key", () ->
		{
			Key newValue = cmbKeys.getValue();
			pushMove(new KeyEntry(newValue, constants.getKey()));
			setKey(newValue);
		}, constants.getKey().ordinal(), Key.values());
		cmbNotes = bar.addComboBox("Number of Notes", () -> setNumNotes(parseNoteInt(cmbNotes.getValue())),
			1, "12 Notes", "16 Notes");
		tempoSlider = bar.addSlider("Tempo", () -> constants.setTempo(tempoSlider.getValue()),
			10, 999, constants.getTempo());
		tempoIndicator = new Label();
		tempoIndicator.textProperty().bind(tempoSlider.valueProperty().asString("%.0f BPM"));
		tempoIndicator.setTextFill(Color.WHITE);
		bar.addNode(tempoIndicator);

		//Edit NotePlayState
		bar.addSeparator();
		bar.addToggleButton("Perma", permaToggle, null);
		bar.addToggleButton("Toggling", noteToggle, null);

		top.getChildren().addAll(menuBar, bar);

		//Music sequencer
		createMusicSequencer();

		//Chord buttons
		HBox primaryChords = new HBox(10);
		HBox secondaryChords = new HBox(10);
		VBox chordRows = new VBox(10);
		secondaryChords.setAlignment(Pos.CENTER);
		chordRows.getChildren().addAll(primaryChords, secondaryChords);
		chordRows.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		chordPane = new StackPane();
		chordPane.setPadding(new Insets(10));
		chordPane.getStyleClass().add("panel");
		chordPane.getChildren().add(chordRows);
		chordButtons = new EnumMap<>(Chord.class);
		for (Chord c : Chord.values())
		{
			CCButton button = new CCButton(c.toString(), c.getColor());
			button.setMinSize(100, 35);
			button.setPrefSize(100, 35);
			button.setMaxSize(100, 35);
			button.setOnMousePressed(evt ->
			{
				if (button.isButtonPressed())
					return;
				pushMove(new ChordEntry(c, constants.getChord()));
				setChord(c);
			});
			if (c == constants.getChord())
				button.setButtonPressed(true);

			chordButtons.put(c, button);
			if (c.isSecondary())
				secondaryChords.getChildren().add(button);
			else
				primaryChords.getChildren().add(button);
		}

		setChord(constants.getChord());
		pane.setBottom(chordPane);

		//Scene settings
		Scene scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		scene.setOnDragDetected(evt -> scene.startFullDrag());
		scene.setOnDragOver(evt ->
		{
			if (evt.getDragboard().hasFiles())
				evt.acceptTransferModes(TransferMode.COPY);
			evt.consume();
		});
		scene.setOnDragDropped(evt ->
		{
			if (evt.getDragboard().hasFiles())
			{
				File open = evt.getDragboard().getFiles().get(0);
				Platform.runLater(() ->
				{
					if (!checkSave())
						return;


					if (open != null)
					{
						loadData(open);
						openFile = open;
						setTitle();
					}
				});
			}

			evt.setDropCompleted(true);
			evt.consume();
		});
		scene.setOnKeyPressed(t ->
		{
			//			Chord cSelect = null;
			//			//secondary
			//			if (t.isShiftDown())
			//			{
			//				if (t.getCode() == KeyCode.DIGIT2 || t.getCode() == KeyCode.NUMPAD2)
			//					cSelect = Chord.V_ii;
			//				else if (t.getCode() == KeyCode.DIGIT3 || t.getCode() == KeyCode.NUMPAD3)
			//					cSelect = Chord.V_iii;
			//				else if (t.getCode() == KeyCode.DIGIT4 || t.getCode() == KeyCode.NUMPAD4)
			//					cSelect = Chord.V_IV;
			//				else if (t.getCode() == KeyCode.DIGIT5 || t.getCode() == KeyCode.NUMPAD5)
			//					cSelect = Chord.V_V;
			//				else if (t.getCode() == KeyCode.DIGIT6 || t.getCode() == KeyCode.NUMPAD6)
			//					cSelect = Chord.V_vi;
			//			}
			//			else
			//			{
			//				if (t.getCode() == KeyCode.DIGIT1 || t.getCode() == KeyCode.NUMPAD1)
			//					cSelect = Chord.I;
			//				else if (t.getCode() == KeyCode.DIGIT2 || t.getCode() == KeyCode.NUMPAD2)
			//					cSelect = Chord.ii;
			//				else if (t.getCode() == KeyCode.DIGIT3 || t.getCode() == KeyCode.NUMPAD3)
			//					cSelect = Chord.iii;
			//				else if (t.getCode() == KeyCode.DIGIT4 || t.getCode() == KeyCode.NUMPAD4)
			//					cSelect = Chord.IV;
			//				else if (t.getCode() == KeyCode.DIGIT5 || t.getCode() == KeyCode.NUMPAD5)
			//					cSelect = Chord.V;
			//				else if (t.getCode() == KeyCode.DIGIT6 || t.getCode() == KeyCode.NUMPAD6)
			//					cSelect = Chord.vi;
			//				else if (t.getCode() == KeyCode.DIGIT7 || t.getCode() == KeyCode.NUMPAD7)
			//					cSelect = Chord.vii$;
			//			}

			//			if (cSelect != null)
			//			{
			//				pushMove(new ChordEntry(cSelect, constants.getChord()));
			//				setChord(cSelect);
			//			}
		});
		scene.setOnMouseReleased(t -> GraphicNote.finishNotesEditing());
		scene.getStylesheets().add(ClearComposer.class.getResource("clearcomposer.css").toExternalForm());

		//Configure main stage
		primaryStage.getIcons().add(new Image(ClearComposer.class.getResourceAsStream("Logo.png")));
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(e ->
		{
			e.consume();
			exitCommand();
		});
		primaryStage.show();

		setTitle();
	}

	private void exitCommand()
	{
		if (!checkSave())
			return;
		MusicPlayer.turnOffNotes();
		Platform.exit();
	}

	private void stopCommand()
	{
		btnPlay.setButtonPressed(false);
		btnPause.setButtonPressed(false);
		pauseToggle.setValue(false);
		player.stop();
	}

	private void playCommand()
	{
		if (!btnPause.isButtonPressed() && player.getPlayState() != Status.RUNNING) //Only play if we are stopped
		{
			btnPlay.setButtonPressed(true);
			player.play();
		}
	}

	private void pausedCommand()
	{
		if (pauseToggle.get())
		{
			btnPlay.setButtonPressed(true); //In case user presses pause first.
			if (player.getPlayState() == Status.RUNNING)
				player.pause();
		}
		else
			player.play();
	}

	private boolean newCommand()
	{
		if (!checkSave())
			return false;

		openFile = null;
		constants = new MusicConstants();
		resetUI();
		setTitle();
		createMusicSequencer();
		return true;
	}

	private boolean saveAsCommand()
	{
		File save = showFileChooser(false);
		if (save == null)
			return false;

		openFile = save;
		saveData(openFile);
		return true;
	}

	private boolean saveCommand()
	{
		if (openFile == null)
		{
			File save = showFileChooser(false);
			if (save == null)
				return false;
			openFile = save;
		}

		saveData(openFile);
		return true;
	}

	private boolean openCommand()
	{
		if (!checkSave())
			return false;
		File open = showFileChooser(true);
		if (open != null)
		{
			loadData(open);
			openFile = open;
			setTitle();
		}
		return true;
	}


	private MenuItem createMenuItem(String name, String keyAccelerator, Runnable onAction)
	{
		MenuItem mnuItem = new MenuItem(name);
		mnuItem.setMnemonicParsing(true);
		if (keyAccelerator != null)
			mnuItem.setAccelerator(KeyCombination.keyCombination(keyAccelerator));
		mnuItem.setOnAction(evt -> onAction.run());
		return mnuItem;
	}

	/**
	 * Initializes menu items and menu-bar
	 */
	private MenuBar initMenuBar()
	{
		//Shortcut means Ctrl in Windows, Meta in Mac
		MenuBar bar = new MenuBar();

		//File
		Menu mnuFile = new Menu("_File");
		mnuFile.setMnemonicParsing(true);
		mnuFile.getItems().addAll(
			createMenuItem("_New", "Shortcut+N", this::newCommand),
			createMenuItem("_Open", "Shortcut+O", this::openCommand),
			createMenuItem("_Save", "Shortcut+S", this::saveCommand),
			createMenuItem("Save _as", "Shortcut+Shift+S", this::saveAsCommand),
			new SeparatorMenuItem(),
			createMenuItem("E_xit", "Alt+X", this::exitCommand)
		);

		//Edit
		Menu mnuEdit = new Menu("_Edit");
		Menu mnuEditChords = new Menu("_Chords");
		mnuEdit.setMnemonicParsing(true);
		mnuEdit.getItems().addAll(
			createMenuItem("_Undo", "Shortcut+Z", this::undo),
			createMenuItem("_Redo", "Shortcut+Y", this::redo),
			new SeparatorMenuItem(),
			mnuEditChords
		);

		ArrayList<Chord> primaryChords = new ArrayList<>();
		ArrayList<Chord> secondaryChords = new ArrayList<>();
		for (Chord c : Chord.values())
		{
			if (c.isSecondary())
				secondaryChords.add(c);
			else
				primaryChords.add(c);
		}

		ToggleGroup chordGroup = new ToggleGroup();
		for (Chord c : primaryChords)
		{
			RadioMenuItem chordMenu = new RadioMenuItem("Chord " + c.toString());
			chordMenu.setOnAction(evt ->
			{
				if (c != constants.getChord())
				{
					pushMove(new ChordEntry(c, constants.getChord()));
					setChord(c);
				}
			});
			chordMenu.setAccelerator(new NumberKeyCombination(c.getChordNumber()));
			chordMenus.put(c, chordMenu);
			chordGroup.getToggles().add(chordMenu);
			mnuEditChords.getItems().add(chordMenu);
		}
		mnuEditChords.getItems().add(new SeparatorMenuItem());
		for (Chord c : secondaryChords)
		{
			RadioMenuItem chordMenu = new RadioMenuItem("Chord " + c.toString());
			chordMenu.setOnAction(evt ->
			{
				if (c != constants.getChord())
				{
					pushMove(new ChordEntry(c, constants.getChord()));
					setChord(c);
				}
			});
			chordMenu.setAccelerator(new NumberKeyCombination(c.getChordNumber(), KeyCombination.SHIFT_DOWN));
			chordMenus.put(c, chordMenu);
			chordGroup.getToggles().add(chordMenu);
			mnuEditChords.getItems().add(chordMenu);
		}

		//Playing
		//TODO

		bar.getMenus().addAll(mnuFile, mnuEdit);
		return bar;
	}

	private void setTitle()
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append("ClearComposer - ");
		if (openFile == null)
			sb.append("Untitled");
		else
			sb.append(openFile.getAbsolutePath());
		if (changed)
			sb.append('*');
		((Stage) pane.getScene().getWindow()).setTitle(sb.toString());
	}


	private boolean checkSave()
	{
		if (!changed)
			return true;

		Alert dlg = new Alert(Alert.AlertType.WARNING, "Would you like to save the current file?", ButtonType.YES,
			ButtonType.NO, ButtonType.CANCEL);
		dlg.setHeaderText(null);
		dlg.setTitle("ClearComposer");
		ButtonType resp = dlg.showAndWait().orElse(ButtonType.CANCEL);
		if (resp == ButtonType.CANCEL || resp == ButtonType.CLOSE)
			return false;
		else if (resp == ButtonType.YES)
			return saveCommand();
		else //Responded No
			return true;
	}

	private void createMusicSequencer()
	{
		//Stop everything
		if (btnPause != null)
			btnPause.setButtonPressed(false);
		if (btnPlay != null)
			btnPlay.setButtonPressed(false);
		if (player != null)
			player.stop();

		//Reset undo/redoes
		undoes.clear();
		redoes.clear();
		changed = false;

		player = new TrackPlayer();
		VBox tracksDisplay = new VBox();
		tracksDisplay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		tracksDisplay.setAlignment(Pos.CENTER);
		tracksDisplay.getStyleClass().add("bg");
		tracksDisplay.setPadding(new Insets(0, 100, 0, 0));
		for (int i = MusicConstants.TRACK_AMOUNT - 1; i >= 0; i--)
		{
			player.getTracks().add(0, new NotesTrack(i / 5, i % 5));
			tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		}
		player.getTracks().add(0, new BeatTrack());
		tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		player.getTracks().add(0, new BassNotesTrack());
		tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		pane.setCenter(tracksDisplay);
	}

	public void pushMove(AbstractEntry move)
	{
		changed = true;
		setTitle();
		redoes.clear();
		if (undoes.size() >= MAX_UNDOS)
			undoes.removeLast();
		undoes.push(move);
	}

	public void undo()
	{
		if (undoes.isEmpty())
			return;
		changed = true;
		AbstractEntry move = undoes.pop();
		redoes.push(move);
		move.undo();
	}

	public void redo()
	{
		if (redoes.isEmpty())
			return;
		changed = true;
		AbstractEntry move = redoes.pop();
		undoes.push(move);
		move.redo();
	}

	/**
	 * This sets the chord to MusicConstants and
	 * updates the ui for the chord
	 *
	 * @param ch chord chosen
	 */
	public void setChord(Chord ch)
	{
		constants.setChord(ch);
		chordButtons.forEach((c, btn) ->
		{
			btn.setButtonPressed(c == ch);
			btn.setBorder(new Color(0, 0, 0, 0), 3);
		});
		ChordProgressionHelper.getPossibleChordProgressions(ch).forEach((c, strength) ->
			chordButtons.get(c).setBorder(new Color(1, 0.843, 0, strength / 3 + 0.5), strength * 2 + 2));
		chordMenus.entrySet()
			.parallelStream()
			.filter(ent -> ent.getKey() == ch)
			.findFirst()
			.ifPresent(ent -> ent.getValue().setSelected(true));
		updateTracks();
	}

	/**
	 * Sets key to new key
	 *
	 * @param key new key
	 */
	public void setKey(Key key)
	{
		constants.setKey(key);
		updateTracks();
	}

	/**
	 * Sets number of notes to new number of notes
	 *
	 * @param numNotes new number of notes
	 */
	public void setNumNotes(int numNotes)
	{
		//TODO: if user sets number of notes, all undoes/redoes will be lost.
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			player.saveTracks(oos);
			oos.flush();

			constants.setNoteAmount(numNotes);
			createMusicSequencer();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			player.loadTracks(ois);
		} catch (IOException e)
		{
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while setting number of notes", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(pane.getScene().getWindow());
			dlg.showAndWait();
			e.printStackTrace();
		}
	}

	private void updateTracks()
	{
		// iterate over all note tracks
		for (int i = 2; i < player.getTracks().size(); i++)
		{
			((NotesTrack) player.getTracks().get(i)).updateTrack();
		}

		((BassNotesTrack) player.getTracks().get(0)).updateTrack();
	}

	/**
	 * Loads all track data from a data file.
	 *
	 * @param f file to load from.
	 */
	public void loadData(File f)
	{
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f)))
		{
			constants = (MusicConstants) ois.readObject();
			resetUI();
			createMusicSequencer();
			player.loadTracks(ois);
			changed = false;
		} catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while loading data", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(pane.getScene().getWindow());
			dlg.showAndWait();
		}
	}

	/**
	 * Saves all tracks to a data file.
	 *
	 * @param f file to save to.
	 */
	public void saveData(File f)
	{
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f)))
		{
			oos.writeObject(constants);
			player.saveTracks(oos);
			changed = false;
			setTitle();
		} catch (IOException e)
		{
			e.printStackTrace();
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while saving data", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(pane.getScene().getWindow());
			dlg.showAndWait();
			//TODO: stop whatever you are doing if this occurs.
		}
	}

	/**
	 * Shows file choosing dialog
	 *
	 * @param open true if opening file, false if saving file
	 * @return opened file or null
	 */
	private File showFileChooser(boolean open)
	{
		player.stop();
		btnPause.setButtonPressed(false);
		btnPlay.setButtonPressed(false);

		FileChooser fileChooser = new FileChooser();

		if (!open && openFile != null)
			fileChooser.setInitialFileName(openFile.getName());
		fileChooser.setTitle(open ? "Open CC file" : "Save CC file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CC File", "*.ccp"));

		String defPath = Preferences.userRoot().get("CCDefaultPath", null);
		File defFilePath;
		if (defPath == null || !(defFilePath = new File(defPath)).exists())
			defFilePath = new File(DEFAULT_FOLDER_HOME);
		fileChooser.setInitialDirectory(defFilePath);

		File result;
		if (open)
			result = fileChooser.showOpenDialog(pane.getScene().getWindow());
		else
			result = fileChooser.showSaveDialog(pane.getScene().getWindow());

		if (result != null)
			Preferences.userRoot().put("CCDefaultPath", result.getParent());

		return result;
	}

	/**
	 * Getter for property 'toggle'.
	 * if true, change on to off and off to on. if false, left click is on and right click is off.
	 *
	 * @return Value for property 'toggle'.
	 */
	public static boolean isToggle()
	{
		return toggle;
	}

	/**
	 * Getter for property 'perma'.
	 * what kind of on? true = permanent, false = temporary
	 *
	 * @return Value for property 'perma'.
	 */
	public static boolean isPerma()
	{
		return perma;
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	private static int parseNoteInt(String notes)
	{
		return Integer.parseInt(notes.replaceAll("\\D", ""));
	}
}