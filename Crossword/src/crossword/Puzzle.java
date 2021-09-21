package crossword;


import java.util.HashMap;

import edu.mit.eecs.parserlib.UnableToParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A mutable datatype representing a crossword puzzle. Our board has a name, description, and set of words. 
 * Each word in our crossword puzzle has a corresponding hint, a direction, and starting location.
 *  
 */
public class Puzzle {
    
    // Abstraction Function
    // AF(entryList, isComplete, numRows, numCols, boardList, locationMap, directionMap, name, description, players) -> a crossword puzzle with
    // name name and descriptor description whose dimensions are numRows x numCols, whose state is isComplete, and whose words, hints, locations,
    // directions, and guesses are contained inside entryList (and mapped to with locationMap, directionMap, and boardList),
    // and whose set of active players is players
    
    // Representation Invariant
    // - all words in the entries of entryList must be unique
    // - if two words intersect on the board, they must intersect at the same letter
    // - no words can overlap on the board
    //
    // Safety from rep exposure
    // - all fields private
    // - all fields final except isComplete, which is not shared with the client
    // - numRows, numCols, name, description immutable
    // - we make a deep copy of entryList to avoid sharing rep's entryList with clients
    // - locationMap and directionMap initialized in constructor
    //
    // Thread safety argument
    // - Our Puzzle ADT is thread safe via the monitor pattern.
    // - Entry, which we use in Puzzle, is also thread safe.
    
    private final List<Entry> entryList;
    private boolean isComplete;
    private final int numRows;
    private final int numCols;
    private final List<String> boardList;
    private final Map<String, List<Integer>> locationMap;
    private final Map<String, Direction> directionMap;
    private final String name;
    private final String description;
    private final Set<String> players;
    
    
    /**
     * Make a new puzzle by parsing a file
     * 
     * @param filepath the file path starting from the folder where the puzzles are located
     * @return a new Puzzle that represents the puzzle in filename
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static synchronized Puzzle parseFromFile(String filepath) throws IllegalArgumentException, IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String allLines = reader.lines().collect(Collectors.joining());
            reader.close();
            try {
                return PuzzleParser.parse(allLines);
            }
            catch (UnableToParseException e) {
                System.out.println(filepath);
              throw new IllegalArgumentException("Unable to parse input");
            }
        }
        catch (IOException e) {
            throw new IOException("file does not exist");
        }

    }
    
    /**
     * Return the dimensions of the smallest rectangle (board) that includes this puzzle
     * 
     * @param entryList the list of entries of the puzzle
     * @return the dimensions of the smallest rectangle containing this puzzle
     */
    private synchronized List<Integer> getDims(List<Entry> entryList) {
        int maxRow = 0;
        int maxCol = 0;
        for (Entry entry: entryList) {
            if (entry.getDirection() == Direction.ACROSS) {
                if (entry.getRow() > maxRow) {
                    maxRow = entry.getRow() + 1;
                }
                if (entry.getCol() + entry.getWord().length() > maxCol) {
                    maxCol = entry.getCol() + entry.getWord().length();
                }
            }
            else {
                if (entry.getRow() + entry.getWord().length() > maxRow) {
                    maxRow = entry.getRow() + entry.getWord().length();
                }
                if (entry.getCol() > maxCol) {
                    maxCol = entry.getCol() + 1;
                }
            }
        }
        maxRow = java.lang.Math.max(maxRow, 1);
        maxCol = java.lang.Math.max(maxCol, 1);
        return List.of(maxRow, maxCol);
    }   
    
    /**
     * Make a new puzzle with the specified name, description, and list of entries
     * @param entryList the list of entries
     * @param name the name of the puzzle
     * @param description the description of the puzzle
     */
    public Puzzle(List<Entry> entryList, String name, String description) {
        this.name = name;
        this.description = description;
        this.entryList = new ArrayList<>();
        for (Entry entry: entryList) {
            Entry newEntry = new Entry(entry.getWord(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol());
            this.entryList.add(newEntry);
        }
        this.isComplete = false;
        this.numRows = getDims(entryList).get(0);
        this.numCols = getDims(entryList).get(1);
        
        this.players = new HashSet<>();
        
        this.locationMap = new HashMap<>();
        this.directionMap = new HashMap<>();
        for (Entry entry: entryList) {
            String word = entry.getWord();
            Direction direction = entry.getDirection();
            int row = entry.getRow();
            int col = entry.getCol();
            this.locationMap.put(word, List.of(row, col));
            this.directionMap.put(word, direction);
        }
        this.boardList = new ArrayList<>();
        for (Integer i = 0; i < numRows*numCols; i++) {
            this.boardList.add("");
        }
        for (Entry entry: entryList) {
            int row = entry.getRow();
            int col = entry.getCol();
            String word = entry.getWord();
            if (this.directionMap.get(word) == Direction.DOWN) {
                for (Integer i = 0; i < word.length(); i++) {
                    int index = ((row+i)*this.numCols)+col;
                    this.boardList.set(index, ""+word.charAt(i));
                }
            }
            else {
                for (Integer i = 0; i < word.length(); i++) {
                    int index = (row*this.numCols)+col+i;
                    this.boardList.set(index, ""+word.charAt(i));
                }
            }
        }
        checkRep();
    }
    
    /**
     * Check if two entries of the puzzle overlap in an invalid way
     * 
     * @param entry1 the first entry
     * @param entry2 the second entry
     * @return true if the two entries share more than one common cells on the puzzle grid, false otherwise
     */
    private synchronized boolean overlapping(Entry entry1, Entry entry2) {
        Set<List<Integer>> entry1Coords = new HashSet<>();
        Set<List<Integer>> entry2Coords = new HashSet<>();
        if (entry1.getDirection().equals(Direction.DOWN)) {
            for (Integer i = 0; i < entry1.getWord().length(); i++) {
                entry1Coords.add(List.of(entry1.getRow() + i, entry1.getCol()));
            }
        }
        else {
            for (Integer i = 0; i < entry1.getWord().length(); i++) {
                entry1Coords.add(List.of(entry1.getRow(), entry1.getCol() + i));
            }
        }
        if (entry2.getDirection().equals(Direction.DOWN)) {
            for (Integer i = 0; i < entry2.getWord().length(); i++) {
                entry2Coords.add(List.of(entry2.getRow() + i, entry2.getCol()));
            }
        } 
        else {
            for (Integer i = 0; i < entry2.getWord().length(); i++) {
                entry2Coords.add(List.of(entry2.getRow(), entry2.getCol() + i));
            }
        }
        entry1Coords.retainAll(entry2Coords);
        return (entry1Coords.size() > 1);
    }
    
    private void checkRep() {
        for (Entry entry1: this.entryList) {
            for (Entry entry2: this.entryList) {
                if (!entry1.equals(entry2)) {
                    assert !overlapping(entry1, entry2);
                }
            }
        }
        Map<String, String> letterMap = new HashMap<>();
        List<String> wordList = new ArrayList<>();
        for (Entry entry: this.entryList) {
            String location = "" + entry.getRow() + entry.getCol();
            String word = entry.getWord();
            wordList.add(word);
            if (this.directionMap.get(word) == Direction.DOWN ) {
                for (Integer i = 0; i < word.length(); i++) {
                    int row = entry.getRow() + i;
                    location = "" + row + "," + entry.getCol();
                    if (!letterMap.containsKey(location)) {
                        letterMap.put(location, ""+word.charAt(i));
                    }
                    assert (letterMap.get(location).equals(""+word.charAt(i)));
                }
            }
            else {
                for (Integer i = 0; i < word.length(); i++) {
                    int col = entry.getCol() + i;
                    location = "" + entry.getRow() + "," + col;
                    if (!letterMap.containsKey(location)) {
                        letterMap.put(location, ""+word.charAt(i));
                    }
                    assert (letterMap.get(location).equals(""+word.charAt(i)));
                }
            }
        }
        Set<String> wordSet = new HashSet<>();
        for (String word: wordList) {
            wordSet.add(word);
        }
        assert (wordList.size() == wordSet.size());
    }
    
    /**
     * Overwrites every guess in the puzzle entryList that passes through cell (row, col) to have value letter
     * @param row Integer representing row index of the cell we are overwriting
     * @param col Integer representing column index of the cell we are overwriting
     * @param letter Character value we are overwriting to the cell
     */
    private synchronized void overwrite(int row, int col, char letter) {
        for (Integer index = 0; index < this.entryList.size(); index ++) {
            Entry entry = this.entryList.get(index);
            String word = entry.getGuess();
            if (entry.getDirection() == Direction.DOWN) {
                int c = entry.getCol();
                for (Integer i = 0; i < word.length(); i++) {
                    int r = entry.getRow() + i;
                    if (row == r && col == c) {
                        StringBuilder newWord = new StringBuilder(entry.getGuess());
                        newWord.setCharAt(i, letter);
                        String newWordString = newWord.toString();
                        Entry newEntry = new Entry(entry.getWord(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol(), newWordString);
                        this.entryList.set(index,  newEntry);
                    }
                }
            }
            else {
                int r = entry.getRow();
                for (Integer i = 0; i < word.length(); i++) {
                    int c = entry.getCol() + i;
                    if (row == r && col == c) {
                        StringBuilder newWord = new StringBuilder(entry.getGuess());
                        newWord.setCharAt(i, letter);
                        String newWordString = newWord.toString();
                        Entry newEntry = new Entry(entry.getWord(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol(), newWordString);
                        this.entryList.set(index,  newEntry);
                    }
                }
            }
        }
    }
    
    
    /** Get the set of active players for this puzzle
     * @return a set of the players currently active for this puzzle
     */
    public synchronized Set<String> getPlayers() {
        Set<String> playersSet = new HashSet<>();
        for (String playerID : this.players) {
            playersSet.add(playerID);
        }
        return Collections.unmodifiableSet(playersSet);
    }
    
    /**
     * Add the player to the set of currently active players for this puzzle, if not currently present
     * @param playerID the playerID of the player to add
     */
    public synchronized void addPlayer(String playerID) {
        if (!players.contains(playerID)) {
            players.add(playerID);
            this.notifyAll();
        }
//        System.out.println("Puzzle: just added player " + playerID + ", players: " +  this.getPlayers());
    }
    
    /**
     * Remove a player from the set of currently active players for this puzzle, if currently present
     * @param playerID the playerID of the player to remove
     */
    public synchronized void removePlayer(String playerID) {
        if (players.contains(playerID)) {
            players.remove((playerID));
            this.notifyAll();
        }
//        System.out.println("Puzzle: just removed player " + playerID + ", players: " +  this.getPlayers());
    }
    
    
    /**
     * Create a minimal string representation of this puzzle to be passed from the server to any client
     *   This is a minimal representation of the puzzle, i.e. without giving away the answers/actual words of the puzzle
     *   This string representation follows the rules of the grammar MinimalPuzzle.g 
     *   
     * @return the minimal string representation of this puzzle
     */
    public synchronized String makeClientString() {
        checkRep();
        String clientString = ">> ";
        clientString += "\"" + this.name + "\" " + "\"" + this.description + "\"" + "\n\n";
        for (Integer i = 0; i < this.entryList.size(); i++) {
            Entry entry = this.entryList.get(i);
            String entryString = "(";
            entryString += i + ", ";
            entryString += "\"" + entry.getHint() + "\"" + ", ";
            entryString += entry.getDirection() + ", ";
            entryString += entry.getRow() +", ";
            entryString += entry.getCol() + ", ";
            entryString += entry.getWord().length() + ", ";
            String guess = entry.getGuess();
            entryString += "\"" + guess + "\"" + ")\n";
            clientString += entryString;
        }
        return clientString.substring(0, clientString.length() - 1);
    }
    
    
    /**
     * Enter a word as a proposed answer to a clue (clue numbered index)
     * @param index the index of the actual word in the puzzle, must be 0 <= index <= getEntryList.size() - 1
     * @param guess the guessed word, must be of same length as the actual word
     * @throws IllegalArgumentException if index or length of guess are not valid
     */
    public synchronized void guessWord(int index, String guess) throws IllegalArgumentException {

        if (! (0 <= index && index <= entryList.size() - 1)) {
            throw new IllegalArgumentException();
        }
        if (guess.length() != entryList.get(index).getWord().length()) {
            throw new IllegalArgumentException();
        }
        Entry entry = entryList.get(index);
        Entry newEntry = new Entry(entry.getWord(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol(), guess);
        entryList.set(index, newEntry);
        for (Integer i = 0; i < guess.length(); i++) {
            char letter = guess.charAt(i);
            if (entry.getDirection() == Direction.DOWN) {
                int col = entry.getCol();
                int row = entry.getRow() + i;
                overwrite(row, col, letter);
            }
            else {
                int row = entry.getRow();
                int col = entry.getCol() + i;
                overwrite(row, col, letter);
            }
        }
        this.notifyAll();
    }
    
    /**
     * Erase a guessed word from the puzzle corresponding to clue numbered index
     * @param index the index of the actual word in the puzzle
     * @throws IllegalArgumentException if index is not valid
     */
    public synchronized void eraseWord(int index) throws IllegalArgumentException {

        if (! (0 <= index && index <= entryList.size() - 1)) {
            throw new IllegalArgumentException();
        }
        Entry entry = entryList.get(index);
        Entry newEntry = new Entry(entry.getWord(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol(), "*".repeat(entry.getWord().length()));
        entryList.set(index, newEntry);
        for (Integer i = 0; i < entry.getWord().length(); i++) {
            if (entry.getDirection() == Direction.DOWN) {
                int col = entry.getCol();
                int row = entry.getRow() + i;
                overwrite(row, col, '*');
            }
            else {
                int row = entry.getRow();
                int col = entry.getCol() + i;
                overwrite(row, col, '*');
            }
        }
        this.notifyAll();
    }
    
    /**
     * Check which letters of all guesses made on the puzzle are correct; leaves each correctly guessed letter in its
     * cell, otherwise replaces the letter in the cell with '*'
     */
    public synchronized void check() {

        boolean isComplete = true;
        for (Integer index = 0; index < this.entryList.size(); index ++) {
            Entry entry = this.entryList.get(index);
            String checkedWord = "";
            String word = entry.getWord();
            String guess = entry.getGuess();
            for (Integer i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess.charAt(i)) {
                    checkedWord += word.charAt(i);
                }
                else {
                    checkedWord += "*";
                    isComplete = false;
                }
            }
            Entry updatedEntry = new Entry(entry.getWord(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol(), checkedWord);
            for (Integer i = 0; i < checkedWord.length(); i++) {
                char letter = checkedWord.charAt(i);
                if (entry.getDirection() == Direction.DOWN) {
                    int col = entry.getCol();
                    int row = entry.getRow() + i;
                    overwrite(row, col, letter);
                }
                else {
                    int row = entry.getRow();
                    int col = entry.getCol() + i;
                    overwrite(row, col, letter);
                }
            }
            this.entryList.set(index, updatedEntry);
            this.isComplete = isComplete;
        }
        this.notifyAll();
    }
    
    /**
     * Get the state of the puzzle (whether it is complete or not)
     * @return true if the puzzle is complete, false otherwise
     */
    public synchronized boolean getIsComplete() {
        if (this.isComplete) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Get the list of entries of this puzzle
     * @return an unmodifiable view of this puzzle's list of entries
     */
    public synchronized List<Entry> getEntryList() {
        return Collections.unmodifiableList(this.entryList);
    }
    
    /**
     * @return the name of this puzzle
     */
    public synchronized String getName() {
        return this.name;
    }
    
    /**
     * @return the description of this puzzle
     */
    public synchronized String getDescription() {
        return this.description;
    }
    
    /**
     * @return the number of rows of this puzzle's board - see getDims spec
     */
    public synchronized int getNumRows() {
        return this.numRows;
    }
    
    /**
     * @return the number of columns of this puzzle's board - see getDims spec
     */
    public synchronized int getNumCols() {
        return this.numCols;
    }
    
    @Override
    public synchronized boolean equals(Object that) {
        checkRep();
        boolean equalEntries = true;
        if (this.entryList.size() != ((Puzzle) that).getEntryList().size()) {
            equalEntries = false;
        }
        else {
            for (Integer i = 0; i < this.entryList.size(); i++) {
                if (!this.entryList.get(i).equals(((Puzzle) that).getEntryList().get(i))) {
                    equalEntries = false;
                    break;
                }
            }
        }
        return that instanceof Puzzle && equalEntries &&
                this.name.equals(((Puzzle) that).getName()) &&
                this.description.equals(((Puzzle) that).getDescription());
                
    }
    
    @Override
    public synchronized int hashCode() {
        return this.entryList.size() + this.name.length() + this.description.length() + this.numRows + this.numCols;
    }
    
    @Override
    public synchronized String toString() {
        String puzzleString = ">> " + "\"" + this.name + "\"" + " " + "\"" + this.description + "\"" + "\n\n";
        for (Entry entry: this.entryList) {
            puzzleString += entry.toString() + "\n";
        }
        return puzzleString.substring(0, puzzleString.length() - 1);
    }
        

}
