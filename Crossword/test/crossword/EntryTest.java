package crossword;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for the Entry abstract data type.
 */
class EntryTest {
    
    // TESTING STRATEGY
    // partitions on getWord(): word is length 1, word is length > 1
    // partitions on getHint(): hint is 1 word, hint is >1 word
    // partitions on getDirection(): direction is DOWN, direction is ACROSS
    // partitions on getRow(): row = 0, row > 0
    // partitions on getCol(): col = 0, col > 0
    // partitions on toString(): none (partitions are dependent on inputs, which we partition in the partitions above)
    // partitions on equals(): this equals that, this does not equal that
    // partitions on hashCode(): two Entries get hashed to same hash code, two Entries get hashed to different hash codes

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // covers word is length 1, hint is 1 word, direction is DOWN, row = 0, col = 0
    @Test 
    public void testSingleLetterWordRow0Col0() {
        Entry entry = new Entry("a", "article", Direction.DOWN, 0, 0);
        assertEquals("a", entry.getWord(), "Expected correct word");
        assertEquals("article", entry.getHint(), "Expected correct hint");
        assertEquals(Direction.DOWN, entry.getDirection(), "Expected correct direction");
        assertEquals(0, entry.getRow(), "Expected correct row");
        assertEquals(0, entry.getCol(), "Expected correct col");
        assertEquals(12, entry.hashCode(), "Expected correct hash code");
        assertEquals("(a, \"article\", DOWN, 0, 0, \"*\")", entry.toString(), "Expected correct string representation");
    }
    
    // covers word is length > 1, hint is > 1 word, direction is ACROSS, row > 0, col > 0
    @Test 
    public void testLongerWordAndHint() {
        Entry entry = new Entry("hello", "A common greeting", Direction.ACROSS, 1, 1);
        assertEquals("hello", entry.getWord(), "Expected correct word");
        assertEquals("A common greeting", entry.getHint(), "Expected correct hint");
        assertEquals(Direction.ACROSS, entry.getDirection(), "Expected correct direction");
        assertEquals(1, entry.getRow(), "Expected correct row");
        assertEquals(1, entry.getCol(), "Expected correct col");
        assertEquals(30, entry.hashCode(), "Expected correct hash code");
        assertEquals("(hello, \"A common greeting\", ACROSS, 1, 1, \"*****\")", entry.toString(), "Expected correct string representation");        
    }
    
    // covers this equals that, two Entries get hashed to same hash code
    @Test
    public void testTwoEqualEntries() {
        Entry entry1 = new Entry("hello", "A common greeting", Direction.ACROSS, 1, 1);
        Entry entry2 = new Entry("hello", "A common greeting", Direction.ACROSS, 1, 1);
        assertTrue(entry1.equals(entry2));
        assertTrue(entry1.hashCode() == entry2.hashCode());
    }
    
    // covers this does not equal that, two Entries get hashed to different hash codes
    @Test
    public void testTwoUnequalEntries() {
        Entry entry1 = new Entry("hello", "A common greeting", Direction.ACROSS, 1, 1);
        Entry entry2 = new Entry("goodbye", "A common departure term", Direction.DOWN, 0, 0);
        assertTrue(!entry1.equals(entry2));
        assertTrue(entry1.hashCode() != entry2.hashCode());
    }

}
