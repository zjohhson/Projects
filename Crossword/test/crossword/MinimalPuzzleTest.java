package crossword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.mit.eecs.parserlib.UnableToParseException;


/**
 * Tests for the MinimalPuzzle abstract data type.
 */
public class MinimalPuzzleTest {
    
    // TESTING STRATEGY
    // partitions on getNumRows(): numRows = 1, numRows > 1
    // partitions on getNumCols(): numCols = 1, numCols > 1
    // partitions on getEntryList(): entryList length 1, entryList length >1
    // partitions on equals(that): this equals that, this does not equal that
    // partitions on hashCode(): two objects are equivalent (have same hash code), two objects have different hash codes
    // partitions on parseFromString(): puzzle is valid according to grammar, puzzle is invalid according to grammar
    // two entries intersect, no intersection
    //
    //
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // covers entryList.size() = 1, numRows = 1, numCols = 1, no intersection
    @Test
    public void testOneSingleLetterWord() {
        MinimalEntry entry = new MinimalEntry(0, "article", Direction.ACROSS, 0, 0, 1, "*");
        MinimalPuzzle puzzle = new MinimalPuzzle(List.of(entry), "Easy", "An easy puzzle to get started");
        assertEquals(1, puzzle.getNumRows(), "expected correct number of rows");
        assertEquals(1, puzzle.getNumCols(), "expected correct number of columns");
        assertEquals(List.of(new MinimalEntry(0, "article", Direction.ACROSS, 0, 0, 1, "*")), puzzle.getEntryList(), "expected correct entryList");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(0, \"article\", ACROSS, 0, 0, 1, \"*\")", puzzle.toString(), "expected correct string rep of puzzle");
    }
    
    // covers entryList.size() > 1, numRows > 1, numCols > 1, this does not equal that, entries intersect
    @Test
    public void testMultipleWords() {
        MinimalEntry entry1 = new MinimalEntry(0, "a common greeting", Direction.ACROSS, 3, 0, 5, "*".repeat(5));
        MinimalEntry entry2 = new MinimalEntry(1, "a common farewell", Direction.DOWN, 1, 4, 7, "*".repeat(7));
        MinimalPuzzle puzzle = new MinimalPuzzle(List.of(entry1, entry2), "Easy", "An easy puzzle to get started");
        assertEquals(8, puzzle.getNumRows(), "expected correct number of rows");
        assertEquals(5, puzzle.getNumCols(), "expected correct number of columns");
        assertEquals(List.of(new MinimalEntry(0, "a common greeting", Direction.ACROSS, 3, 0, 5, "*".repeat(5)), new MinimalEntry(1, "a common farewell", Direction.DOWN, 1, 4, 7, "*".repeat(7))),
                puzzle.getEntryList(), "expected correct entry list");
        assertEquals(">> \"Easy\" \"An easy puzzle to get started\"\n\n"
                + "(0, \"a common greeting\", ACROSS, 3, 0, 5, \"*****\")\n"
                + "(1, \"a common farewell\", DOWN, 1, 4, 7, \"*******\")", puzzle.toString(), "expected correct string rep of puzzle");
    }
    
    // covers this does not equal that, two objects have different hash codes
    @Test
    public void testTwoUnequalPuzzles() {
        MinimalEntry entry1 = new MinimalEntry(0, "a common greeting", Direction.ACROSS, 3, 0, 5, "*".repeat(5));
        MinimalEntry entry2 = new MinimalEntry(0, "a common farewell", Direction.DOWN, 1, 4, 7, "*".repeat(7));
        MinimalPuzzle puzzle1 = new MinimalPuzzle(List.of(entry1), "Easy", "An easy puzzle to get started");
        MinimalPuzzle puzzle2 = new MinimalPuzzle(List.of(entry2), "Easy", "An easy puzzle to get started");
        assertFalse(puzzle1.equals(puzzle2), "expected puzzles to not be equal");
        assertFalse(puzzle1.hashCode() == puzzle2.hashCode(), "expected puzzles to have different hash codes");
    }
    
    // covers this equals that, two objects have same hash code
    @Test
    public void testTwoEqualPuzzles() {
        MinimalEntry entry1 = new MinimalEntry(0, "a common greeting", Direction.ACROSS, 3, 0, 5, "*".repeat(5));
        MinimalEntry entry2 = new MinimalEntry(0, "a common greeting", Direction.ACROSS, 3, 0, 5, "*".repeat(5));
        MinimalPuzzle puzzle1 = new MinimalPuzzle(List.of(entry1), "Easy", "An easy puzzle to get started");
        MinimalPuzzle puzzle2 = new MinimalPuzzle(List.of(entry2), "Easy", "An easy puzzle to get started");
        assertTrue(puzzle1.equals(puzzle2), "expected puzzles to be equal");
        assertTrue(puzzle1.hashCode() == puzzle2.hashCode(), "expected puzzles to have same hash code");
    }
    
    // covers puzzle is valid according to grammar
    @Test
    public void testValidPuzzle() throws IOException{
        Puzzle puzzle = Puzzle.parseFromFile("puzzles/simple.puzzle");
        String clientString = puzzle.makeClientString();
        MinimalPuzzle minpuzzle = MinimalPuzzle.parseFromString(clientString);
        assertEquals(10, minpuzzle.getNumCols(), "expected correct number of columns");
        assertEquals(7, minpuzzle.getNumRows(), "expected correct number of rows");
        assertEquals("Easy", minpuzzle.getName(), "expected correct name");
        assertEquals("An easy puzzle to get started", minpuzzle.getDescription(), "expected correct description");
        assertEquals(List.of(new MinimalEntry(0, "twinkle twinkle", Direction.ACROSS, 1, 0, 4, "*".repeat(4)),
                new MinimalEntry(1, "Farmers ______", Direction.DOWN, 0, 2, 6, "*".repeat(6)),
                new MinimalEntry(2, "It's tea time!", Direction.ACROSS, 3, 2, 6, "*".repeat(6)),
                new MinimalEntry(3, "more", Direction.DOWN, 1, 5, 5, "*".repeat(5)),
                new MinimalEntry(4, "Everyone loves honey", Direction.ACROSS, 4, 0, 3, "*".repeat(3)),
                new MinimalEntry(5, "Every pirate's dream", Direction.ACROSS, 5, 2, 8, "*".repeat(8)),
                new MinimalEntry(6, "Everyone's favorite twitter pastime", Direction.ACROSS, 4, 4, 5, "*".repeat(5)),
                new MinimalEntry(7, "This is not a gain", Direction.DOWN, 3, 6, 4, "*".repeat(4))), minpuzzle.getEntryList(), "expected correct entry list");        
    }
    
    // covers puzzle is invalid according to grammar
    @Test
    public void testInvalidMinPuzzle() throws UnableToParseException, IOException {
        String invalidClientString = ">> \"Easy\" \"An easy puzzle to get started\" \n \n (star, \"twinkle twinkle\", 1, 0)";
        assertThrows(IllegalArgumentException.class, () -> {MinimalPuzzle.parseFromString(invalidClientString);}, "expected exception to be thrown");
    }
    
    
    
}
