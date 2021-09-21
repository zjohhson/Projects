package crossword;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.mit.eecs.parserlib.UnableToParseException;


/**
 * Tests for the PuzzleParser abstract data type.
 */
class PuzzleParserTest {

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    @Test
    public void testParserSimplePuzzle() throws UnableToParseException {
        String puzzleString = ">> \"Easy\" \"An easy puzzle to get started\"\r\n" + 
                "\r\n" + 
                "(star, \"twinkle twinkle\", ACROSS, 1, 0)\r\n" + 
                "(market, \"Farmers ______\", DOWN, 0, 2)\r\n" + 
                "(kettle, \"It's tea time!\", ACROSS, 3, 2)\r\n" + 
                "(extra, \"more\", DOWN, 1, 5)(bee, \"Everyone loves honey\", ACROSS, 4, 0)\r\n" + 
                "(treasure, \"Every pirate's dream\", ACROSS, 5, 2)\r\n" + 
                "(troll, \"Everyone's favorite twitter pastime\", ACROSS, 4, 4)\r\n" + 
                "(loss, \"This is not a gain\", DOWN, 3, 6)";
        
        Puzzle puzzle = PuzzleParser.parse(puzzleString);
    }
    
    
    @Test
    public void testMinimalParserSimplePuzzle() throws UnableToParseException {
        String puzzleString = ">> \"Easy\" \"An easy puzzle to get started\"\r\n" + 
                "\r\n" + 
                "(0, \"twinkle twinkle\", ACROSS, 1, 0, 4, \"****\")\r\n" + 
                "(1, \"Farmers ______\", DOWN, 0, 2, 6, \"******\")\r\n" + 
                "(2, \"It's tea time!\", ACROSS, 3, 2, 6, \"******\")\r\n" + 
                "(3, \"more\", DOWN, 1, 5, 5, \"*****\")(4, \"Everyone loves honey\", ACROSS, 4, 0, 3, \"***\")";
        
        MinimalPuzzle puzzle = MinimalPuzzleParser.parse(puzzleString);
    }

}
