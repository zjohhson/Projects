package crossword.gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class NewCrosswordCanvasTest {
    
    // TESTING STRATEGY 
    // partitions on number of rows: 1, >1
    // partitions on number of columns: 1, >1
    // partitions on locations of words: at least two words overlap, no words overlap
    
    // MANUAL TESTS
    // covers number of rows > 1, number of columns > 1, at least two words overlap
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }

    // MANUAL TESTS
    // covers number of rows >1, number of columns > 1, at least two words overlap
    // run the Client UI with simple.puzzle
    // assert that the hints have correct indices
    // assert that the hints belong to the right direction
    // assert that the hints correspond to the correct word length and direction in the puzzle board
    // assert that each word starts in the correct place
    // assert that the correct squares are black and the correct squares are right
    // assert that the name and description are correct
    // assert that the board has 10 columns and 7 rows
    
    // covers number of rows = 1, number of column = 1, no two words overlap (one single letter word)
    // run the client UI with a single letter word starting at 0,0
    // assert that the board has 1 row and 1 column
    // assert that the hint is correct and is assigned to the correct direction
    // assert that the word length is 1
    // assert that the only puzzle square is white
    // assert that the name and description are correct
}
