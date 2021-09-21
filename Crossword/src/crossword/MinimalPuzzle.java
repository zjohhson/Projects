package crossword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.eecs.parserlib.UnableToParseException;

/**
 * 
 * An immutable datatype representing a minimal puzzle that intended for client-use. This puzzle is called minimal
 * because it contains none of the actual words contained in the puzzle -- rather it contains the starting location of words,
 * their direction, their length, their hint, and the current guessed word. 
 *
 */

public class MinimalPuzzle {
    // Abstraction Function
    // AF(entryList, numRows, numCols, name, description) -> a crossword puzzle with name name and descriptor 
    // description whose dimensions are numRows x numCols, and whose hints, locations,
    // directions, and guesses are contained inside entryList. Contains same information as Puzzle ADT, except for the correct words themselves
    // so as to not share the solutions with the client.
    
    //
    // Representation Invariant
    // - no words can overlap on the board
    // - each minimal entry's index field corresponds to its index in entryList 
    //
    // Safety from rep exposure
    // - all fields private
    // - all fields final except isComplete, which is immutable
    // - numRows, numCols, name, and description immutable
    // - we create a deep copy of entryList to avoid sharing rep's entryList with client
    //
    // Thread safety argument
    //   Datatype is threadsafe immutable
    //      - no mutators, all fields are private and final, no rep exposure
    //
    
    private final List<MinimalEntry> entryList;
    private final int numRows;
    private final int numCols;
    private final String name;
    private final String description;
//    private Map<Integer, String> currentWord;
    
    
    /**
     * Make a new puzzle by parsing a string
     * 
     * @param clientString the string representing a minimal version of a puzzle, must follow the rules of the grammar MinimalPuzzle.g
     * @return a new MinimalPuzzle that represents the minimal puzzle as described by clientString
     * @throws IllegalArgumentException
     */
    public static MinimalPuzzle parseFromString(String clientString) throws IllegalArgumentException {
        try {
            return MinimalPuzzleParser.parse(clientString);
        }
        catch (UnableToParseException e) {
          throw new IllegalArgumentException("Unable to parse input");
        }
    }
    
    /**
     * Return the dimensions of the smallest rectangle that includes this puzzle
     * 
     * @param entryList the list of minimal entries of the puzzle
     * @return the dimensions of the smallest rectangle containing this puzzle
     */
    private List<Integer> getDims(List<MinimalEntry> entryList) {
        int maxRow = 0;
        int maxCol = 0;
        for (MinimalEntry entry: entryList) {
            if (entry.getDirection() == Direction.ACROSS) {
                if (entry.getRow() > maxRow) {
                    maxRow = entry.getRow() + 1;
                }
                if (entry.getCol() + entry.getLength() > maxCol) {
                    maxCol = entry.getCol() + entry.getLength();
                }
            }
            else {
                if (entry.getRow() + entry.getLength() > maxRow) {
                    maxRow = entry.getRow() + entry.getLength();
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
     * Make a new minimal puzzle with the specified name, description, and list of minimal entries
     * @param entryList the list of minimal entries
     * @param name the name of the puzzle
     * @param description the description of the puzzle
     */
    public MinimalPuzzle(List<MinimalEntry> entryList, String name, String description) {
        this.name = name;
        this.description = description;
        this.entryList = new ArrayList<>();
//        this.currentWord = new HashMap<>();
        for (MinimalEntry entry: entryList) {
            MinimalEntry newEntry = new MinimalEntry(entry.getIndex(), entry.getHint(), entry.getDirection(), entry.getRow(), entry.getCol(), entry.getLength(), entry.getGuess());
//            this.currentWord.put(newEntry.getIndex(), "");
            this.entryList.add(newEntry);
        }
        this.numRows = getDims(entryList).get(0);
        this.numCols = getDims(entryList).get(1);
    }
    
    
    /**
     * Check if two entries of the puzzle overlap in an invalid way
     * 
     * @param entry1 the first entry
     * @param entry2 the second entry
     * @return true if the two entries share more than one common cells on the puzzle grid, false otherwise
     */
    private boolean overlapping(MinimalEntry entry1, MinimalEntry entry2) {
        Set<List<Integer>> entry1Coords = new HashSet<>();
        Set<List<Integer>> entry2Coords = new HashSet<>();
        if (entry1.getDirection().equals(Direction.DOWN)) {
            for (Integer i = 0; i < entry1.getLength(); i++) {
                entry1Coords.add(List.of(entry1.getRow() + i, entry1.getCol()));
            }
        }
        else {
            for (Integer i = 0; i < entry1.getLength(); i++) {
                entry1Coords.add(List.of(entry1.getRow(), entry1.getCol() + i));
            }
        }
        if (entry2.getDirection().equals(Direction.DOWN)) {
            for (Integer i = 0; i < entry2.getLength(); i++) {
                entry2Coords.add(List.of(entry2.getRow() + i, entry2.getCol()));
            }
        } 
        else {
            for (Integer i = 0; i < entry2.getLength(); i++) {
                entry2Coords.add(List.of(entry2.getRow(), entry2.getCol() + i));
            }
        }
        entry1Coords.retainAll(entry2Coords);
        return (entry1Coords.size() > 1);
    }
    
    private void checkRep() {
        for (MinimalEntry entry1: this.entryList) {
            for (MinimalEntry entry2: this.entryList) {
                if (!entry1.equals(entry2)) {
                    assert !overlapping(entry1, entry2);
                }
            }
        }
        for (int idx = 0; idx <= entryList.size() - 1; idx ++) {
            MinimalEntry entry = this.entryList.get(idx);
            assert idx == entry.getIndex();
        }
        
    }
    
    /**
     * Get the list of the minimal entries of this puzzle
     * @return an unmodifiable view of this puzzle's list of minimal entries
     */
    public List<MinimalEntry> getEntryList() {
        return Collections.unmodifiableList(this.entryList);
    }
    
    /**
     * @return the name of this puzzle
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the description of this puzzle
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * @return the number of rows of this puzzle's board - see getDims spec
     */
    public int getNumRows() {
        return this.numRows;
    }
    
    /**
     * @return the number of columns of this puzzle's board - see getDims spec
     */
    public int getNumCols() {
        return this.numCols;
    }
    
    @Override
    public boolean equals(Object that) {
        checkRep();
        boolean equalEntries = true;
        if (this.entryList.size() != ((MinimalPuzzle) that).getEntryList().size()) {
            equalEntries = false;
        }
        else {
            for (Integer i = 0; i < this.entryList.size(); i++) {
                if (!this.entryList.get(i).equals(((MinimalPuzzle) that).getEntryList().get(i))) {
                    equalEntries = false;
                    break;
                }
            }
        }
        return that instanceof MinimalPuzzle && equalEntries &&
                this.name.equals(((MinimalPuzzle) that).getName()) &&
                this.description.equals(((MinimalPuzzle) that).getDescription());
                
    }
    
    @Override
    public int hashCode() {
        return this.entryList.size() + this.name.length() + this.description.length() + this.numRows + this.numCols;
    }
    
    @Override
    public String toString() {
        String puzzleString = ">> " + "\"" + this.name + "\"" + " " + "\"" + this.description + "\"" + "\n\n";
        for (MinimalEntry entry: this.entryList) {
            puzzleString += entry.toString() + "\n";
        }
        return puzzleString.substring(0, puzzleString.length() - 1);
    }
        

}
