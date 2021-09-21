package crossword;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for the MinimalEntry abstract data type.
 */
class MinimalEntryTest {
    
    // TESTING STRATEGY
    // partitions on getIndex(): index is 0; >0
    // partitions on getHint(): hint is 1 word, hint is >1 word
    // partitions on getDirection(): direction is DOWN, direction is ACROSS
    // partitions on getRow(): row = 0, row > 0
    // partitions on getCol(): col = 0, col > 0
    // partitions on getlength(): length = 1; length > 1
    // partitions on getGuess(): guess consists of only '*'s, guess has other characters
    // partitions on toString(): none (partitions are dependent on inputs, which we partition in the partitions above)
    // partitions on equals(): this equals that, this does not equal that
    // partitions on hashCode(): two Entries get hashed to same hash code, two Entries get hashed to different hash codes

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // covers word is length 1, hint is 1 word, direction is DOWN, row = 0, col = 0, index = 0, guess has only '*'s
    @Test 
    public void testSingleLetterWordRow0Col0() {
        MinimalEntry entry = new MinimalEntry(0, "article", Direction.DOWN, 0, 0, 1, "*".repeat(1));
        assertEquals(0, entry.getIndex(), "Expected correct index");
        assertEquals("article", entry.getHint(), "Expected correct hint");
        assertEquals(Direction.DOWN, entry.getDirection(), "Expected correct direction");
        assertEquals(0, entry.getRow(), "Expected correct row");
        assertEquals(0, entry.getCol(), "Expected correct col");
        assertEquals(1, entry.getLength(), "Expected correct length");
        assertEquals("*".repeat(1), entry.getGuess(), "Expected correct guess");
        assertEquals(13, entry.hashCode(), "Expected correct hash code");
        assertEquals("(0, \"article\", DOWN, 0, 0, 1, \"*\")", entry.toString(), "Expected correct string representation");
    }
    
    // covers word is length > 1, hint is > 1 word, direction is ACROSS, row > 0, col > 0, index > 0, guess has other characters
    @Test 
    public void testLongerWordAndHint() {
        MinimalEntry entry = new MinimalEntry(3, "A common greeting", Direction.ACROSS, 1, 1, 5, "hallo");
        assertEquals(3, entry.getIndex(), "Expected correct index");
        assertEquals("A common greeting", entry.getHint(), "Expected correct hint");
        assertEquals(Direction.ACROSS, entry.getDirection(), "Expected correct direction");
        assertEquals(1, entry.getRow(), "Expected correct row");
        assertEquals(1, entry.getCol(), "Expected correct col");
        assertEquals(5, entry.getLength(), "Expected correct length");
        assertEquals("hallo", entry.getGuess(), "Expected correct guess");
        assertEquals(38, entry.hashCode(), "Expected correct hash code");
        assertEquals("(3, \"A common greeting\", ACROSS, 1, 1, 5, \"hallo\")", entry.toString(), "Expected correct string representation");        
    }

}

