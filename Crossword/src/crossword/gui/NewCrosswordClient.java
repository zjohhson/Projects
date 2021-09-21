/* Copyright (c) 2019-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package crossword.gui;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import crossword.MinimalPuzzle;
import crossword.Player;

/**
 * A mutable data type representing a client to our system that hosts collaborative multi-player crossword puzzle games.
 */
public class NewCrosswordClient {

    private static final int CANVAS_WIDTH = 1000;
    private static final int CANVAS_HEIGHT = 750;
    private static final int SMALL_BUTTON_WIDTH = 75;
    private static final int GENERAL_BUTTON_WIDTH = 150;
    private static final int GENERAL_BUTTON_HEIGHT = 30;
    private static final int RETURN_BUTTON_X = 15;
    private static final int RETURN_BUTTON_Y = 15;
    private static final int LOAD_PUZZLE_BUTTON_START_X = 45;
    private static final int LOAD_PUZZLE_BUTTON_START_Y = 100;
    private static final int DEFAULT_FONT_SIZE = 20;
    private static final int GAME_MESSAGE_X = 25;
    private static final int GAME_MESSAGE_Y = 495;
    private static final int GAME_MESSAGE_WIDTH = 600;
    private static final int NEW_PANE_START = 25;
    private static final int NEW_PANE_WIDTH = 1000;
    private static final int NEW_PANE_HEIGHT = 500;
    private static final String HOSTNAME = "http://localhost:4949";
    
    private static String playerID = "";
    private static Player player;
    private static List<JComponent> allCreateGameButtons = new ArrayList<>();
    private static List<JComponent> allJoinGameButtons = new ArrayList<>();
    private static List<JComponent> createPlayerComponents = new ArrayList<>();
    private static List<JComponent> lobbyComponents = new ArrayList<>();
    private static List<JComponent> allReturnButtons = new ArrayList<>();
    private static List<JComponent> puzzleGameComponents = new ArrayList<>();
    private static List<JComponent> puzzleGameMessageComponents = new ArrayList<>();
    private static String currentGameID = "";
    
    // AF(playerID, player, allCreateGameButtons, allJoinGameButtons, createPlayerComponents, lobbyComponents, allReturnButtons, puzzleGameComponents, puzzleGameMessageComponents)
    // -> a Client whose name (or ID) is playerID. 
    //      -allCreateGameButtons and allJoinGameButtons contain the buttons to create a new game from a given puzzle and to join an existing game, respectively. 
    //      -allReturnButtons contains the buttons to return to the lobby from any game. 
    //      -createPlayerComponents, lobbyComponents, puzzleGameComponents, and puzzleGameMessageComponents contain the text boxes for creating a player, entering a game from the lobby, and guessing a word in a game.
    // Rep Invariant:
    // - true
    // SRE:
    // - no methods return any of our fields to the client, so rep never exposed
    //
    // Thread safety:
    // - only one client at a time, so there is a never an issue of concurrency
    /**
     * Sets client to newPlayer.
     * @param newPlayer Player representing the player we are setting our client to
     */
    private static void setPlayer(Player newPlayer) {
        player = newPlayer;
        playerID = newPlayer.getID();
    }
    
    /**
     * Sets components to either be visible or invisible to the client
     * @param components components we are wanting to make visible/invisible to the client.
     * @param bool representing whether the component is visible (true) or not (false)
     */
    private static void setVisibility(List<JComponent> components, boolean bool) {
        for (JComponent component : components) {
            component.setVisible(bool);
        }
    }

    /**
     * Creates button to create a new game from a puzzle.
     * @param window represents the JFrame to which the canvas is held in
     * @param canvas represents the JPanel interface to which we want to add the button
     * @param puzzleName String representing the name of the puzzle we want to parse to create a new game
     * @param puzzleGameComponents list of components representing the components (e.g. text boxes) of the game
     * @param watchRunnable the runnable for a separate thread on which this new game will watch from
     * @param buttonX x coordinate of where the button will be positioned
     * @param buttonY y coordinate of where the button will be positioned
     * @return JButton representing a button that will be clicked by the client to create new game from puzzleName
     */
    private static JButton createNewPuzzleGameButton(JFrame window, JPanel currentPanel, NewCrosswordCanvas canvas, String puzzleName, Runnable watchRunnable, int buttonX, int buttonY) {

        JButton loadNewPuzzleButton = new JButton(puzzleName);
        loadNewPuzzleButton.setBounds(buttonX, buttonY, GENERAL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        allCreateGameButtons.add(loadNewPuzzleButton);
        loadNewPuzzleButton.addActionListener((event) -> {
            try {
                final URL loadNewPuzzleURL = new URL("http://localhost:4949/createGame/" + playerID + "/" + puzzleName);
                final InputStream input = loadNewPuzzleURL.openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
                String puzzleInStringFormat = response[0];
                String allPlayersString = response[1];
                List<String> allActivePlayers = Arrays.asList(allPlayersString.split(","));
                MinimalPuzzle minimalPuzzle = MinimalPuzzle.parseFromString(puzzleInStringFormat);
                setVisibility(allCreateGameButtons, false);
                setVisibility(allReturnButtons, false);
                setVisibility(puzzleGameComponents, true);
                
                window.setComponentZOrder(canvas, 0);
                currentPanel.setVisible(false);
                canvas.setMinimalPuzzle(Optional.of(minimalPuzzle));
                canvas.setActivePlayerList(allActivePlayers);
                canvas.repaint();
                
                new Thread(watchRunnable).start();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return loadNewPuzzleButton;
    }
    
    /**
     * Creates button to join an existing game.
     * @param window represents the JFrame to which the canvas is held in
     * @param canvas represents the JPanel interface to which we want to add the button
     * @param gameID String representing the name of the game we are creating a button for
     * @param watchThread the thread on which this new game will run
     * @param buttonX x coordinate of where the button will be positioned
     * @param buttonY y coordinate of where the button will be positioned
     * @return JButton representing a button that will be clicked by the client to join existing game labeled gameID
     */
    private static JButton createExistingPuzzleGameButton(JFrame window, JPanel currentPanel, NewCrosswordCanvas canvas, String gameID, Runnable watchRunnable, int buttonX, int buttonY) {

        JButton joinExistingGameButton = new JButton(gameID);
        joinExistingGameButton.setBounds(buttonX, buttonY, GENERAL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        allJoinGameButtons.add(joinExistingGameButton);
        joinExistingGameButton.addActionListener((event) -> {
            try {
                final URL joinExistingGameURL = new URL(HOSTNAME + "/joinExistingGame/" + playerID + "/" + gameID);
                final InputStream input = joinExistingGameURL.openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                
                String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
                String puzzleInStringFormat = response[0];
                String allPlayersString = response[1];
                List<String> allActivePlayers = Arrays.asList(allPlayersString.split(","));
                MinimalPuzzle minimalPuzzle = MinimalPuzzle.parseFromString(puzzleInStringFormat);
                setVisibility(allJoinGameButtons, false);
                setVisibility(allReturnButtons, false);
                setVisibility(puzzleGameComponents, true);
                
                window.setComponentZOrder(canvas, 0);
                currentPanel.setVisible(false);
                canvas.setMinimalPuzzle(Optional.of(minimalPuzzle));
                canvas.setActivePlayerList(allActivePlayers);
                canvas.repaint();
                
                new Thread(watchRunnable).start();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        });     
        return joinExistingGameButton;
        
    }
    /**
     * Start a Crossword Extravaganza client.
     * @param args The command line arguments should include only the server address.
     */
    public static void main(String[] args) {
        launchGameWindow();

    }
    
    /**
     * Code to display the user interface for the Crossword Extravaganza. The client is navigated through specific "pages" from top to bottom
     * 1) Create player: Textbox and start button for initiating a client's playerID
     * 2) Lobby: Create game/Join game buttons loads the available puzzles/games to create or join
     * 3) i) Creating game: List of buttons with puzzle names that can be clicked to make the puzzle
     *   ii) Joining game: List of games with gameIDs that can be clicked to join that game
     * 4) Puzzle: Two textboxs to type in the crossword index and a client's guess respectively and buttons
     *            to submit or erase a guess and check the entire puzzle. An exit button to return to the lobby
     *            Guessing --> Both crossword index and guess textbox must be filled with valid entries and then press submit
     *            Erase --> Only crossword index must be filled with valid entry and press erase
     *            Check --> No textbox needs to be filled, press check
     */
    private static void launchGameWindow() {
       
        final Player player;
        
        // Setup client window, canvas, and panel
        NewCrosswordCanvas canvas = new NewCrosswordCanvas();
        final int puzzleSize = 300;
        canvas.setSize(puzzleSize, puzzleSize);
        
        JFrame window = new JFrame("Crossword Client");
        window.setLayout(null);
        window.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        window.add(canvas);        
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBounds(0,0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Components for the puzzle game
        JTextField indexTextBox = new JTextField();
        JTextField guessTextBox = new JTextField();
        JButton submitButton = new JButton("Submit");
        JLabel submitErrorLabel = new JLabel("Please enter a valid index for your guess and an alphabetical word with the correct length for that index");
        JButton eraseButton = new JButton("Erase");
        JLabel eraseErrorLabel = new JLabel("Please enter a valid index to erase from");
        JButton checkButton = new JButton("Check");
        JLabel puzzleIsCompleteLabel = new JLabel("You have completed the whole puzzle!");
        JButton exitButton = new JButton("Exit");

        
        
        Runnable watchRunnable = new Runnable() {
            public void run() {
                try {
                    boolean playerInGame = true;
                    while (playerInGame) {
                        final URL watchGameURL = new URL(HOSTNAME + "/watchGame/" + playerID);
                        final InputStream input = watchGameURL.openStream();
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));                        
                        
                        String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
                        String puzzleInStringFormat = response[0];
                        String allPlayersString = "";
                        String isComplete = "false";
                        final int fullResponseLength = 3;
                        if (response.length == fullResponseLength) {
                            allPlayersString = response[1];
                            isComplete = response[2];
                        }
                        List<String> allActivePlayers = Arrays.asList(allPlayersString.split(","));
                        if (!allActivePlayers.contains(playerID)) {
                            playerInGame = false;
                            break;
                        }
                        
                        MinimalPuzzle minimalPuzzle = MinimalPuzzle.parseFromString(puzzleInStringFormat);
                        setVisibility(allJoinGameButtons, false);
                        setVisibility(allReturnButtons, false);
                        setVisibility(puzzleGameComponents, true);
                        canvas.setMinimalPuzzle(Optional.of(minimalPuzzle));
                        canvas.setActivePlayerList(allActivePlayers);
                        
                        if (isComplete.equals("true")) {
                            puzzleIsCompleteLabel.setVisible(true);
                        }
                        canvas.repaint();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
        };
        
        // Components for new player to set their playerID
        JLabel createPlayerDescription = new JLabel("Please enter your name to get started!");
        JTextField createPlayerTextbox = new JTextField();
        JButton createPlayerButton = new JButton("Start");
        
        final int createPlayerWidth = 220;
        final int createTextboxWidth = 600;
        final int createPlayerButtonWidth = 100;
        final int totalCreatePlayerWidth = createTextboxWidth + createPlayerButtonWidth;
        
        createPlayerDescription.setBounds(CANVAS_WIDTH/2-createPlayerWidth/2, CANVAS_HEIGHT/2-(GENERAL_BUTTON_HEIGHT + GENERAL_BUTTON_HEIGHT/2), createPlayerWidth, GENERAL_BUTTON_HEIGHT);
        createPlayerTextbox.setBounds(CANVAS_WIDTH/2-totalCreatePlayerWidth/2, CANVAS_HEIGHT/2-GENERAL_BUTTON_HEIGHT/2, createTextboxWidth, GENERAL_BUTTON_HEIGHT);
        createPlayerButton.setBounds(CANVAS_WIDTH/2+totalCreatePlayerWidth/2-createPlayerButtonWidth, CANVAS_HEIGHT/2-GENERAL_BUTTON_HEIGHT/2, createPlayerButtonWidth, GENERAL_BUTTON_HEIGHT);
        
        createPlayerComponents = List.of(createPlayerDescription, createPlayerTextbox, createPlayerButton);
        createPlayerTextbox.setFont(new Font("Arial", Font.BOLD, DEFAULT_FONT_SIZE));
        createPlayerButton.addActionListener((event) -> {
            try {
                String newPlayerID = createPlayerTextbox.getText();
                if (newPlayerID.length() != 0) {
                    Player newPlayer = new Player(newPlayerID);
                    setPlayer(newPlayer);
                    setVisibility(createPlayerComponents, false);
                    setVisibility(lobbyComponents, true);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
        
        // Components for player to choose to join/create a game (Lobby)
        JButton joinGameButton = new JButton("Join Game");
        JButton createGameButton = new JButton("Create Game");
        JButton returnFromJoinButton = new JButton("Return"); // Buttons to go back to lobby after clicking above buttons
        JButton returnFromCreateButton = new JButton("Return");
        final int joinAndCreateButtonDelta = 35;

        joinGameButton.setBounds(CANVAS_WIDTH/2-GENERAL_BUTTON_WIDTH/2, CANVAS_HEIGHT/2-joinAndCreateButtonDelta, GENERAL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        createGameButton.setBounds(CANVAS_WIDTH/2-GENERAL_BUTTON_WIDTH/2, CANVAS_HEIGHT/2+joinAndCreateButtonDelta, GENERAL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        returnFromCreateButton.setBounds(RETURN_BUTTON_X, RETURN_BUTTON_Y, SMALL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        returnFromJoinButton.setBounds(RETURN_BUTTON_X, RETURN_BUTTON_Y, SMALL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        
        lobbyComponents = List.of(joinGameButton, createGameButton);
        allReturnButtons = List.of(returnFromJoinButton, returnFromCreateButton);
        
        returnFromJoinButton.addActionListener((event) -> {
            window.setComponentZOrder(contentPane, 0);
            setVisibility(allReturnButtons,false);
            setVisibility(allJoinGameButtons, false);
            setVisibility(lobbyComponents, true); 
         });
        returnFromCreateButton.addActionListener((event) -> {
            window.setComponentZOrder(contentPane, 0);
            setVisibility(allReturnButtons,false);
            setVisibility(allCreateGameButtons, false);
            setVisibility(lobbyComponents, true);
         });
        joinGameButton.addActionListener((event) -> {
            try {
                final URL getAllActiveGamesURL = new URL (HOSTNAME + "/getAllActiveGames/" + playerID);
                final InputStream input = getAllActiveGamesURL.openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                String[] allGameIDs = reader.readLine().split(",");
                JPanel newContentPane = new JPanel();
                newContentPane.setLayout(null);
                newContentPane.setBounds(NEW_PANE_START, NEW_PANE_START, NEW_PANE_WIDTH, NEW_PANE_HEIGHT);
                returnFromJoinButton.setVisible(true);
                int buttonX = LOAD_PUZZLE_BUTTON_START_X;
                int buttonY = LOAD_PUZZLE_BUTTON_START_Y;
                final int xDelta = 163;
                final int yDelta = 50;

                for (String gameID: allGameIDs) {
                    if (!gameID.equals("")) {
                        JButton createExistingGameButton = createExistingPuzzleGameButton(window, newContentPane, canvas, gameID, watchRunnable, buttonX, buttonY);
                        newContentPane.add(createExistingGameButton);
                        buttonX = (buttonX + xDelta < CANVAS_HEIGHT - LOAD_PUZZLE_BUTTON_START_X) ? buttonX + xDelta : LOAD_PUZZLE_BUTTON_START_X;
                        buttonY = (buttonX == LOAD_PUZZLE_BUTTON_START_X) ? buttonY + yDelta : buttonY;

                    }
                }
                JPanel currentContentPane = (JPanel) window.getContentPane();
                setVisibility(lobbyComponents, false);
                currentContentPane.add(newContentPane);
                window.setComponentZOrder(newContentPane, 0);
                currentContentPane.revalidate(); 
                currentContentPane.repaint();
                window.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        createGameButton.addActionListener((event) -> {
            try {
                final URL getAllPuzzlesURL = new URL(HOSTNAME + "/getAllPuzzles/" + playerID );
                final InputStream input = getAllPuzzlesURL.openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                String[] allPuzzles = reader.readLine().split(","); 
                JPanel newContentPane = new JPanel();
                newContentPane.setLayout(null);
                newContentPane.setBounds(NEW_PANE_START, NEW_PANE_START, NEW_PANE_WIDTH, NEW_PANE_HEIGHT);
                returnFromCreateButton.setVisible(true);
                int buttonX = LOAD_PUZZLE_BUTTON_START_X;
                int buttonY = LOAD_PUZZLE_BUTTON_START_Y;
                final int xDelta = 175;
                final int yDelta = 50;
                for (String puzzleName: allPuzzles) {
                    if (!puzzleName.equals("")) {
                        JButton createNewPuzzleGameButton = createNewPuzzleGameButton(window, newContentPane, canvas, puzzleName, watchRunnable, buttonX, buttonY);
                        newContentPane.add(createNewPuzzleGameButton);
                        buttonX = (buttonX + xDelta < CANVAS_HEIGHT - LOAD_PUZZLE_BUTTON_START_X) ? buttonX + xDelta : LOAD_PUZZLE_BUTTON_START_X;
                        buttonY = (buttonX == LOAD_PUZZLE_BUTTON_START_X) ? buttonY + yDelta : buttonY;
                    }
                }
                JPanel currentContentPane = (JPanel) window.getContentPane();
                setVisibility(lobbyComponents, false);
                currentContentPane.add(newContentPane);
                window.setComponentZOrder(newContentPane, 0);
                currentContentPane.revalidate(); 
                currentContentPane.repaint();
                window.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        
        // Components for the puzzle game
        String instructionsText = "<html><h2>How to play</h2> <p>Guessing: Enter the clue number AND your guess into the textboxes under Index and Guess and then press the SUBMIT button. <br />   Erasing: Enter ONLY the clue number into the textbox under Index and press the Erase button.  <br /> Checking: Press the check button if you want to check if your guesses are correct.<br /> Exit: Press the exit button if you would like to leave this puzzle. <br/></p> </html>";
        JLabel instructions = new JLabel(instructionsText);
        JLabel indexDescription = new JLabel("Index");
        JLabel guessDescription = new JLabel("Guess");
        JLabel guessEntryError = new JLabel("Please enter both a valid index AND guess to guess.");
        JLabel indexEntryError = new JLabel("Please enter a valid index to erase from.");
        
        // Entry description row
        final int indexWidth = 40;
        final int guessWidth = 200;
        final int guessWidthX = 70;
        final int entryDescriptionY = 415;
        indexDescription.setBounds(GAME_MESSAGE_X, entryDescriptionY, indexWidth, GENERAL_BUTTON_HEIGHT);
        guessDescription.setBounds(guessWidthX, entryDescriptionY, guessWidth, GENERAL_BUTTON_HEIGHT);
        // Entry box row
        final int submitButtonX = 280;
        final int buttonDelta = 5;
        final int entryBoxY = 450;
        guessTextBox.setBounds(guessWidthX, entryBoxY, guessWidth, GENERAL_BUTTON_HEIGHT);
        indexTextBox.setBounds(GAME_MESSAGE_X, entryBoxY, indexWidth, GENERAL_BUTTON_HEIGHT);
        submitButton.setBounds(submitButtonX, entryBoxY, SMALL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        eraseButton.setBounds(submitButtonX+SMALL_BUTTON_WIDTH+buttonDelta, entryBoxY, SMALL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        checkButton.setBounds(submitButtonX+2*(SMALL_BUTTON_WIDTH+buttonDelta), entryBoxY, SMALL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);
        // Client messages row
        submitErrorLabel.setBounds(GAME_MESSAGE_X, GAME_MESSAGE_Y, GAME_MESSAGE_WIDTH, GENERAL_BUTTON_HEIGHT);
        eraseErrorLabel.setBounds(GAME_MESSAGE_X, GAME_MESSAGE_Y, GAME_MESSAGE_WIDTH, GENERAL_BUTTON_HEIGHT);
        puzzleIsCompleteLabel.setBounds(GAME_MESSAGE_X, GAME_MESSAGE_Y, GAME_MESSAGE_WIDTH, GENERAL_BUTTON_HEIGHT);
        guessEntryError.setBounds(GAME_MESSAGE_X, GAME_MESSAGE_Y, GAME_MESSAGE_WIDTH, GENERAL_BUTTON_HEIGHT);
        indexEntryError.setBounds(GAME_MESSAGE_X, GAME_MESSAGE_Y, GAME_MESSAGE_WIDTH, GENERAL_BUTTON_HEIGHT);
        // Instruction row
        final int instructionsY = 530;
        final int instructionsWidth = 800;
        final int instructionsHeight = 150;
        instructions.setBounds(GAME_MESSAGE_X, instructionsY, instructionsWidth, instructionsHeight);

        exitButton.setBounds(RETURN_BUTTON_X, RETURN_BUTTON_Y, SMALL_BUTTON_WIDTH, GENERAL_BUTTON_HEIGHT);

        puzzleGameComponents = List.of(exitButton, indexDescription, indexTextBox, guessDescription, guessTextBox, submitButton, eraseButton, checkButton, instructions);
        puzzleGameMessageComponents = List.of(submitErrorLabel, eraseErrorLabel, puzzleIsCompleteLabel);
        indexTextBox.setFont(new Font("Arial", Font.BOLD, DEFAULT_FONT_SIZE));
        guessTextBox.setFont(new Font("Arial", Font.BOLD, DEFAULT_FONT_SIZE));
        
        submitButton.addActionListener((event) -> {
            try {
                setVisibility(puzzleGameMessageComponents,false);
                String indexString = indexTextBox.getText();
                String guessString = guessTextBox.getText();
                if (indexString.length() > 0 && indexString.matches("\\d+") && guessString.length() > 0 && guessString.matches("[A-Za-z]+")) {
                    final URL getAllActiveGamesURL = new URL (HOSTNAME + "/guess/" + playerID + "/" + indexString + "/" + guessString);
                    final InputStream input = getAllActiveGamesURL.openStream();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                    String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
                    final String puzzleInStringFormat = response[0];
                    if (response.length > 1) {
                        submitErrorLabel.setVisible(true);
                    }
                    MinimalPuzzle updatedMinimalPuzzle = MinimalPuzzle.parseFromString(puzzleInStringFormat);
                    canvas.setMinimalPuzzle(Optional.of(updatedMinimalPuzzle));
                    indexTextBox.setText("");
                    guessTextBox.setText("");
                    canvas.repaint();
                } else {
                    submitErrorLabel.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        eraseButton.addActionListener((event) -> {
            try {
                setVisibility(puzzleGameMessageComponents,false);
                String indexString = indexTextBox.getText();
                if (indexString.length() > 0 && indexString.matches("\\d+")) {
                    final URL getAllActiveGamesURL = new URL (HOSTNAME + "/erase/" + playerID + "/" + indexString);
                    final InputStream input = getAllActiveGamesURL.openStream();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                    String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
                    String puzzleInStringFormat = response[0];
                    if (response.length > 1) {
                        eraseErrorLabel.setVisible(true);
                    }
                    MinimalPuzzle updatedMinimalPuzzle = MinimalPuzzle.parseFromString(puzzleInStringFormat);
                    canvas.setMinimalPuzzle(Optional.of(updatedMinimalPuzzle));
                    indexTextBox.setText("");
                    canvas.repaint();
                } else {
                    eraseErrorLabel.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        checkButton.addActionListener((event) -> {
            try {
                setVisibility(puzzleGameMessageComponents,false);
                final URL getAllActiveGamesURL = new URL (HOSTNAME + "/check/" + playerID);
                final InputStream input = getAllActiveGamesURL.openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
                String puzzleInStringFormat = response[0];
                String isComplete = response[1];
                if (isComplete.equals("true")) {
                    puzzleIsCompleteLabel.setVisible(true);
                }
                MinimalPuzzle updatedMinimalPuzzle = MinimalPuzzle.parseFromString(puzzleInStringFormat);
                canvas.setMinimalPuzzle(Optional.of(updatedMinimalPuzzle));
                canvas.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        exitButton.addActionListener((event) -> {
            try {
                final URL getAllActiveGamesURL = new URL (HOSTNAME + "/exitGame/" + playerID);
                final InputStream input = getAllActiveGamesURL.openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
                canvas.setMinimalPuzzle(Optional.empty());
                
                window.setComponentZOrder(contentPane, 0);
                exitButton.setVisible(false);
                setVisibility(puzzleGameComponents, false);
                setVisibility(puzzleGameMessageComponents,false);
                setVisibility(lobbyComponents, true);
                canvas.repaint();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // add all static components onto the initial content pane
        for (JComponent component: createPlayerComponents) {contentPane.add(component);}
        for (JComponent component: lobbyComponents) {contentPane.add(component);}
        setVisibility(lobbyComponents, false);
        for (JComponent component: allReturnButtons) {contentPane.add(component);}
        setVisibility(allReturnButtons, false);
        for (JComponent component : puzzleGameComponents) {contentPane.add(component);}
        setVisibility(puzzleGameComponents, false);
        for (JComponent component: puzzleGameMessageComponents) {contentPane.add(component);}
        setVisibility(puzzleGameMessageComponents, false);

        
        window.add(contentPane);

        window.getContentPane().add(contentPane);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}