package crossword;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import crossword.web.ExceptionsFilter;
import crossword.web.HeadersFilter;
import crossword.web.LogFilter;

/**
 * A mutable datatype representing an HTTP WebServer that can support running multiple crossword puzzle games.
 */
public class WebServer {
    
    private final HttpServer server;
    private final Map<String, Puzzle> puzzleNameToPuzzle;
    private final Map<String, String> puzzleNameToPuzzleDirectory;
    private static final int SUCCESS = 200;
    private static final int ERROR = 404;
    private final Map<String, String> playerToGameID;
    private final Map<String, Set<String>> gameIDToPlayers;
    private final Map<String, Puzzle> gameIDToPuzzle;
    private int lastGameID;
    
    private final Object lock = new Object();
    
    
    
    // Abstraction function:
    //  AF(server, puzzleNameToPuzzleDirectory, playerToGameID, gameIDToPlayers, gameIDToPuzzle, lastGameID) = 
    //     A web server supporting multiple crossword puzzle games. puzzleNameToPuzzleDirectory maps the names of crossword puzzle boards
    //     to their directory path for all valid puzzles in the puzzles directory. playerToGameID maps players to the gameID of the game they are currently 
    //     in. gameIDToPlayers maps gameIDs to the set of IDs of players who are currently playing that game. gameIDToPuzzle maps gameIDs to the instance of
    //     Puzzle on which the game is being played. lastGameID represents the most recent gameID used for creating a game (gameIDs are integers that are incremented 
    //     every time a new game is created).
    //     
    //
    // Representation invariant:
    //  - gameIDToPuzzle.keySet() = gameIDToPlayers.keySet()
    //  - playerToGameID.get(player) = gameID for player in gameIDToPlayers.get(gameID)
    //  - lastGameID >= max { int(gameID) for gameID in gameIDtoPuzzle.keySet() } (not equal since there can be gaps)
    //
    // Safety from rep exposure:
    //  - all fields private and final, except lastGameID which is never shared with the client
    //  - puzzleNameToPuzzleDirectory, playerToGameID, gameIDToPlayers, gameIDToPuzzle are never shared with the client in any method
    //  - all methods return primitive datatypes int and void
    //  - getPlayerToGameID(), getGameIDToPlayers(), getGameIDToPuzzle() all create deep copies, so the reps are never shared with clients
    
    // Thread safety argument:
    //   - our handler methods that mutate WebServer rep fields are not synchronized but it is okay since all blocks of code that mutate any WebServer rep fields are in synchronized blocks (using the lock of a special lock object)
    //   - our other handler methods are not atomic or synchronized, but that is okay because our handlers only rely on helper methods from our thread safe Puzzle ADT,
    //     which are atomic and synchronized via the monitor pattern. Thus, the order in which these Puzzle operations take place doesn't matter.
    
    /**
     * Make a new web server using a list of crossword puzzles that listens for connections on port
     * 
     * @param puzzleNameToPuzzle a list of shared crossword puzzle boards
     * @param puzzleNameToPuzzleDirectory a map of puzzle names to their directories (paths)
     * @param port server port number
     * @throws IOException if an error occurs starting the server
     */
    public WebServer(Map<String, Puzzle> puzzleNameToPuzzle, Map<String, String> puzzleNameToPuzzleDirectory, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0); 
        this.puzzleNameToPuzzle = puzzleNameToPuzzle;
        this.puzzleNameToPuzzleDirectory = puzzleNameToPuzzleDirectory;
        
        this.playerToGameID = new HashMap<>();
        this.gameIDToPlayers = new HashMap<>();
        this.gameIDToPuzzle = new HashMap<>();
        this.lastGameID = -1;
        
        // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());
        
        HeadersFilter headers = new HeadersFilter(Map.of(
                // allow requests from web pages hosted anywhere
                "Access-Control-Allow-Origin", "*",
                // all responses will be plain-text UTF-8
                "Content-Type", "text/plain; charset=utf-8"
                ));
        List<Filter> filters = List.of(new ExceptionsFilter(), new LogFilter(), headers);
        

        // add logging to all handlers and set required HTTP headers
        
        HttpContext createGame = server.createContext("/createGame/", this::handleCreateGame);
        createGame.getFilters().addAll(filters);
        
        HttpContext getAllPuzzles = server.createContext("/getAllPuzzles/", this::handleGetAllPuzzles);
        getAllPuzzles.getFilters().addAll(filters);
        
        HttpContext getAllActiveGames = server.createContext("/getAllActiveGames/", this::handleGetAllActiveGames);
        getAllActiveGames.getFilters().addAll(filters);
        
        HttpContext joinExistingGame = server.createContext("/joinExistingGame/", this::handleJoinExistingGame);
        joinExistingGame.getFilters().addAll(filters);
        
        HttpContext guess = server.createContext("/guess/", this::handleGuess);
        guess.getFilters().addAll(filters);
        
        HttpContext erase = server.createContext("/erase/", this::handleErase);
        erase.getFilters().addAll(filters);
        
        HttpContext check = server.createContext("/check/", this::handleCheckPuzzle);
        check.getFilters().addAll(filters);
        
        HttpContext exitGame = server.createContext("/exitGame/", this::handleExitGame);
        exitGame.getFilters().addAll(filters);
        
        HttpContext watchGame = server.createContext("/watchGame/", this::handleWatchGame);
        watchGame.getFilters().addAll(filters);
    }
    
    /**
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return server.getAddress().getPort();
    }
    
    /**
     * Start this server in a new background thread.
     */
    public void start() {
        System.err.println("Server will listen on " + server.getAddress());
        server.start();
    }
    
    /**
     * Stop this server. Once stopped, this server cannot be restarted.
     */
    public void stop() {
        System.err.println("Server will stop");
        server.stop(0);
    }
    
    
    private void checkRep() {
        assert gameIDToPuzzle.keySet().equals(gameIDToPlayers.keySet());
        
        for (String gameID : gameIDToPlayers.keySet()) {
            for (String playerID : gameIDToPlayers.get(gameID)) {
                assert gameID.equals(playerToGameID.get(playerID));
            }
        }
        
        Set<Integer> allGameIDs = new HashSet<>();
        for (String gameID : gameIDToPuzzle.keySet()) {
            allGameIDs.add(Integer.parseInt(gameID));            
        }
        if (!allGameIDs.isEmpty()) {
            assert lastGameID >= Collections.max(allGameIDs);
        }
    }

    
    /**
     * Handle a request for /getAllPuzzles/<playerID> by returning the names of all available puzzles
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server           
     */
    private void handleGetAllPuzzles(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String playerID = path.substring(base.length());
        
        String response = "";
        if (playerID.matches("\\w+")) {
            exchange.sendResponseHeaders(SUCCESS, 0);
            
            synchronized (lock) {
                for (String puzzleName : puzzleNameToPuzzle.keySet()) {
                    response += puzzleName + ",";
                }
            }

        } else {
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Game creation must include a valid playerID";
        }
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter (new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    
    /**
     * Handle a request for /createGame/<playerID>/<puzzle> by creating a new game for the specified puzzle
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleCreateGame(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        // puzzleName, not puzzle directory
        final String[] playerAndPuzzleName = path.substring(base.length()).split("/");
        
        String response = "";
        
        if (playerAndPuzzleName.length == 2) {
            
            final String playerID = playerAndPuzzleName[0];
            final String puzzleName = playerAndPuzzleName[1];
            
            synchronized (lock) {
                if (puzzleNameToPuzzleDirectory.containsKey(puzzleName) && playerID.matches("\\w+")) {
                    exchange.sendResponseHeaders(SUCCESS, 0);
                    
                    // Parses a puzzle from available puzzle directories
                    final Puzzle currentPuzzle = Puzzle.parseFromFile(puzzleNameToPuzzleDirectory.get(puzzleName));
                    final int gameID = lastGameID + 1;
                    
                    // Adjusts players if they're already in another game
                    for (String otherGameID : gameIDToPlayers.keySet()) {
                        if (gameIDToPlayers.get(otherGameID).contains(playerID)) {
                            gameIDToPlayers.get(otherGameID).remove(playerID);
                        }
                    }                
                    
                    playerToGameID.put(playerID, "" + gameID);
                    gameIDToPlayers.put("" + gameID, new HashSet<>());
                    gameIDToPlayers.get("" + gameID).add(playerID);
                    gameIDToPuzzle.put("" + gameID, currentPuzzle);
                    lastGameID += 1;
                    
                    currentPuzzle.addPlayer(playerID);
                    
                    String allPlayersString = "";
                    for (String player: gameIDToPlayers.get("" + gameID)) {
                        allPlayersString += player + ",";
                    }
                    
                    response = currentPuzzle.makeClientString() + "|" + allPlayersString; 
                    
                } else {
                    exchange.sendResponseHeaders(ERROR, 0);
                    response = "Not a valid puzzle or not a puzzle in the puzzles directory";
                }
            }            
            
            
        } else {
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Not a valid request: missing playerID or puzzle directory";
        }    
        
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter (new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();        
        
        exchange.close();
        checkRep();
    }
    
    
    /**
     * Handle a request for /getAllActiveGames/<playerID> by returning all active game ids
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleGetAllActiveGames(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String playerID = path.substring(base.length());

        String response = "";
        if (playerID.matches("\\w+")) {
            exchange.sendResponseHeaders(SUCCESS, 0);
            
            synchronized (lock) {
                for (String gameID : gameIDToPlayers.keySet()) {
                    response += gameID + ",";
                }
            }
            
            
        } else {
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Game creation must include a valid playerID";
        }
        
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter (new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    /**
     * Handle a request for /joinExistingGame/<playerID>/<gameID> by joining the game specified by gameID
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleJoinExistingGame(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
                
        final String[] playerAndGameID = path.substring(base.length()).split("/");
        
        String response = "";
        
        if (playerAndGameID.length >= 2) {
            
            final String playerID = playerAndGameID[0];
            final String gameID = playerAndGameID[1];       
            
            synchronized (lock) {
                if (playerID.matches("\\w+") && gameIDToPuzzle.containsKey(gameID)) {
                    exchange.sendResponseHeaders(SUCCESS, 0);
                    
                    final Puzzle currentPuzzle = gameIDToPuzzle.get(gameID);
                    
                    for (String otherGameID : gameIDToPlayers.keySet()) {
                        if (gameIDToPlayers.get(otherGameID).contains(playerID)) {
                            gameIDToPlayers.get(otherGameID).remove(playerID);
                        }
                    }                
                    
                    playerToGameID.put(playerID, "" + gameID);
                    gameIDToPlayers.get("" + gameID).add(playerID);
                    
                    currentPuzzle.addPlayer(playerID);                   
                    String allPlayersString = "";
                    for (String player: gameIDToPlayers.get(gameID)) {
                        allPlayersString += player + ",";
                    }
                    
                    response = currentPuzzle.makeClientString() + "|" + allPlayersString;                
                } else {
                    exchange.sendResponseHeaders(ERROR, 0);
                    response = "Not a valid puzzle or not a puzzle in the puzzles directory";
                }
            }            
            
        } else {
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Not a valid request: missing playerID or puzzle directory";
        }
        
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter (new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    /**
     * Handle a request for /exitGame/<playerID> by leaving the game that they're in
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleExitGame(HttpExchange exchange) throws IOException {
       final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
                
        final String playerID = path.substring(base.length());        
        String response = "";
               
        synchronized (lock) {
            if (playerID.matches("\\w+") && playerToGameID.containsKey(playerID)) {
                exchange.sendResponseHeaders(SUCCESS, 0);
                
                final String currentGameID = playerToGameID.get(playerID);
                
                playerToGameID.remove(playerID);
                gameIDToPlayers.get(currentGameID).remove(playerID);
                
                final Puzzle currentPuzzle = gameIDToPuzzle.get(currentGameID);
                
                currentPuzzle.removePlayer(playerID);
                synchronized (currentPuzzle) {currentPuzzle.notifyAll();}
                
                // remove game if it is empty
//                if (gameIDToPlayers.get(currentGameID).isEmpty()) {
//                    gameIDToPlayers.remove(currentGameID);
//                    gameIDToPuzzle.remove(currentGameID);
//                }
                
            } else {
                exchange.sendResponseHeaders(ERROR, 0);
                response = "Not a valid request: invalid playerID or player not in the current game";
            }
        }        
        
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter (new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    /**
     * Handle a request for /guess/<playerID>/index/guess by making a guess for the 
     * current puzzle the <playerID> is on. Index is the canvas entry they're attempting to solve
     * 
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleGuess(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String[] guessContent = path.substring(base.length()).split("/");
        final List<String> guessContentList = List.of(guessContent);
        // guessContent will be ""
        final String response;
        String errorMessage = "";
        if (guessContent.length >= 3 && !guessContentList.contains("")) {
            final String playerID = guessContent[0];
            final String index = guessContent[1];
            final String guess = guessContent[2];
            
            synchronized (lock) {
                if (playerToGameID.containsKey(playerID) && index.matches("\\d+") && guess.matches("[A-Za-z]+") ) {
                    exchange.sendResponseHeaders(SUCCESS, 0);
                    String currentGameID = playerToGameID.get(playerID);
                    Puzzle currentPuzzle = gameIDToPuzzle.get(currentGameID);
                    try {
                        currentPuzzle.guessWord(Integer.parseInt(index), guess);
                    } catch (IllegalArgumentException e) {
                         errorMessage = "Please enter a valid index for your guess and a word with the correct length for that index";
                    }
                    response = currentPuzzle.makeClientString() + "|" + errorMessage;
                } else {
                    exchange.sendResponseHeaders(ERROR, 0);
                    response = "Player " + playerID + " is not currently in any puzzle games";
                }
            }
            
        } else {
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Not a valid call: must include a playerID, a guess index, and a guess";
        }      
            
        // writes response to the output stream using UTF-8 character encoding
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    /**
     * Handle a request for /erase/<playerID>/index by erasing the current guess for the 
     * current puzzle the <playerID> is on. Index is the canvas entry that is being removed
     * 
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleErase(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String[] guessContent = path.substring(base.length()).split("/");
        final String response;
        String errorMessage = "";
        if (guessContent.length >= 2) {
            final String playerID = guessContent[0];
            final String index = guessContent[1];
            
            synchronized (lock) {
                if (playerToGameID.containsKey(playerID) && index.matches("\\d+")) {
                    exchange.sendResponseHeaders(SUCCESS, 0);
                    String currentGameID = playerToGameID.get(playerID);
                    Puzzle currentPuzzle = gameIDToPuzzle.get(currentGameID);
                    try {
                        currentPuzzle.eraseWord(Integer.parseInt(index));
                    } catch (IllegalArgumentException e) {
                        errorMessage = "Please enter a valid index to erase from";
                    }
                    response = currentPuzzle.makeClientString() + "|" + errorMessage;
                } else {
                    exchange.sendResponseHeaders(ERROR, 0);
                    response = "Player " + playerID + " is not currently in any puzzle games";
                }
            }
            
        } else {
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Not a valid call: must include a playerID and a guess index to erase from";
        }      
            
        // writes response to the output stream using UTF-8 character encoding
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    /**
     * Handle a request for /check/<playerID> by erasing the current guess for the 
     * current puzzle the <playerID> is on. Index is the canvas entry that is being removed
     * 
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleCheckPuzzle(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);

        final String playerID = path.substring(base.length());

        final String response;
        
        synchronized (lock) {
            if (playerToGameID.containsKey(playerID)) {
                exchange.sendResponseHeaders(SUCCESS, 0);
                String currentGameID = playerToGameID.get(playerID);
                Puzzle currentPuzzle = gameIDToPuzzle.get(currentGameID);
                currentPuzzle.check();
                final String isComplete = String.valueOf(currentPuzzle.getIsComplete());
                response = currentPuzzle.makeClientString() + "|" + isComplete;
            } else {
                exchange.sendResponseHeaders(ERROR, 0);
                response = "Not a valid call: must include a playerID";
            }
        }
              
        
        // writes response to the output stream using UTF-8 character encoding
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        out.flush();
        
        exchange.close();
        checkRep();
    }
    
    
    
    /**
     * Handle a request for /watch/<playerID> by notifying the player when the state of the game changes.
     *    This request will block until a change occurs in the game state.
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     * @throws IOException if there is an error starting the server/sending response back to server                         
     */
    private void handleWatchGame(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();        
        final String base = exchange.getHttpContext().getPath();        
        assert path.startsWith(base);
        
        final String playerID = path.substring(base.length());       
        
        String response;
        
        final boolean playerInGame;
        synchronized (lock) {playerInGame = playerToGameID.containsKey(playerID);}
        
        if (playerID.matches("\\w+") && playerInGame) {
            exchange.sendResponseHeaders(SUCCESS, 0);
            final String gameID;
            final Puzzle currentPuzzle;
            
            synchronized (lock) {
                gameID = playerToGameID.get(playerID);
                currentPuzzle = gameIDToPuzzle.get(gameID);
            }
            final String previousClientString = currentPuzzle.makeClientString();            
            final Set<String> previousPlayerSet = currentPuzzle.getPlayers();
            final boolean isComplete = currentPuzzle.getIsComplete();
                        
            synchronized (currentPuzzle) {
                while (previousClientString.equals(currentPuzzle.makeClientString()) && previousPlayerSet.equals(currentPuzzle.getPlayers()) && isComplete == currentPuzzle.getIsComplete()) {
                    try {
                        currentPuzzle.wait();
                    } catch (Exception e) {}
                }                
                String allPlayersString = "";
                for (String player: currentPuzzle.getPlayers()) {
                    allPlayersString += player + ",";
                }
                
                response = currentPuzzle.makeClientString() + "|" + allPlayersString + "|" + currentPuzzle.getIsComplete(); 
            }
            
        } 
        else {
            // otherwise, respond with HTTP code 404 to indicate an error
            exchange.sendResponseHeaders(ERROR, 0);
            response = "Cannot watch, not part of any game";
        }
        
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
        out.println(response);
        exchange.close();
        checkRep();
    }
    /**
     * Creates a deep copy of getPlayerToGameID for testing purposes.
     * @return a deep copy of getPlayerToGameID
     */
    public Map<String, String> getPlayerToGameID() {
        Map<String, String> returnMap = new HashMap<>();
        for (String player: playerToGameID.keySet()) {
            returnMap.put(player, playerToGameID.get(player));
        }
        checkRep();
        return returnMap;
    }
    
    /**
     * Creates a deep copy of getGameIDToPlayers for testing purposes.
     * @return a depp copy of getGameIDToPlayers
     */
    public Map<String, Set<String>> getGameIDToPlayers() {
        Map<String, Set<String>> returnMap = new HashMap<>();
        for (String gameID: gameIDToPlayers.keySet()) {
            returnMap.put(gameID, gameIDToPlayers.get(gameID));
        }
        checkRep();
        return returnMap;
    }
    
    /**
     * Creates a deep copy of getGameIDToPuzzle for testing purposes.
     * @return a deep copy of getGameIDToPuzzle
     */
    public Map<String, Puzzle> getGameIDToPuzzle() {
        Map<String, Puzzle> returnMap = new HashMap<>();
        for (String gameID: gameIDToPuzzle.keySet()) {
            returnMap.put(gameID, gameIDToPuzzle.get(gameID));
        }
        checkRep();
        return returnMap;
    }
    
    
}
