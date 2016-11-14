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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import com.ctry.clearcomposer.history.AbstractEntry;
import com.ctry.clearcomposer.history.ChordEntry;
import com.ctry.clearcomposer.history.KeyEntry;
import com.ctry.clearcomposer.history.TempoEntry;
import com.ctry.clearcomposer.music.Chord;
import com.ctry.clearcomposer.music.ChordProgressionHelper;
import com.ctry.clearcomposer.music.Key;
import com.ctry.clearcomposer.music.MusicConstants;
import com.ctry.clearcomposer.music.MusicPlayer;
import com.ctry.clearcomposer.music.TrackPlayer;
import com.ctry.clearcomposer.sequencer.BassNotesTrack;
import com.ctry.clearcomposer.sequencer.BeatTrack;
import com.ctry.clearcomposer.sequencer.GraphicNote;
import com.ctry.clearcomposer.sequencer.GraphicTrack;
import com.ctry.clearcomposer.sequencer.NotesTrack;
import com.sun.glass.ui.Screen;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class ClearComposer extends Application {
	public static final int INSETS = 300;
	public static final int MAX_UNDOS = 1000;

	public static String DEFAULT_FOLDER_HOME = System.getProperty("user.home");

	private static String ANALYTICS_URL = "https://c.statcounter.com/11161817/0/807aa70b/1/";

	/**
	 * Main entity
	 */
	public static ClearComposer cc;
	//TODO: remove uses of this ^^ because it might break with multiple instances of the ClearComposer object.

	private Stage primaryStage;

	/**
	 * file opened currently or null
	 */
	private File openFile = null;
	private boolean changed = false;

	private MusicConstants constants = new MusicConstants();
	private boolean toggle = true;
	private boolean perma = true;
	private int chordInterval = 1;

	private BeatTrack beatTrack;
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
	private VBox topBar;
	private StackPane topHidden;

	private Transition hidingDelay;
	private TranslateTransition barSlide;

	//Buttons
	private ToolbarButton btnPlay;
	private ToolbarButton btnPause;

	//Menus
	private MenuItem mnuEditUndo;
	private MenuItem mnuEditRedo;

	//Note config stuff
	private ComboBox<Key> cmbKeys;
	private ComboBox<Integer> cmbNotes;
	private ComboBox<Integer> cmbChordChanges;
	private Slider tempoSlider;
	private Label tempoIndicator;

	private double tempoBefore = -1;

	//Disable states
	private BooleanProperty saveDisabled = new SimpleBooleanProperty(false);
	private BooleanProperty undoDisabled = new SimpleBooleanProperty(true);
	private BooleanProperty redoDisabled = new SimpleBooleanProperty(true);

	//Toggling states
	private BooleanProperty pauseToggle = new SimpleBooleanProperty();
	private BooleanProperty permaToggle = new SimpleBooleanProperty(perma) {
		@Override
		protected void invalidated() {
			perma = get();
		}
	};
	private BooleanProperty noteToggle = new SimpleBooleanProperty(toggle) {
		@Override
		protected void invalidated() {
			toggle = get();
		}
	};

	private Deque<AbstractEntry> undoes = new LinkedList<>();
	private Deque<AbstractEntry> redoes = new LinkedList<>();
	private Tooltip undoTooltip;
	private Tooltip redoTooltip;

	/**
	 * Initializes all the scene components.
	 * This is automatically called on a separate
	 * thread before the {@link #start(Stage)} method is
	 * called.
	 */
	@Override
	public void init() {
		//Count analytics
		try {
			InputStream in = new URL(ANALYTICS_URL).openStream();
			while (in.read() != -1) ;
			in.close();
		} catch (IOException e) {
			//Does nothing.
		}

		cc = this;

		pane = new BorderPane();
		pane.getStyleClass().add("bg");

		topBar = new VBox();
		topBar.setMaxHeight(Region.USE_PREF_SIZE);
		pane.setTop(topBar);
		StackPane.setAlignment(topBar, Pos.TOP_CENTER);
		//Toggle hidden/show state if you double click bar.
		topBar.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
			if (evt.getClickCount() == 2)
				showToolbar(topBar.getParent() == topHidden);
			evt.consume();
		});

		barSlide = new TranslateTransition(Duration.seconds(.5), topBar);
		barSlide.fromYProperty().bind(topBar.heightProperty().negate());
		barSlide.setInterpolator(Interpolator.EASE_BOTH);
		barSlide.setToY(0);

		hidingDelay = new Transition() {
			{
				setCycleDuration(Duration.seconds(1));
			}
			@Override
			protected void interpolate(double frac) {
				if (topBar.getParent() != topHidden)
					stop();
				else if (frac == 1)
				{
					barSlide.stop();
					barSlide.setRate(-1);
					barSlide.playFrom(barSlide.getDuration());
				}
			}
		};

		topHidden = new StackPane();
		topHidden.setMinHeight(20);
		topHidden.setPrefHeight(20);
		topHidden.setMaxHeight(20);
		topHidden.addEventFilter(MouseEvent.MOUSE_ENTERED, evt -> {
			hidingDelay.stop();
			if (barSlide.getCurrentRate() <= 0 && barSlide.getCurrentTime().lessThan(barSlide.getDuration())) {
				barSlide.setRate(1);
				barSlide.play();
			}
		});
		topHidden.addEventFilter(MouseEvent.MOUSE_EXITED, evt -> {
			if (barSlide.getCurrentRate() >= 0)
				hidingDelay.playFromStart();
		});

		undoTooltip = new Tooltip("Undo");
		redoTooltip = new Tooltip("Redo");

		MenuBar menuBar = initMenuBar();
		Toolbar bar = initToolbar();
		topBar.getChildren().addAll(menuBar, bar);
		menuBar.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> { //copy from ToolBar listener
			if (evt.getClickCount() == 2)
				showToolbar(topBar.getParent() == topHidden);
		});

		//Music sequencer
		initMusicSequencer();

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
		for (Chord c : Chord.values()) {
			CCButton button = new CCButton(c.toString(), c.getColor());
			button.setMinSize(100, Region.USE_PREF_SIZE);
			button.setPrefSize(100, Region.USE_COMPUTED_SIZE);
			button.setMaxSize(100, Region.USE_PREF_SIZE);
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
		updateChordOutlines();

		setChord(constants.getChord());
		pane.setBottom(chordPane);
	}

	public void pushMove(AbstractEntry move) {
		changed = true;
		setTitle();
		redoes.clear();
		if (undoes.size() >= MAX_UNDOS)
			undoes.removeLast();
		undoes.push(move);
		updateMoveStack();
	}

	/**
	 * Sets all ui stuff to match MusicConstants
	 */
	public void resetUI() {
		cmbKeys.setValue(constants.getKey());
		setKey(constants.getKey());
		cmbNotes.setValue(constants.getNumNotes());
		setNumNotes(constants.getNumNotes());
		setChord(constants.getChord());
		tempoSlider.setValue(constants.getTempo());
	}


	/**
	 * Main javafx method
	 *
	 * @param primaryStage main stage
	 * @throws Exception exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		//Scene settings
		Screen res = Screen.getMainScreen();
		Scene scene = new Scene(pane, res.getWidth() - INSETS, res.getHeight() - INSETS);
		scene.setOnDragDetected(evt -> scene.startFullDrag());
		scene.setOnDragOver(evt ->
		{
			if (evt.getDragboard().hasFiles())
				evt.acceptTransferModes(TransferMode.COPY);
			evt.consume();
		});
		scene.setOnDragDropped(evt ->
		{
			if (evt.getDragboard().hasFiles()) {
				File open = evt.getDragboard().getFiles().get(0);
				Platform.runLater(() ->
				{
					if (!checkSave())
						return;


					if (open != null) {
						loadData(open);
						openFile = open;
						setTitle();
					}
				});
			}

			evt.setDropCompleted(true);
			evt.consume();
		});
		scene.setOnMouseReleased(t -> GraphicNote.finishNotesEditing());
		scene.setOnMouseClicked(evt -> {
			if (evt.getClickCount() == 2)
			{
				primaryStage.setFullScreen(!primaryStage.isFullScreen());
			}
		});
		scene.getStylesheets().add(ClearComposer.class.getResource("clearcomposer.css").toExternalForm());

		//Configure main stage
		primaryStage.fullScreenProperty().addListener((val, before, after) -> showToolbar(!after));

		primaryStage.getIcons().add(new Image(ClearComposer.class.getResourceAsStream("Logo.png")));
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(e ->
		{
			e.consume();
			exitCommand();
		});
		primaryStage.show();

		double hInsets = primaryStage.getWidth() - scene.getWidth();
		double vInsets = primaryStage.getHeight() - scene.getHeight();

		boolean maximized = Preferences.userNodeForPackage(ClearComposer.class).getBoolean("maximized",
				res.getHeight() < 960 || res.getWidth() < 1280);
		double width = Preferences.userNodeForPackage(ClearComposer.class).getDouble("width", scene.getWidth() + hInsets);
		double height = Preferences.userNodeForPackage(ClearComposer.class).getDouble("height", scene.getHeight() + vInsets);
		double x = Preferences.userNodeForPackage(ClearComposer.class).getDouble("left", (res.getWidth() - width) / 2);
		double y = Preferences.userNodeForPackage(ClearComposer.class).getDouble("topBar", (res.getHeight() - height) / 2);

		if (x < res.getWidth() || y < res.getHeight())
		{
			x = (res.getWidth() - width) / 2;
			y = (res.getHeight() - height) / 2;
		}

		primaryStage.setMinWidth(pane.minWidth(-1) + hInsets);
		primaryStage.setMinHeight(pane.minHeight(-1) + vInsets);
		primaryStage.setWidth(width);
		primaryStage.setHeight(height);

		primaryStage.setX(x);
		primaryStage.setY(y);

		new Transition() {

			{
				setCycleDuration(Duration.seconds(.1));
			}

			@Override
			protected void interpolate(double frac) {
				if (frac == 1)
					primaryStage.setMaximized(maximized);
			}

		}.playFromStart();

		setTitle();

	}

	/**
	 * Updates all the chord cues (suggested chords and chord indicator).
	 */
	public void updateChordOutlines() {
		//Reset border and pressed.
		chordButtons.forEach((c, btn) ->
		{
			if (c == getChord())
				btn.setEffect(new InnerShadow(10, Color.RED));
			else
				btn.setEffect(null);
			btn.setBorder(new Color(0, 0, 0, 0), 3);
		});

		//Outline the suggested chords
		ChordProgressionHelper.getPossibleChordProgressions(getChord()).forEach((c, strength) ->
				chordButtons.get(c).setBorder(new Color(1, 0.843, 0, strength / 3 + 0.5), strength * 2 + 2));
	}

	//*********************
	//* ACCESSOR/ MUTATOR METHODS
	//* 
	//* Public accessor and mutator methods for certain parameters.
	//* This is ordered by property name
	//*********************

	/**
	 * This sets the chord to MusicConstants and
	 * updates the ui for the chord
	 *
	 * @param ch chord chosen
	 */
	public void setChord(Chord ch) {
		constants.setChord(ch);

		//Toggle button pressed state
		chordButtons.forEach((c, btn) -> btn.setButtonPressed(c == ch));

		//Select chord in chord menu
		chordMenus.entrySet()
				.parallelStream()
				.filter(ent -> ent.getKey() == ch)
				.findFirst()
				.ifPresent(ent -> ent.getValue().setSelected(true));
	}

	public Chord getChord() {
		return constants.getChord();
	}

	public int getChordInterval() {
		return chordInterval;
	}

	public void setChordInterval(int chordInterval) {
		this.chordInterval = chordInterval;
		if (beatTrack != null)
			beatTrack.updateChord();
	}

	/**
	 * Sets key to new key
	 *
	 * @param key new key
	 */
	public void setKey(Key key) {
		constants.setKey(key);
		chordButtons.forEach((c, btn) -> btn.setTextFill(c.getColor()));
		player.getTracks().forEach(GraphicTrack::updateChord);
	}

	public Key getKey() {
		return constants.getKey();
	}

	/**
	 * Sets number of notes to new number of notes
	 *
	 * @param numNotes new number of notes
	 */
	public void setNumNotes(int numNotes) {
		//TODO: if user sets number of notes, all undoes/redoes will be lost.
		cmbChordChanges.getItems().clear();
		for (int i = 1; i <= numNotes; i++) {
			if (numNotes % i == 0)
				cmbChordChanges.getItems().add(i);
		}
		cmbChordChanges.getSelectionModel().selectLast();

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			player.saveTracks(oos);
			oos.flush();

			constants.setNoteAmount(numNotes);
			initMusicSequencer();
			changed = true;
			setTitle();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			player.loadTracks(ois);
		} catch (IOException e) {
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while setting number of notes", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(primaryStage);
			dlg.showAndWait();
			e.printStackTrace();
		}
	}

	public int getNumNotes() {
		return constants.getNumNotes();
	}

	/**
	 * Getter for property 'perma'.
	 * what kind of on? true = permanent, false = temporary
	 *
	 * @return Value for property 'perma'.
	 */
	public boolean isPerma() {
		return perma;
	}

	public void setTempo(double tempo) {
		constants.setTempo(tempo);
		tempoSlider.setValue(tempo);
	}

	public double getTempo() {
		return constants.getTempo();
	}

	/**
	 * Getter for property 'toggle'.
	 * if true, change on to off and off to on. if false, left click is on and right click is off.
	 *
	 * @return Value for property 'toggle'.
	 */
	public boolean isToggle() {
		return toggle;
	}

	//*********************
	//* UI HELPER METHODS
	//* 
	//* All the following methods are helper methods
	//* that initialize certain aspects of the GUI.
	//*********************

	/**
	 * Helper method to create a menu item. (Convenience method)
	 *
	 * @param name           name of the menu button
	 * @param keyAccelerator shortcut key used to run menu item
	 * @param onAction       the handler when menu item is selected
	 * @return the created menu-item
	 */
	private MenuItem createMenuItem(String name, String keyAccelerator, Runnable onAction) {
		return createMenuItem(name, keyAccelerator, onAction, null);
	}

	/**
	 * Helper method to create a menu item.
	 *
	 * @param name             name of the menu button
	 * @param keyAccelerator   shortcut key used to run menu item
	 * @param onAction         the handler when menu item is selected
	 * @param disabledProperty a property describing when this menu-item is disabled
	 * @return the created menu-item
	 */
	private MenuItem createMenuItem(String name, String keyAccelerator, Runnable onAction, BooleanProperty disabledProperty) {
		MenuItem mnuItem = new MenuItem(name);
		mnuItem.setMnemonicParsing(true);
		if (keyAccelerator != null)
			mnuItem.setAccelerator(KeyCombination.keyCombination(keyAccelerator));
		mnuItem.setOnAction(evt -> onAction.run());
		if (disabledProperty != null)
			mnuItem.disableProperty().bind(disabledProperty);
		return mnuItem;
	}

	/**
	 * Initializes menu items and menu-bar
	 */
	private MenuBar initMenuBar() {
		//Shortcut means Ctrl in Windows, Meta in Mac
		MenuBar bar = new MenuBar();

		//File
		Menu mnuFile = new Menu("_File");
		mnuFile.setMnemonicParsing(true);
		Menu mnuFileOpenTemplates = new Menu("Open _Templates");
		mnuFileOpenTemplates.setMnemonicParsing(true);
		mnuFile.getItems().addAll(
				createMenuItem("_New", "Shortcut+N", this::newCommand),
				createMenuItem("_Open", "Shortcut+O", this::openCommand),
				mnuFileOpenTemplates,
				new SeparatorMenuItem(),
				createMenuItem("_Save", "Shortcut+S", this::saveCommand, saveDisabled),
				createMenuItem("Save _as", "Shortcut+Shift+S", this::saveAsCommand),
				new SeparatorMenuItem(),
				createMenuItem("E_xit", "Alt+X", this::exitCommand)
		);

		int tmplIndex = 1;
		while (true) {
			URL template = ClearComposer.class.getResource("Rhythm" + tmplIndex + ".ccp");
			if (template == null)
				break;

			MenuItem mnuTemplate = createMenuItem("Rhythm Template " + tmplIndex, null, () -> {
				try {
					if (!checkSave())
						return;
					loadData(template.openStream());
					openFile = null;
					changed = true;
					setTitle();
				} catch (IOException e) {
					Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while loading data", ButtonType.OK);
					dlg.setHeaderText(null);
					dlg.setTitle("ClearComposer");
					dlg.initOwner(primaryStage);
					dlg.showAndWait();
					e.printStackTrace();
				}
			});
			mnuFileOpenTemplates.getItems().add(mnuTemplate);

			tmplIndex++;
		}
		if (mnuFileOpenTemplates.getItems().isEmpty())
			mnuFileOpenTemplates.setDisable(true);

		//Edit
		Menu mnuEdit = new Menu("_Edit");
		mnuEdit.setMnemonicParsing(true);
		Menu mnuEditChords = new Menu("_Chords");
		mnuEditChords.setMnemonicParsing(true);
		mnuEditUndo = createMenuItem("_Undo", "Shortcut+Z", this::undoCommand, undoDisabled);
		mnuEditRedo = createMenuItem("_Redo", "Shortcut+Y", this::redoCommand, redoDisabled);

		mnuEdit.getItems().addAll(
				mnuEditUndo,
				mnuEditRedo,
				new SeparatorMenuItem(),
				mnuEditChords
		);

		ArrayList<Chord> primaryChords = new ArrayList<>();
		ArrayList<Chord> secondaryChords = new ArrayList<>();
		for (Chord c : Chord.values()) {
			if (c.isSecondary())
				secondaryChords.add(c);
			else
				primaryChords.add(c);
		}

		ToggleGroup chordGroup = new ToggleGroup();
		for (Chord c : primaryChords) {
			RadioMenuItem chordMenu = new RadioMenuItem("Chord " + c.toString());
			chordMenu.setOnAction(evt ->
			{
				if (c != constants.getChord()) {
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
		for (Chord c : secondaryChords) {
			RadioMenuItem chordMenu = new RadioMenuItem("Chord " + c.toString());
			chordMenu.setOnAction(evt ->
			{
				if (c != constants.getChord()) {
					pushMove(new ChordEntry(c, constants.getChord()));
					setChord(c);
				}
			});
			chordMenu.setAccelerator(new NumberKeyCombination(c.getChordNumber(), KeyCombination.SHIFT_DOWN));
			chordMenus.put(c, chordMenu);
			chordGroup.getToggles().add(chordMenu);
			mnuEditChords.getItems().add(chordMenu);
		}

		Menu mnuView = new Menu("_View");
		mnuView.setMnemonicParsing(true);
		mnuView.getItems().addAll(
				createMenuItem("Full screen...", "F5", this::fullScreenCommand)
		);
		//TODO

		bar.getMenus().addAll(mnuFile, mnuEdit, mnuView);
		return bar;
	}

	/**
	 * Initializes the music sequencer to the current
	 * <code>MusicConstants</code> parameters.
	 */
	private void initMusicSequencer() {
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

		//Update the stuff likewise
		setTitle();
		updateMoveStack();

		player = new TrackPlayer();
		setChordInterval(cmbChordChanges.getValue());
		if (chordButtons != null)
			updateChordOutlines();
		VBox tracksDisplay = new VBox();
		tracksDisplay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		tracksDisplay.setAlignment(Pos.CENTER);
		tracksDisplay.getStyleClass().add("bg");
		tracksDisplay.setPadding(new Insets(0, 100, 0, 0));
		for (int i = MusicConstants.TRACK_AMOUNT - 1; i >= 0; i--) {
			player.getTracks().add(0, new NotesTrack(i / 5, i % 5));
			tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		}
		beatTrack = new BeatTrack();
		player.getTracks().add(0, beatTrack);
		tracksDisplay.getChildren().add(beatTrack.getTrack());
		player.getTracks().add(0, new BassNotesTrack());
		tracksDisplay.getChildren().add(player.getTracks().get(0).getTrack());
		pane.setCenter(tracksDisplay);
	}

	/**
	 * Initializes the tool-bar buttons
	 *
	 * @return the toolbar that is initialized
	 */
	private Toolbar initToolbar() {
		Toolbar bar = new Toolbar();

		//File
		bar.addRegularButton("New", this::newCommand);
		bar.addRegularButton("Open", this::openCommand);
		bar.addRegularButton("Save", this::saveCommand).disableProperty().bind(saveDisabled);
		bar.addRegularButton("Save As", this::saveAsCommand);

		//Edit
		bar.addSeparator();
		ToolbarButton undoButton = bar.addRegularButton("Undo", this::undoCommand);
		undoButton.disableProperty().bind(undoDisabled);
		Tooltip.install(undoButton, undoTooltip);
		ToolbarButton redoButton = bar.addRegularButton("Redo", this::redoCommand);
		redoButton.disableProperty().bind(redoDisabled);
		Tooltip.install(redoButton, redoTooltip);

		//Running
		bar.addSeparator();
		btnPlay = bar.addButton("Play");
		btnPlay.setOnMousePressed(evt -> btnPlay.setButtonPressed(true));
		btnPlay.setOnMouseClicked(evt -> playCommand());
		btnPause = bar.addToggleButton("Pause", pauseToggle, this::pausedCommand);
		bar.addRegularButton("Stop", this::stopCommand);

		//Note config
		bar.addSeparator();
		cmbKeys = bar.addComboBox("Set the major key", () ->
		{
			Key newValue = cmbKeys.getValue();
			pushMove(new KeyEntry(newValue, constants.getKey()));
			setKey(newValue);
		}, constants.getKey().ordinal(), Key.values());
		cmbNotes = bar.addComboBox("Set the number of notes per cycle", () -> setNumNotes(cmbNotes.getValue()), 2, 8, 12, 16, 20);
		cmbNotes.setConverter(new StringConverter<Integer>() {

			@Override
			public String toString(Integer val) {
				return val + " Notes";
			}

			@Override
			public Integer fromString(String val) {
				return Integer.parseInt(val.replaceAll("\\D", ""));
			}
		});
		cmbChordChanges = bar.addComboBox("Set when the chord can change", () -> {
			if (player != null)
				setChordInterval(cmbChordChanges.getValue());
		}, 0, 1, 2, 4, 8, 16);
		cmbChordChanges.getSelectionModel().selectLast();
		cmbChordChanges.setConverter(new StringConverter<Integer>() {

			@Override
			public String toString(Integer val) {
				return "Per " + (val == cmbNotes.getValue() ? "Measure" :
						(val == 1 ? "Note" : val + " Notes"));
			}

			@Override
			public Integer fromString(String val) {
				return Integer.parseInt(val.replaceAll("\\D", ""));
			}
		});

		tempoSlider = bar.addSlider("Set the tempo", () -> constants.setTempo(tempoSlider.getValue()),
				MusicConstants.DEFAULT_TEMPO_MIN, MusicConstants.DEFAULT_TEMPO_MAX, constants.getTempo());
		tempoSlider.setOnMousePressed(evt -> tempoChanging());
		tempoSlider.setOnKeyPressed(evt -> tempoChanging());
		tempoSlider.setOnMouseReleased(evt -> tempoChanged());
		tempoSlider.setOnKeyReleased(evt -> tempoChanged());
		tempoIndicator = new Label();
		tempoIndicator.textProperty().bind(tempoSlider.valueProperty().asString("%.0f BPM"));
		tempoIndicator.setTextFill(Color.WHITE);
		bar.addNode(tempoIndicator);

		//Edit NotePlayState
		bar.addSeparator();
		bar.addToggleButton("Perma", permaToggle, null);
		bar.addToggleButton("Toggling", noteToggle, null);
		return bar;
	}

	private void tempoChanging() {
		if (tempoBefore == -1)
			tempoBefore = tempoSlider.getValue();
	}

	private void tempoChanged() {
		if (tempoBefore != -1 && tempoBefore != tempoSlider.getValue()) {
			pushMove(new TempoEntry(tempoBefore, tempoSlider.getValue()));
			tempoBefore = -1;
		}
	}

	/**
	 * Sets the window title to the opened file
	 * and whether or not it is modified.
	 */
	private void setTitle() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("ClearComposer - ");
		if (openFile == null)
			sb.append("Untitled");
		else
			sb.append(openFile.getAbsolutePath());
		if (changed)
			sb.append('*');
		saveDisabled.set(!changed);

		if (primaryStage != null)
			primaryStage.setTitle(sb.toString());
	}

	private void showToolbar(boolean shown)
	{
		//Only change state if it is hidden and we show it or vice versa.
		if (topBar.getParent() == topHidden ^ shown)
			return;

		if (shown) {
			hidingDelay.stop();
			barSlide.stop();
			barSlide.jumpTo(Duration.ZERO);
			topHidden.getChildren().remove(topBar);
			pane.setTop(topBar);
			topBar.setTranslateY(0);
		} else {
			pane.setTop(topHidden);
			topHidden.getChildren().add(topBar);
			topBar.setTranslateY(-topBar.getHeight());
		}
	}

	//*********************
	//* COMMAND METHODS
	//* 
	//* These are the handler procedure called
	//* when a command is selected in the GUI.
	//*********************	
	private void exitCommand() {
		if (!checkSave())
			return;
		MusicPlayer.turnOffNotes();

		primaryStage.setFullScreen(false);
		Preferences.userNodeForPackage(ClearComposer.class).putBoolean("maximized", primaryStage.isMaximized());
		primaryStage.setMaximized(false);

		//Save window positioning properties
		Preferences.userNodeForPackage(ClearComposer.class).putDouble("width", primaryStage.getWidth());
		Preferences.userNodeForPackage(ClearComposer.class).putDouble("height", primaryStage.getHeight());
		Preferences.userNodeForPackage(ClearComposer.class).putDouble("left", primaryStage.getX());
		Preferences.userNodeForPackage(ClearComposer.class).putDouble("topBar", primaryStage.getY());

		Platform.exit();
	}

	private boolean newCommand() {
		if (!checkSave())
			return false;

		openFile = null;
		constants = new MusicConstants();
		resetUI();
		initMusicSequencer();
		return true;
	}

	private boolean openCommand() {
		if (!checkSave())
			return false;
		File open = showFileChooser(true);
		if (open != null) {
			loadData(open);
			openFile = open;
			setTitle();
		}
		return true;
	}

	private void fullScreenCommand() {
		primaryStage.setFullScreen(!primaryStage.isFullScreen());
	}

	private void playCommand() {
		if (!btnPause.isButtonPressed() && player.getPlayState() != Status.RUNNING) //Only play if we are stopped
		{
			btnPlay.setButtonPressed(true);
			player.play();
		}
	}

	private void pausedCommand() {
		if (pauseToggle.get()) {
			btnPlay.setButtonPressed(true); //In case user presses pause first.
			if (player.getPlayState() == Status.RUNNING)
				player.pause();
		} else
			player.play();
	}

	private void redoCommand() {
		if (redoes.isEmpty())
			return;
		changed = true;
		AbstractEntry move = redoes.pop();
		undoes.push(move);
		updateMoveStack();

		move.redo();
	}

	private boolean saveAsCommand() {
		File save = showFileChooser(false);
		if (save == null)
			return false;

		openFile = save;
		saveData(openFile);
		return true;
	}

	private boolean saveCommand() {
		if (!changed)
			return true;

		if (openFile == null) {
			File save = showFileChooser(false);
			if (save == null)
				return false;
			openFile = save;
		}

		saveData(openFile);
		return true;
	}

	private void stopCommand() {
		btnPlay.setButtonPressed(false);
		btnPause.setButtonPressed(false);
		pauseToggle.setValue(false);
		player.stop();
	}

	private void undoCommand() {
		if (undoes.isEmpty())
			return;
		changed = true;
		AbstractEntry move = undoes.pop();
		redoes.push(move);
		updateMoveStack();

		move.undo();
	}

	private void updateMoveStack() {
		undoDisabled.set(undoes.isEmpty());
		redoDisabled.set(redoes.isEmpty());

		mnuEditUndo.setText(undoes.isEmpty() ? "_Undo" : "_Undo " + undoes.peek());
		mnuEditRedo.setText(redoes.isEmpty() ? "_Redo" : "_Redo " + redoes.peek());
		undoTooltip.setText(undoes.isEmpty() ? "Undo" : "Undo " + undoes.peek());
		redoTooltip.setText(redoes.isEmpty() ? "Redo" : "Redo " + redoes.peek());
	}

	//*********************
	//* LOAD/SAVE HELPER METHODS
	//* 
	//* These are helper methods used
	//* to load and save files.
	//*********************	
	private boolean checkSave() {
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

	/**
	 * Loads all track data from an byte stream
	 *
	 * @param is file to load from.
	 */
	private void loadData(InputStream is) {
		try (ObjectInputStream ois = new ObjectInputStream(is)) {
			constants = (MusicConstants) ois.readObject();
			resetUI();
			initMusicSequencer();
			player.loadTracks(ois);
			changed = false;
			setTitle();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while loading data", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(primaryStage);
			dlg.showAndWait();
		}
	}

	/**
	 * Loads all track data from a data file.
	 *
	 * @param f file to load from.
	 */
	private void loadData(File f) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
			constants = (MusicConstants) ois.readObject();
			resetUI();
			initMusicSequencer();
			player.loadTracks(ois);
			changed = false;
			setTitle();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while loading data", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(primaryStage);
			dlg.showAndWait();
		}
	}

	/**
	 * Saves all tracks to a data file.
	 *
	 * @param f file to save to.
	 */
	private void saveData(File f) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
			oos.writeObject(constants);
			player.saveTracks(oos);
			changed = false;
			setTitle();
		} catch (IOException e) {
			e.printStackTrace();
			Alert dlg = new Alert(Alert.AlertType.ERROR, "Error while saving data", ButtonType.OK);
			dlg.setHeaderText(null);
			dlg.setTitle("ClearComposer");
			dlg.initOwner(primaryStage);
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
	private File showFileChooser(boolean open) {
		player.stop();
		btnPause.setButtonPressed(false);
		btnPlay.setButtonPressed(false);

		FileChooser fileChooser = new FileChooser();

		if (!open && openFile != null)
			fileChooser.setInitialFileName(openFile.getName());
		fileChooser.setTitle(open ? "Open CC file" : "Save CC file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CC File", "*.ccp"));

		String defPath = Preferences.userNodeForPackage(ClearComposer.class).get("CCDefaultPath", null);
		File defFilePath;
		if (defPath == null || !(defFilePath = new File(defPath)).exists())
			defFilePath = new File(DEFAULT_FOLDER_HOME);
		fileChooser.setInitialDirectory(defFilePath);

		File result;
		if (open)
			result = fileChooser.showOpenDialog(primaryStage);
		else
			result = fileChooser.showSaveDialog(primaryStage);

		if (result != null)
			Preferences.userNodeForPackage(ClearComposer.class).put("CCDefaultPath", result.getParent());

		return result;
	}

	public static void main(String[] args) {
		launch(args);
	}
}