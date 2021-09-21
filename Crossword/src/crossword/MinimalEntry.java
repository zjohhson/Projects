package crossword;


/**
 * 
 *  An immutable datatype representing a minimal entry of a minimal crossword puzzle. Designed for client-use.
 *  Similar to Entry but without storing the actual word; instead MinimalEntry stores the index of the respective Entry
 *  in the actual puzzle, the length of the word, and the current guess.
 *
 */


public class MinimalEntry {
    
    // Abstraction Function
    // AF(index, hint, direction, row, col, length, guess) -> the entry in a minimal crossword puzzle which has index index, begins at location (row, col),
    //                                                 goes in the specified direction (downwards if direction == DOWN, to the right if direction == ACROSS), 
    //                                                 is associated with hint hint, and where the current guessed word is guess.
    //
    // Representation Invariant
    //   index >= 0
    //   0 <= row, col
    //   length > 0
    //   guess.length() = length
    //
    // Safety from rep exposure
    // - all fields private and final
    // - all fields immutable
    //
    // Thread safety argument
    //    Datatype is threadsafe immutable
    //      - no mutators, all fields are private and final, no rep exposure
    //
    
    
    private final int index;
    private final String hint;
    private final Direction direction;
    private final int row;
    private final int col;
    private final int length;
    private final String guess;
    
    /**
     * Creates a new instance of MinimalEntry from the given parameters
     * @param index Integer assigned to every word in our MinimalPuzzle ADT, this will correspond to the number that appears in front of our hint in the puzzle
     * @param hint String representing the clue given to the client corresponding to the word 
     * @param direction Direction representing the direction of the word in our Puzzle (down or across)
     * @param row Integer representing the row index of the first letter of our word in the crossword puzzle
     * @param col Integer representing the column index of the first letter of our word in the crossword puzzle
     * @param length Integer representing the length of the word in our puzzle
     * @param guess the current guessed word: must have length equal to the length of word, 
     *                                        and consists of a '*' character for each unguessed character.
     */
    public MinimalEntry(int index, String hint, Direction direction, int row, int col, int length, String guess) {
        this.index = index;
        this.hint = hint;
        this.direction = direction;
        this.row = row;
        this.col = col;
        this.length = length;
        this.guess = guess;
    }
    
    /**
     * asserts that the conditions of our Rep Invariant are met
     */
    private void checkRep() {
        assert this.index >= 0;
        assert this.row >= 0;
        assert this.col >= 0;
        assert this.length > 0; 
        assert this.guess.length() == this.length;
    }
    
    /**
     * returns the index in our MinimalEntry
     * @return Integer index representing the index in our MinimalEntry
     */
    public int getIndex() {
        checkRep();
        return this.index;
    }
    
    /**
     * returns the hint in our MinimalEntry
     * @return hint String hint represents the hint in our MinimalEntry
     */
    public String getHint() {
        checkRep();
        return this.hint;
    }
    
    /**
     * returns the direction of the word in our MinimalEntry
     * @return direction Direction representing the direction of the word in our MinimalEntry
     */
    public Direction getDirection() {
        checkRep();
        return this.direction;
    }
    
    /**
     * returns the row index in our board of the first letter of the word in our MinimalEntry
     * @return row Integer row representing the row index in our board of the first letter of the word in our MinimalEntry
     */
    public int getRow() {
        checkRep();
        return this.row;
    }
    
    /**
     * returns the column index in our board of the first letter of the word in our MinimalEntry
     * @return col Integer col representing the column index in our board of the first letter of the word in our MinimalEntry
     */
    public int getCol() {
        checkRep();
        return this.col;
    }
    
    /**
     * return the length of the word that our MinimalEntry corresponds to
     * @return Integer length representing the length of the word that our MinimalEntry corresponds to
     */
    public int getLength() {
        checkRep();
        return this.length;
    }
    
    /**
     * return the current guess of the word that our MinimalEntry corresponds to
     * @return guess String the current guessed word
     */
    public String getGuess() {
        checkRep();
        return this.guess;
    }
    
    /**
     * returns string representation of our MinimalEntry ADT
     */
    @Override
    public String toString() {
        return "(" + this.index + ", " + "\"" + this.hint + "\"" + ", " + this.direction + ", " + this.row + ", " + this.col + ", " + this.length + ", " + "\"" + this.guess + "\"" + ")";
    }
    
    /**
     * returns hash code of our instance of MinimalEntry
     */
    @Override
    public int hashCode() {
        return this.index + this.hint.length() + this.direction.toString().length() + this.row + this.col + this.length + this.guess.length();
    }
    
    /**
     * returns true if this equals that, false otherwise
     */
    @Override
    public boolean equals(Object that) {
        return (that instanceof MinimalEntry && 
                this.index == ((MinimalEntry) that).index &&
                this.hint.equals(((MinimalEntry) that).hint) &&
                this.direction.equals(((MinimalEntry) that).direction) &&
                this.row == ((MinimalEntry) that).row &&
                this.col == ((MinimalEntry) that).col &&
                this.index == ((MinimalEntry) that).index) &&
                this.guess.equals(((MinimalEntry) that).guess);
    }

}
