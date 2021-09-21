package crossword;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Tests for the WebServer abstract data type.
 */
public class WebServerTest {
    
    private static final int SUCCESS = 200;
    private static final int ERROR = 404;
    
    // Testing strategy
    //
    //
    // handleGetAllPuzzles(): (/getAllPuzzles/<playerID>)
    //   1 puzzle; >1 puzzles
    //   valid; invalid (no playerID specified)
    //
    // handleCreateGame(): (/createGame/<playerID>/<puzzle>)
    //   valid; invalid (no playerID specified); invalid (no puzzle specified); invalid (puzzle not in directory)   
    //
    // handleGetAllActiveGames(): (/getAllActiveGames/<playerID>)
    //   number of active games: 0; 1; >1;
    //   valid; invalid (no playerID specified)
    //
    // handleJoinExistingGame(): (/joinExistingGame/<playerID>/<gameID>)
    //   valid; invalid (no playerID specified); invalid (no gameID specified); invalid (invalid gameID)
    //
    // handleExitGame(): (/exitGame/<playerID>)
    //   valid; invalid (no playerID specified); invalid (playerID not in any game)
    //
    // handleGuess(): (/guess/<playerID>/index/guess)
    //   playerID: valid; invalid (no playerID specified); invalid (playerID not in any game)
    //   index: valid; invalid (out of bounds); invalid (not a number)
    //   guess: valid; invalid (wrong length); invalid (not all characters)
    //
    // handleErase(): (/erase/<playerID>/index)
    //   playerID: valid; invalid (no playerID specified); invalid (playerID not in any game)
    //   index: valid; invalid (out of bounds); invalid (not a number)
    //
    // handleCheckPuzzle(): (/check/<playerID>)
    //    playerID: valid; invalid (no playerID specified); invalid (playerID not in any game)
    //
    // handleWatchGame(): (/watchGame/<playerID>)
    //    playerID: valid; invalid (no playerID specified); invalid (playerID not in any game)
    //
    

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    
    // handleGetAllPuzzles() tests
    
    // covers handleGetAllPuzzles(): valid, 1 puzzle, output 200
    @Test
    public void testGetAllPuzzlesOnePuzzle() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // Look up
        final URL getAllPuzzlesURL = new URL("http://localhost:" + server.port() + "/getAllPuzzles/" + "giannis");
        final HttpURLConnection connection = (HttpURLConnection) getAllPuzzlesURL.openConnection();
        assertEquals(200, connection.getResponseCode(), "Expected 200 Success response code");
        
        final InputStream input = getAllPuzzlesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        String[] allPuzzles = allLines.split(",");
        assertEquals(Set.of("simple"), Set.of(allPuzzles));
        
        server.stop();
    }
    
    // covers handleGetAllPuzzles(): valid, >1 puzzles, output 200
    @Test
    public void testGetAllPuzzlesTwoPuzzles() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        String puzzlepath2 = "puzzles/simple2.puzzle";
        Puzzle validPuzzle2 = Puzzle.parseFromFile(puzzlepath2);
        String puzzleName2 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath2);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1, puzzleName2, validPuzzle2);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1, puzzleName2, puzzlepath2);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // Look up
        final URL getAllPuzzlesURL = new URL("http://localhost:" + server.port() + "/getAllPuzzles/" + "giannis");
        final HttpURLConnection connection = (HttpURLConnection) getAllPuzzlesURL.openConnection();
        assertEquals(SUCCESS, connection.getResponseCode(), "Expected 200 Success response code");
        
        final InputStream input = getAllPuzzlesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        String[] allPuzzles = allLines.split(",");
        assertEquals(Set.of("simple", "simple2"), Set.of(allPuzzles));
                
        server.stop();
    }
    
    // covers handleGetAllPuzzles(): invalid (no playerID), output 404
    @Test
    public void testGetAllPuzzlesNoPlayerID() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // Look up
        final URL invalidGet = new URL("http://localhost:" + server.port() + "/getAllPuzzles/");
        final HttpURLConnection connection = (HttpURLConnection) invalidGet.openConnection();
        assertEquals(ERROR, connection.getResponseCode(), "Expected 404 Error response code");
        server.stop();
    }
    
    
    // handleCreateGame() tests
    
    // covers handleCreateGame(): valid, output 200
    @Test
    public void testCreateGameValid() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "giannis/" + puzzleName1);
        final HttpURLConnection connection = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, connection.getResponseCode(), "Expected 200 Success response code");
        
        server.stop();
    }
    
    // covers handleCreateGame(): invalid (no player ID), output 404
    @Test
    public void testCreateGameNoPlayerID() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create game (no player ID)
        final URL invalidCreateGame = new URL("http://localhost:" + server.port() + "/createGame/" + puzzleName1);
        final HttpURLConnection connection = (HttpURLConnection) invalidCreateGame.openConnection();
        assertEquals(ERROR, connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    // covers handleCreateGame(): invalid (no puzzle specified), output 404
    @Test
    public void testCreateGameNoPuzzle() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create game (no puzzle specified)
        final URL invalidCreateGame = new URL("http://localhost:" + server.port() + "/createGame/" + "giannis/");
        final HttpURLConnection connection = (HttpURLConnection) invalidCreateGame.openConnection();
        assertEquals(ERROR, connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    // covers handleCreateGame(): invalid (puzzle not in directory), output 404
    @Test
    public void testCreateGamePuzzleNotInDirectory() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create game (puzzle not in directory)
        final URL invalidCreateGame = new URL("http://localhost:" + server.port() + "/createGame/" + "giannis/" + "nonexistent");
        final HttpURLConnection connection = (HttpURLConnection) invalidCreateGame.openConnection();
        assertEquals(ERROR, connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    
    // handleGetAllActiveGames() tests
    
    
    // covers handleGetAllActiveGames(): valid, 0 games
    @Test
    public void testGetAllGamesNoGames() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // get all games (no games created)
        final URL getGamesURL = new URL("http://localhost:" + server.port() + "/getAllActiveGames/" + "giannis");
        final HttpURLConnection connection = (HttpURLConnection) getGamesURL.openConnection();
        assertEquals(SUCCESS, connection.getResponseCode(), "Expected 200 Success response code");
        
        final InputStream input = getGamesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        assertEquals("", allLines);
        
        server.stop();
    }
    
    // covers handleGetAllActiveGames(): valid, 1 game
    @Test
    public void testGetAllGamesOneGame() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL1 = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL1.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        
        // get all games
        final URL getGamesURL = new URL("http://localhost:" + server.port() + "/getAllActiveGames/" + "giannis");
        final HttpURLConnection connection2 = (HttpURLConnection) getGamesURL.openConnection();
        assertEquals(SUCCESS, connection2.getResponseCode(), "Expected 200 Success response code");
        
        final InputStream input = getGamesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        String[] allGameIDs = allLines.split(",");
        assertEquals(1, allGameIDs.length);
        
        server.stop();
    }
    
    
    // covers handleGetAllActiveGames(): valid, 2 games
    @Test
    public void testGetAllGamesTwoGames() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL1 = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL1.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        // create second game
        final URL createGameURL2 = new URL("http://localhost:" + server.port() + "/createGame/" + "zach/" + puzzleName1);
        final HttpURLConnection connection2 = (HttpURLConnection) createGameURL2.openConnection();
        assertEquals(SUCCESS, connection2.getResponseCode(), "Expected 200 Success response code");
        
        // get all games
        final URL getGamesURL = new URL("http://localhost:" + server.port() + "/getAllActiveGames/" + "giannis");
        final HttpURLConnection connection3 = (HttpURLConnection) getGamesURL.openConnection();
        assertEquals(SUCCESS, connection3.getResponseCode(), "Expected 200 Success response code");
        
        final InputStream input = getGamesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        String[] allGameIDs = allLines.split(",");
        assertEquals(2, allGameIDs.length);
        
        server.stop();
    }
    
    // covers handleGetAllActiveGames(): invalid (no playerID)
    @Test
    public void testGetAllGamesInvalid() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // invalid get all games (no playerID field in URL)
        final URL invalidGetGames = new URL("http://localhost:" + server.port() + "/getAllActiveGames/");
        final HttpURLConnection connection = (HttpURLConnection) invalidGetGames.openConnection();
        assertEquals(ERROR, connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    
    // handleJoinExistingGame() tests
    
    
    // covers handleJoinExistingGame(): valid, 1 game
    @Test
    public void testJoinGameValid() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL1 = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL1.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        
        // get all games (one game)
        final URL getGamesURL = new URL("http://localhost:" + server.port() + "/getAllActiveGames/" + "giannis");
        final HttpURLConnection connection2 = (HttpURLConnection) getGamesURL.openConnection();
        assertEquals(SUCCESS, connection2.getResponseCode(), "Expected 200 Success response code");
        // extract gameID
        final InputStream input = getGamesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        String[] allGameIDs = allLines.split(",");
        final String gameID = allGameIDs[0];
        
        // join the game
        final URL joinGameURL = new URL("http://localhost:" + server.port() + "/joinExistingGame/" + "giannis/" + gameID);
        final HttpURLConnection connection3 = (HttpURLConnection) joinGameURL.openConnection();
        assertEquals(SUCCESS, connection3.getResponseCode(), "Expected 200 Success response code");
        
        server.stop();
    }    
    
    // covers handleJoinExistingGame(): invalid (no playerID), invalid (no gameID specified), invalid (invalid gameID)
    @Test
    public void testJoinGameInvalid() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL1 = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL1.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        
        // get all games (one game)
        final URL getGamesURL = new URL("http://localhost:" + server.port() + "/getAllActiveGames/" + "giannis");
        final HttpURLConnection connection2 = (HttpURLConnection) getGamesURL.openConnection();
        assertEquals(SUCCESS, connection2.getResponseCode(), "Expected 200 Success response code");
        // extract gameID
        final InputStream input = getGamesURL.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String allLines = reader.lines().collect(Collectors.joining());
        String[] allGameIDs = allLines.split(",");
        final String gameID = allGameIDs[0];
        
        // join the game (no playerID specified)        
        final URL invalidJoinGame3 = new URL("http://localhost:" + server.port() + "/joinExistingGame/" + gameID);
        final HttpURLConnection connection3 = (HttpURLConnection) invalidJoinGame3.openConnection();
        assertEquals(ERROR, connection3.getResponseCode(), "Expected 404 Error response code");
        
        // join the game (no gameID specified)
        final URL invalidJoinGame4 = new URL("http://localhost:" + server.port() + "/joinExistingGame/" + "giannis/");
        final HttpURLConnection connection4 = (HttpURLConnection) invalidJoinGame4.openConnection();
        assertEquals(ERROR, connection4.getResponseCode(), "Expected 404 Error response code");
        
        // join the game (invalid gameID)
        final String invalidGameID = "5";
        final URL invalidJoinGame5 = new URL("http://localhost:" + server.port() + "/joinExistingGame/" + "giannis/" + invalidGameID);
        final HttpURLConnection connection5 = (HttpURLConnection) invalidJoinGame5.openConnection();
        assertEquals(ERROR, connection5.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    
    // handleExitGame() tests
    
    
    // covers handleExitGame(): valid
    @Test
    public void testExitGameValid() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // exit game
        final URL exitGameURL = new URL("http://localhost:" + server.port() + "/exitGame/" + "donald");
        final HttpURLConnection connection2 = (HttpURLConnection) exitGameURL.openConnection();
        assertEquals(SUCCESS, connection2.getResponseCode(), "Expected 200 Success response code");       
        
        server.stop();
    }
    
    // covers handleExitGame(): invalid (no playerID specified), invalid (playerID not in any game)
    @Test
    public void testExitGameInvalid() throws IOException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Successr response code");
        
        // exit game (no playerID specified)
        final URL exitGameURL2 = new URL("http://localhost:" + server.port() + "/exitGame/");
        final HttpURLConnection connection2 = (HttpURLConnection) exitGameURL2.openConnection();
        assertEquals(ERROR, connection2.getResponseCode(), "Expected 404 Error response code");
        
        // exit game (playerID not in any game)
        final URL exitGameURL3 = new URL("http://localhost:" + server.port() + "/exitGame/" + "giannis");
        final HttpURLConnection connection3 = (HttpURLConnection) exitGameURL3.openConnection();
        assertEquals(ERROR, connection3.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    // handleGuess() tests
    
    // covers handleGuess(): playerID(): invalid (no playerID specified); invalid (playerID not in any game)
    @Test
    public void testHandleGuessInvalidPlayerID() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
            
        
        // guess (playerID not in the game)
        final URL guessURL1 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/star");
        final HttpURLConnection guess1Connection = (HttpURLConnection) guessURL1.openConnection();
        assertEquals(ERROR, guess1Connection.getResponseCode(), "Expected 404 Error response code");
        
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // guess (no playerID specified)
        final URL guessURL2 = new URL ("http://localhost:" + server.port() + "/guess/" +"/" + "0/star");
        final HttpURLConnection guess2Connection = (HttpURLConnection) guessURL2.openConnection();
        assertEquals(ERROR, guess2Connection.getResponseCode(), "Expected 404 Error response code");


        server.stop();
    }
    
    // covers handleGuess(): index: invalid (out of bounds); invalid (not a number)
    @Test
    public void testHandleGuessInvalidIndex() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
  
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);

        // guess (index out of bounds)
        final URL guessURL1 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "-5/star");
        final HttpURLConnection guess1Connection = (HttpURLConnection) guessURL1.openConnection();
        assertEquals(ERROR, guess1Connection.getResponseCode(), "Expected 404 Error response code");
        
        // guess (index out of bounds #2 -> gets passed but returns an error message in client)
        final URL guessURL2 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "100/star");
        final HttpURLConnection guess2Connection = (HttpURLConnection) guessURL2.openConnection();
        assertEquals(SUCCESS, guess2Connection.getResponseCode(), "Expected 200 Success response code");
        
        // guess (index not a number)
        final URL guessURL3 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "abc/star");
        final HttpURLConnection guess3Connection = (HttpURLConnection) guessURL3.openConnection();
        assertEquals(ERROR, guess3Connection.getResponseCode(), "Expected 404 Error response code");
        
        
        // guess (index not a number)
        final URL guessURL4 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "!$/star");
        final HttpURLConnection guess4Connection = (HttpURLConnection) guessURL4.openConnection();
        assertEquals(ERROR, guess4Connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    // covers handleGuess(): guess: invalid (wrong length); invalid (not all characters)
    @Test
    public void testHandleGuessInvalidGuess() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
  
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);

        // guess (wrong length) -> gets passed but error notification gets shown on client
        final URL guessURL1 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/sta");
        final HttpURLConnection guess1Connection = (HttpURLConnection) guessURL1.openConnection();
        assertEquals(SUCCESS, guess1Connection.getResponseCode(), "Expected 200 Success response code");
        
        // guess (wrong length #2) -> gets passed but error notification gets shown on client
        final URL guessURL2 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/stars");
        final HttpURLConnection guess2Connection = (HttpURLConnection) guessURL2.openConnection();
        assertEquals(SUCCESS, guess2Connection.getResponseCode(), "Expected 200 Success response code");
        
        // guess (not all characters)
        final URL guessURL3 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/st4r");
        final HttpURLConnection guess3Connection = (HttpURLConnection) guessURL3.openConnection();
        assertEquals(ERROR, guess3Connection.getResponseCode(), "Expected 404 Error response code");
        
        
        // guess (not all characters)
        final URL guessURL4 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/sta!");
        final HttpURLConnection guess4Connection = (HttpURLConnection) guessURL4.openConnection();
        assertEquals(ERROR, guess4Connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    // covers handleGuess() : playerID, index, guess: valid
    @Test
    public void testHandleGuessValid() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
  
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // guess (valid)
        final URL guessURL1 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/star");
        final HttpURLConnection guess1Connection = (HttpURLConnection) guessURL1.openConnection();
        assertEquals(SUCCESS, guess1Connection.getResponseCode(), "Expected 200 Success response code");
        
        // guess (valid)
        final URL guessURL2 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/star");
        final HttpURLConnection guess2Connection = (HttpURLConnection) guessURL2.openConnection();
        assertEquals(SUCCESS, guess2Connection.getResponseCode(), "Expected 200 Success response code");
        
        server.stop();
    }
    
    
    // handleErase() tests
    
    // covers handleErase(): playerID(): invalid (no playerID specified); invalid (playerID not in any game)
    @Test
    public void testHandleEraseInvalidPlayerID() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
            
        
        // erase (playerID not in the game)
        final URL eraseURL1 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "0");
        final HttpURLConnection erase1Connection = (HttpURLConnection) eraseURL1.openConnection();
        assertEquals(ERROR, erase1Connection.getResponseCode(), "Expected 404 Error response code");
        
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // erase (no playerID specified)
        final URL eraseURL2 = new URL ("http://localhost:" + server.port() + "/erase/" +"/" + "0");
        final HttpURLConnection erase2Connection = (HttpURLConnection) eraseURL2.openConnection();
        assertEquals(ERROR, erase2Connection.getResponseCode(), "Expected 404 Error response code");


        server.stop();
    }
    
    // covers handleErase(): index: invalid (out of bounds); invalid (not a number)
    @Test
    public void testHandleEraseInvalidIndex() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
  
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);

        // erase (index out of bounds)
        final URL eraseURL1 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "-5");
        final HttpURLConnection erase1Connection = (HttpURLConnection) eraseURL1.openConnection();
        assertEquals(ERROR, erase1Connection.getResponseCode(), "Expected 404 Error response code");
        
        // erase (index out of bounds #2 -> gets passed but returns an error message in client)
        final URL eraseURL2 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "100");
        final HttpURLConnection erase2Connection = (HttpURLConnection) eraseURL2.openConnection();
        assertEquals(SUCCESS, erase2Connection.getResponseCode(), "Expected 200 Success response code");
        
        // erase (index not a number)
        final URL eraseURL3 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "abc");
        final HttpURLConnection erase3Connection = (HttpURLConnection) eraseURL3.openConnection();
        assertEquals(ERROR, erase3Connection.getResponseCode(), "Expected 404 Error response code");
        
        
        // erase (index not a number)
        final URL eraseURL4 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "!$");
        final HttpURLConnection erase4Connection = (HttpURLConnection) eraseURL4.openConnection();
        assertEquals(ERROR, erase4Connection.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    // covers handleErase() : playerID, index: valid
    @Test
    public void testHandleEraseValid() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
  
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // erase (valid)
        final URL eraseURL1 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "0");
        final HttpURLConnection erase1Connection = (HttpURLConnection) eraseURL1.openConnection();
        assertEquals(SUCCESS, erase1Connection.getResponseCode(), "Expected 200 Success response code");
        
        // erase (valid)
        final URL eraseURL2 = new URL ("http://localhost:" + server.port() + "/erase/" +"donald/" + "7");
        final HttpURLConnection erase2Connection = (HttpURLConnection) eraseURL2.openConnection();
        assertEquals(SUCCESS, erase2Connection.getResponseCode(), "Expected 200 Success response code");
        
        server.stop();
    }
    
    // handleCheckPuzzle() tests
    
    // covers handleCheckPuzzle(): playerID: invalid (no playerID specified); invalid (playerID not in any game)
    @Test
    public void testHandleCheckInvalidPlayerID() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
            
        
        // check (playerID not in the game)
        final URL checkURL1 = new URL ("http://localhost:" + server.port() + "/check/" +"donald");
        final HttpURLConnection check1Connection = (HttpURLConnection) checkURL1.openConnection();
        assertEquals(ERROR, check1Connection.getResponseCode(), "Expected 404 Error response code");
        
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // check (no playerID specified)
        final URL checkURL2 = new URL ("http://localhost:" + server.port() + "/check/" +"/");
        final HttpURLConnection check2Connection = (HttpURLConnection) checkURL2.openConnection();
        assertEquals(ERROR, check2Connection.getResponseCode(), "Expected 404 Error response code");

        server.stop();
    }
    
    // covers handleCheck() : playerID: valid
    @Test
    public void testHandleCheckValid() throws IOException, InterruptedException {

        String puzzlepath1 = "puzzles/simple2.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);

        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
  
        // create game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection gameConnection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, gameConnection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        // check (valid)
        final URL checkURL1 = new URL ("http://localhost:" + server.port() + "/check/" +"donald");
        final HttpURLConnection check1Connection = (HttpURLConnection) checkURL1.openConnection();
        assertEquals(SUCCESS, check1Connection.getResponseCode(), "Expected 200 Success response code");
        
        // 2 guess to complete puzzle (valid)
        final URL guessURL1 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "0/cat");
        final HttpURLConnection guess1Connection = (HttpURLConnection) guessURL1.openConnection();
        assertEquals(SUCCESS, guess1Connection.getResponseCode(), "Expected 200 Success response code");
        
        // guess (valid)
        final URL guessURL2 = new URL ("http://localhost:" + server.port() + "/guess/" +"donald/" + "1/mat");
        final HttpURLConnection guess2Connection = (HttpURLConnection) guessURL2.openConnection();
        assertEquals(SUCCESS, guess2Connection.getResponseCode(), "Expected 200 Success response code");
        
        // check (valid)
        final URL checkURL2 = new URL ("http://localhost:" + server.port() + "/check/" +"donald");
        final HttpURLConnection check2Connection = (HttpURLConnection) checkURL2.openConnection();
        assertEquals(SUCCESS, check2Connection.getResponseCode(), "Expected 200 Success response code");
        final InputStream input = checkURL2.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        String[] response = reader.lines().collect(Collectors.joining()).split("[|]");
        String isComplete = response[1];
        assertTrue(isComplete.equals("true"), "Expected the puzzle to be completed");
        
        server.stop();
    }
    
    
    
    
    // covers PlayerX creates a game, >0 players in a game, 0 players in a game, a game is created
    @Test
    public void testCreatingGame() throws IOException, InterruptedException{
        String puzzlepath1 = "puzzles/simple2.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();

        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final InputStream gameConnection1 = (InputStream) createGameURL.openStream();
        Thread.sleep(500);

        assertEquals(Map.of("donald", "0"), server.getPlayerToGameID(), "expected correct mapping of players to game IDs");
        assertEquals(Map.of("0", Set.of("donald")), server.getGameIDToPlayers(), "expected correct mapping of game IDs to players");
        assertEquals(Map.of("0", validPuzzle1), server.getGameIDToPuzzle(), "expected correct mapping of game IDs to puzzle instance");

        final URL exitGameURL = new URL("http://localhost:" + server.port() + "/exitGame/" + "donald");
        final InputStream gameConnection2 = (InputStream) exitGameURL.openStream();
        Thread.sleep(500);

        assertEquals(Map.of(), server.getPlayerToGameID(), "expected correct mapping of players to game IDs");
        assertEquals(Map.of("0", Set.of()), server.getGameIDToPlayers(), "expected correct mapping of players to game IDs");

        server.stop();
    }

    // covers PlayerX joins an existing game
    @Test
    public void testJoiningGame() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple2.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1 );
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();

        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final InputStream gameConnection1 = (InputStream) createGameURL.openStream();
        Thread.sleep(500);

        final URL joinGameURL = new URL("http://localhost:" + server.port() + "/joinExistingGame/" + "zach/" + "0");
        final InputStream gameConnection2 = (InputStream) joinGameURL.openStream();
        Thread.sleep(500);

        assertEquals(Map.of("donald", "0", "zach", "0"), server.getPlayerToGameID(), "expected correct mapping of players to game IDs");
        assertEquals(Map.of("0", Set.of("donald", "zach")), server.getGameIDToPlayers(), "expected correct mapping of game IDs to players");

        server.stop();
    }
    
    
    // handleWatchGame() tests:
    
    private URL watchGameURL;
    private HttpURLConnection connection2;
    
    // covers handleWatchGame(): valid
    @Test
    public void testHandleWatchGameValid() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);
        
        
        Thread watchThread = new Thread(new Runnable() {
            public void run() {
                try {
                    watchGameURL = new URL("http://localhost:" + server.port() + "/watchGame/" + "donald");
                    connection2 = (HttpURLConnection) watchGameURL.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
        });
        watchThread.start();
        
        Thread.sleep(500);
               
        assertEquals(SUCCESS, connection2.getResponseCode(), "Expected 200 Success response code");
        
        server.stop();
    }
    
    
    // covers handleWatchGame(): invalid (no playerID specified)
    @Test
    public void testHandleWatchGameInvalidNoPlayer() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);        
        
        Thread watchThread = new Thread(new Runnable() {
            public void run() {
                try {
                    watchGameURL = new URL("http://localhost:" + server.port() + "/watchGame/");
                    connection2 = (HttpURLConnection) watchGameURL.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
        });
        watchThread.start();        
        Thread.sleep(500);
               
        assertEquals(ERROR, connection2.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    
    
    // covers handleWatchGame(): invalid (playerID not in any game)
    @Test
    public void testHandleWatchGameInvalidPlayer() throws IOException, InterruptedException {
        String puzzlepath1 = "puzzles/simple.puzzle";
        Puzzle validPuzzle1 = Puzzle.parseFromFile(puzzlepath1);
        String puzzleName1 = Utilities.getFileNameWithoutExtensionWithForwardSlash(puzzlepath1);
        Map<String, Puzzle> puzzleNameToPuzzle = Map.of(puzzleName1, validPuzzle1);
        Map<String, String> puzzleNameToPuzzleDirectory = Map.of(puzzleName1, puzzlepath1);
        final WebServer server = new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, 4949);
        server.start();
        
        // create first game
        final URL createGameURL = new URL("http://localhost:" + server.port() + "/createGame/" + "donald/" + puzzleName1);
        final HttpURLConnection connection1 = (HttpURLConnection) createGameURL.openConnection();
        assertEquals(SUCCESS, connection1.getResponseCode(), "Expected 200 Success response code");
        Thread.sleep(500);        
        
        Thread watchThread = new Thread(new Runnable() {
            public void run() {
                try {
                    watchGameURL = new URL("http://localhost:" + server.port() + "/watchGame/" + "giannis");
                    connection2 = (HttpURLConnection) watchGameURL.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
        });
        watchThread.start();        
        Thread.sleep(500);
               
        assertEquals(ERROR, connection2.getResponseCode(), "Expected 404 Error response code");
        
        server.stop();
    }
    

}
