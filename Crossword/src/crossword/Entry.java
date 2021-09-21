package crossword;

/**
 * 
 * An immutable datatype representing an entry in our crossword puzzle. Our entry contains a word to be included in the crossword, as well as the
 * row and column index at which the word will begin. Entry also contains the direction in which our word goes -- either DOWN or ACROSS. Lastly,
 * Entry contains a hint that will be displayed to the client during the crossword game, relating to the word in our Entry.
 * Entry also contains a guess field representing the current guess for this Entry. 
 *
 */

public class Entry {

    // Abstraction Function
    // AF(word, hint, direction, row, col, guess) -> an entry in our crossword puzzle, which contains word word, begins at location (row, col),
    //                                               goes in the specified direction (downwards if direction == DOWN, to the right if direction == ACROSS), 
    //                                               is associated with hint hint, and where the current guess is guess.
    //
    // Representation Invariant
    // - 0 <= row, col
    // - guess.length() = word.length()
    //
    // Safety from rep exposure
    // - all fields private and final
    // - all fields immutable
    //
    // Thread safety argument
    //   Datatype is threadsafe immutable
    //      - no mutators, all fields are private and final, no rep exposure
    //
    
    private final String word;
    private final String hint;
    private final Direction direction;
    private final int row;
    private final int col;
    private final String guess;
    
    /**
     * Creates a new instance of Entry
     * @param word String representing the word in our crossword
     * @param hint String represents the hint corresponding to our word
     * @param direction Direction represents whether our word goes down or across
     * @param row Integer represents the row index of the first letter of our word
     * @param col Integer represents the column index of the first letter of our word
     * @param guess the current guessed word: must have length equal to the length of word, 
     *                                         and consists of a '*' character for each unguessed character.
     */
    public Entry(String word, String hint, Direction direction, int row, int col, String guess) {
        this.word = word;
        this.hint = hint;
        this.direction = direction;
        this.row = row;
        this.col = col;
        this.guess = guess;
    }
    
    /**
     * Creates a new instance of Entry without a 'guess' field specified 
     * @param word String representing the word in our crossword
     * @param hint String represents the hint corresponding to our word
     * @param direction Direction represents whether our word goes down or across
     * @param row Integer represents the row index of the first letter of our word
     * @param col Integer represents the column index of the first letter of our word
     */
    public Entry(String word, String hint, Direction direction, int row, int col) {
        this.word = word;
        this.hint = hint;
        this.direction = direction;
        this.row = row;
        this.col = col;
        this.guess = "*".repeat(word.length());
    }
    
    /**
     * asserts that all conditions of the Rep Invariant are met
     */
    private void checkRep() {
        assert this.row >= 0;
        assert this.col >= 0;
//        assert this.hint.matches("[^\"\\r\\n\\t\\\\]*"); //  ask about regex representation
//        assert this.word.matches("[a-z]+");
        assert this.guess.length() == this.word.length();
    }
    
    /**
     * returns the word in our entry
     * @return String word represents the word in our entry
     */
    public String getWord() {
        checkRep();
        return this.word;
    }
    
    /**
     * returns the hint in our entry
     * @return hint String hint represents the hint in our entry
     */
    public String getHint() {
        checkRep();
        return this.hint;
    }
    
    /**
     * returns the direction of the word in our entry
     * @return direction Direction representing the direction of the word in our entry
     */
    public Direction getDirection() {
        checkRep();
        return this.direction;
    }
    
    /**
     * returns the row index in our board of the first letter of the word in our entry
     * @return row Integer row representing the row index in our board of the first letter of the word in our entry
     */
    public int getRow() {
        checkRep();
        return this.row;
    }
    
    /**
     * returns the column index in our board of the first letter of the word in our entry
     * @return col Integer col representing the column index in our board of the first letter of the word in our entry
     */
    public int getCol() {
        checkRep();
        return this.col;
    }
    
    /**
     * return the current guess of the word that our Entry corresponds to
     * @return guess String the current guessed word
     */
    public String getGuess() {
        checkRep();
        return this.guess;
    }
    
    /**
     * returns string representation of our Entry ADT
     */
    @Override
    public String toString() {
        return "(" + this.word + ", " + "\"" + this.hint + "\"" + ", " + this.direction + ", " + this.row + ", " + this.col + ", " + "\"" + this.guess + "\"" + ")";
    }
    
    /**
     * returns hash code of our instance of Entry
     */
    @Override
    public int hashCode() {
        return this.word.length() + this.hint.length() + this.direction.toString().length() + this.row + this.col;
    }
    
    /**
     * returns true if this equals that, false otherwise
     */
    @Override
    public boolean equals(Object that) {
        return (that instanceof Entry && 
                this.word.equals(((Entry) that).word) &&
                this.hint.equals(((Entry) that).hint) &&
                this.direction.equals(((Entry) that).direction) &&
                this.row == ((Entry) that).row &&
                this.col == ((Entry) that).col) &&
                this.guess.equals(((Entry) that).guess);
    }
    
}


