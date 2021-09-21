package crossword;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.mit.eecs.parserlib.UnableToParseException;


/**
 * Tests for the Puzzle abstract data type.
 */
class PuzzleTest {
    
    // TESTING STRATEGY
    // partitions on getNumRows(): numRows = 1, numRows > 1
    // partitions on getNumCols(): numCols = 1, numCols > 1
    // partitions on getEntryList(): entryList length 1, entryList length >1
    // partitions on equals(that): this equals that, this does not equal that
    // partitions on hashCode(): two objects are equivalent (have same hash code), two objects have different hash codes
    // partitions on makeClientString(): entryList length 1, entryList length > 1
    // partitions on parseFromFile(): puzzle is valid according to grammar, puzzle is invalid according to grammar, filename is valid, filename is invalid
    // partitions on isComplete(): puzzle is complete, puzzle is not complete
    // two entries intersect, no intersection
    //
    // partitions on guessWord(): 
    //   index is valid, index is out of bounds
    //   guess is correct length, guess is incorrect length
    //   guess overwrites at least one cell, guess does not overwrite any cells
    // partitions on eraseWord(): 
    //   index is valid, index is out of bounds
    //   erase overwrites at least one already-guessed cell from another word, erase does not overwrite any already-guessed cells from another word
    // partitions on check(): 
    //   no words have been guessed, words have been guessed;
    //   word has been guessed incorrectly, word has been guessed partially correctly, word has been guessed completely correctly
    //   check overwrites at least one already-guessed cell from another word, check does not overwrite any already-guessed cells from another word
    //
    // partitions on addPlayer(), removePlayer(), getPlayers():
    //   number of players: 0, 1, >1
    //   player added does not exist; player added already existed
    //   player removed existed; player removed did not exist
    //

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // covers entryList.size() = 1, numRows = 1, numCols = 1, no intersection no words have been guessed, puzzle is complete
    @Test
    public void testOneSingleLetterWord() {
        Entry entry = new Entry("a", "article", Direction.ACROSS, 0, 0);
        Puzzle puzzle = new Puzzle(List.of(entry), "Easy", "An easy puzzle to get started");
        puzzle.check();
        assertEquals("*", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
        puzzle.guessWord(0, "a");
        puzzle.check();
        assertTrue(puzzle.getIsComplete(), "expected puzzle to be complete");
        assertEquals(1, puzzle.getNumRows(), "expected correct number of rows");
        assertEquals(1, puzzle.getNumCols(), "expected correct number of columns");
        assertEquals(List.of(new Entry("a", "article", Direction.ACROSS, 0, 0, "a")), puzzle.getEntryList(), "expected correct entryList");
        String string = puzzle.makeClientString();
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(0, \"article\", ACROSS, 0, 0, 1, \"a\")", string, "expected correct client string");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(a, \"article\", ACROSS, 0, 0, \"a\")", puzzle.toString(), "expected correct string rep of puzzle");
    }
    
    // covers entryList.size() > 1, numRows > 1, numCols > 1, this does not equal that, entries intersect
    @Test
    public void testMultipleWords() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        assertEquals(8, puzzle.getNumRows(), "expected correct number of rows");
        assertEquals(5, puzzle.getNumCols(), "expected correct number of columns");
        assertEquals(List.of(new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0), new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4)),
                puzzle.getEntryList(), "expected correct entry list");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(0, \"a common greeting\", ACROSS, 3, 0, 5, \"*****\")\n"
                + "(1, \"a common farewell\", DOWN, 1, 4, 7, \"*******\")", puzzle.makeClientString(), "expected correct client string");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(hello, \"a common greeting\", ACROSS, 3, 0, \"*****\")\n"
                + "(goodbye, \"a common farewell\", DOWN, 1, 4, \"*******\")", puzzle.toString(), "expected correct string rep of puzzle");
    }
    
    // covers this does not equal that, two objects have different hash codes
    @Test
    public void testTwoUnequalPuzzles() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle1 = new Puzzle(List.of(entry1), "Easy", "An easy puzzle to get started");
        Puzzle puzzle2 = new Puzzle(List.of(entry2), "Easy", "An easy puzzle to get started");
        assertFalse(puzzle1.equals(puzzle2), "expected puzzles to not be equal");
        assertFalse(puzzle1.hashCode() == puzzle2.hashCode(), "expected puzzles to have different hash codes");
    }
    
    // covers this equals that, two objects have same hash code
    @Test
    public void testTwoEqualPuzzles() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Puzzle puzzle1 = new Puzzle(List.of(entry1), "Easy", "An easy puzzle to get started");
        Puzzle puzzle2 = new Puzzle(List.of(entry2), "Easy", "An easy puzzle to get started");
        assertTrue(puzzle1.equals(puzzle2), "expected puzzles to be equal");
        assertTrue(puzzle1.hashCode() == puzzle2.hashCode(), "expected puzzles to have same hash code");
    }
    
    // covers filename is valid, puzzle is valid according to grammar, word is guessed incorrectly
    @Test
    public void testParserSimplePuzzle() throws UnableToParseException, IOException {
        String filename = "puzzles/simple.puzzle";
        Puzzle puzzle = Puzzle.parseFromFile(filename);
        puzzle.guessWord(0, "zzzz");
        puzzle.check();
        assertEquals("****", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
        assertEquals(10, puzzle.getNumCols(), "expected correct number of columns");
        assertEquals(7, puzzle.getNumRows(), "expected correct number of rows");
        assertEquals("Easy", puzzle.getName(), "expected correct name");
        assertEquals("An easy puzzle to get started", puzzle.getDescription(), "expected correct description");
        assertEquals(List.of(new Entry("star", "twinkle twinkle", Direction.ACROSS, 1, 0),
                new Entry("market", "Farmers ______", Direction.DOWN, 0, 2),
                new Entry("kettle", "It's tea time!", Direction.ACROSS, 3, 2),
                new Entry("extra", "more", Direction.DOWN, 1, 5),
                new Entry("bee", "Everyone loves honey", Direction.ACROSS, 4, 0),
                new Entry("treasure", "Every pirate's dream", Direction.ACROSS, 5, 2),
                new Entry("troll", "Everyone's favorite twitter pastime", Direction.ACROSS, 4, 4),
                new Entry("loss", "This is not a gain", Direction.DOWN, 3, 6)), puzzle.getEntryList(), "expected correct entry list");
    }

    // covers filename is invalid
    @Test
    public void testInvalidFile() {
        assertThrows(java.io.IOException.class, () -> {Puzzle.parseFromFile("puzzles/invalidFile");}, "expected exception to be thrown for nonexistent file");
    }
    
    // covers puzzle is invalid according to grammar
    @Test
    public void testInvalidPuzzle() {
        assertThrows(IllegalArgumentException.class, () -> {Puzzle.parseFromFile("invalidPuzzles/invalidsimple.puzzle");}, "expected exception to be thrown for invalid puzzle");
    }
    
    
    // guessWord(), eraseWord() tests    
    
    // guess: covers index is valid and guess has correct length, word has been guessed completely correctly
    @Test
    public void testGuessWordValid() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.guessWord(1, "goodbye");
        puzzle.check();
        assertFalse(puzzle.getIsComplete(), "expected puzzle to be incomplete");
        assertEquals("goodbye", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals(List.of(new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0, "****o"), new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4, "goodbye")),
                puzzle.getEntryList(), "expected correct entry list after guess");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(hello, \"a common greeting\", ACROSS, 3, 0, \"****o\")\n"
                + "(goodbye, \"a common farewell\", DOWN, 1, 4, \"goodbye\")", puzzle.toString(), "expected correct string rep of puzzle after guess");
    }
    
    // guess: covers index is out of bounds 
    @Test
    public void testGuessWordInvalidIndex() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        assertThrows(IllegalArgumentException.class, () -> {puzzle.guessWord(2, "goodbye");}, "expected exception to be thrown");
    }
    
    // guess: covers guess has incorrect length 
    @Test
    public void testGuessWordInvalidGuess() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        assertThrows(IllegalArgumentException.class, () -> {puzzle.guessWord(2, "bye");}, "expected exception to be thrown");
    }
    
    // guess and erase: covers index is valid, guess is partially correct
    @Test
    public void testGuessWordAndEraseValid() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.guessWord(0, "hello");
        puzzle.guessWord(1, "notSure");
        puzzle.check();
        assertEquals("*o****e", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        puzzle.eraseWord(1);
        assertEquals(List.of(new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0, "hell*"), new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4, "*".repeat(7))),
                puzzle.getEntryList(), "expected correct entry list after guess");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(hello, \"a common greeting\", ACROSS, 3, 0, \"hell*\")\n"
                + "(goodbye, \"a common farewell\", DOWN, 1, 4, \"*******\")", puzzle.toString(), "expected correct string rep of puzzle after guess");
    }
    
    // guess and erase: covers index is is out of bounds
    @Test
    public void testEraseInvalidIndex() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 3, 0);
        Entry entry2 = new Entry("goodbye", "a common farewell", Direction.DOWN, 1, 4);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.guessWord(0, "hello");
        puzzle.guessWord(1, "notSure");
        assertThrows(IllegalArgumentException.class, () -> {puzzle.eraseWord(2);}, "expected exception to be thrown");        
    }
    
    // covers guess overwrites at least one cell, erase overwrites at least one already-guessed cell, check overwrites at least one already-guessed cell
    @Test
    public void testOverwriting() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 0, 0);
        Entry entry2 = new Entry("hey", "a common greeting", Direction.DOWN, 0, 0);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.guessWord(1, "hey");
        puzzle.guessWord(0, "group");
        assertEquals("gey", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals("group", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
        puzzle.eraseWord(0);
        assertEquals("*ey", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals("*****", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
        puzzle.guessWord(0, "group");
        puzzle.check();
        assertEquals("*ey", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals("*****", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
    }
    
    // covers guess does not overwrite any cells (except for initial *), erase does not overwrite any already-guessed cells from another word, check does not overwrite any already-guessed cells from another word
    @Test
    public void testNoOverwriting() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 0, 0);
        Entry entry2 = new Entry("hey", "a common greeting", Direction.DOWN, 0, 0);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.guessWord(1, "hey");
        assertEquals("hey", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals("h****", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
        puzzle.eraseWord(1);
        assertEquals("***", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals("*****", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
        puzzle.guessWord(0, "happy");
        puzzle.check();
        assertEquals("h**", puzzle.getEntryList().get(1).getGuess(), "expected correct guess");
        assertEquals("h****", puzzle.getEntryList().get(0).getGuess(), "expected correct guess");
    }
    
    
    // addPlayer(), removePlayer(), getPlayers() tests:
    
    // covers 0 players
    @Test
    public void testGetPlayersNoPlayers() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 0, 0);
        Entry entry2 = new Entry("hey", "a common greeting", Direction.DOWN, 0, 0);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        assertEquals(Set.of(), puzzle.getPlayers(), "expected correct set of players");
    }
    
    // covers 1 player, player added did not exist, player removed existed
    @Test
    public void testGetPlayersOnePlayer() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 0, 0);
        Entry entry2 = new Entry("hey", "a common greeting", Direction.DOWN, 0, 0);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.addPlayer("giannis");
        assertEquals(Set.of("giannis"), puzzle.getPlayers(), "expected correct set of players");
        puzzle.removePlayer("giannis");
        assertEquals(Set.of(), puzzle.getPlayers(), "expected correct set of players");
    }
    
    // covers >1 player, player added already existed, player removed did not exist
    @Test
    public void testGetPlayersMultiplePlayers() {
        Entry entry1 = new Entry("hello", "a common greeting", Direction.ACROSS, 0, 0);
        Entry entry2 = new Entry("hey", "a common greeting", Direction.DOWN, 0, 0);
        Puzzle puzzle = new Puzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        puzzle.addPlayer("giannis");
        puzzle.addPlayer("donald");
        assertEquals(Set.of("giannis", "donald"), puzzle.getPlayers(), "expected correct set of players");
        puzzle.removePlayer("giannis");
        puzzle.removePlayer("ioannis"); // remove player who did not exist
        assertEquals(Set.of("donald"), puzzle.getPlayers(), "expected correct set of players");
        puzzle.addPlayer("donald"); // add player who already existed
        puzzle.addPlayer("zach");
        assertEquals(Set.of("donald", "zach"), puzzle.getPlayers(), "expected correct set of players");
    }
    
    
    
}
