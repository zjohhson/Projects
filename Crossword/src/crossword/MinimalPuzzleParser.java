package crossword;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;

public class MinimalPuzzleParser {
    /**
     * Main method. Parses and then reprints an example minimal puzzle.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example puzzle can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {
        final String input = ">> \"Easy\" \"An easy puzzle to get started\"  (0, \"twinkle twinkle\", ACROSS, 1, 0, 4, \"****\")";
        System.out.println(input);
        final MinimalPuzzle puzzle = MinimalPuzzleParser.parse(input);
        System.out.println(puzzle);
    }
    
    // a helper map mapping each of the minimal entry's components to each index in a components list
    private static final Map<String, Integer> ENTRY_COMP_TO_IDX = Map.of("index",     0,
                                                                         "clue",      1,
                                                                         "direction", 2,
                                                                         "row",       3,
                                                                         "col",       4,
                                                                         "length",    5,
                                                                         "guess",     6);
    
    // the nonterminals of the grammar
    private static enum MinimalPuzzleGrammar {
        FILE, NAME, DESCRIPTION, ENTRY, INDEX, CLUE, DIRECTION, ROW, COL, LENGTH, GUESS,
        STRING, INT, WHITESPACE
    }

    private static Parser<MinimalPuzzleGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @return parser for the grammar in Puzzle.g
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<MinimalPuzzleGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/crossword/MinimalPuzzle.g");
            return Parser.compile(grammarFile, MinimalPuzzleGrammar.FILE);
            
        // Parser.compile() throws two checked exceptions.
        // Translate these checked exceptions into unchecked RuntimeExceptions,
        // because these failures indicate internal bugs rather than client errors
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }

    /**
     * Parse a string into a minimal puzzle.
     * 
     * @param string string to parse
     * @return MinimalPuzzle parsed from the entire string
     * @throws UnableToParseException if the string doesn't match the MinimalPuzzle grammar
     */
    public static MinimalPuzzle parse(final String string) throws UnableToParseException {
        // parse the example into a parse tree
        final ParseTree<MinimalPuzzleGrammar> parseTree = parser.parse(string);

        // display the parse tree in various ways, for debugging only
//          System.out.println("parse tree " + parseTree);
        // Visualizer.showInBrowser(parseTree);

        // make an AST from the parse tree
        final MinimalPuzzle puzzle = makeAbstractSyntaxTree(parseTree);
        
        return puzzle;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in MinimalPuzzle.g
     * @return abstract syntax tree corresponding to the parseTree
     */
    private static MinimalPuzzle makeAbstractSyntaxTree(final ParseTree<MinimalPuzzleGrammar> parseTree) {
        switch (parseTree.name()) {
        
        case FILE: // FILE ::= ">>" NAME DESCRIPTION ENTRY*;
        {
            final List<ParseTree<MinimalPuzzleGrammar>> children = parseTree.children();
            // ToDo: add name, description as rep fields in Puzzle 
            String name = children.get(0).children().get(0).text();
            String description = children.get(1).children().get(0).text();
            
            List<MinimalEntry> entryList = new ArrayList<>();
            for (int i = 2; i < children.size(); i++) {
                List<ParseTree<MinimalPuzzleGrammar>> entryComponents = children.get(i).children();
                MinimalEntry entry = getEntryfromChildren(entryComponents);
                entryList.add(entry);
            }
            
//          System.out.println(name);
//          System.out.println(description);
//          System.out.println("first entry:");
//          System.out.println(entryList.get(0).getIndex());
//          System.out.println(entryList.get(0).getHint());
//          System.out.println(entryList.get(0).getRow());
//          System.out.println(entryList.get(0).getCol());
//          System.out.println(entryList.get(0).getLength());
//          System.out.println(entryList.get(0).getGuess());
            
          MinimalPuzzle puzzle = new MinimalPuzzle(entryList, name, description);
          return puzzle;
        }            
        
        default:
            throw new AssertionError("should never get here");
        }

    }
    
    /**
     * Get a minimal entry out of the given entry components
     * @param entryComponents the children in the parse tree corresponding to the entry's components
     * @return a minimal entry corresponding to the given entry components
     */
    private static MinimalEntry getEntryfromChildren(List<ParseTree<MinimalPuzzleGrammar>> entryComponents) {
        
        assert entryComponents.size() == ENTRY_COMP_TO_IDX.size();
        
        final int index = Integer.parseInt(entryComponents.get(ENTRY_COMP_TO_IDX.get("index")).text());
        
        final String clue = entryComponents.get(ENTRY_COMP_TO_IDX.get("clue")).children().get(0).text();
        
        final String directionString = entryComponents.get(ENTRY_COMP_TO_IDX.get("direction")).text();        
        Direction direction = directionString.equals("DOWN") ? Direction.DOWN : Direction.ACROSS;
        
        final int row = Integer.parseInt(entryComponents.get(ENTRY_COMP_TO_IDX.get("row")).text());
        
        final int col = Integer.parseInt(entryComponents.get(ENTRY_COMP_TO_IDX.get("col")).text());
        
        final int length = Integer.parseInt(entryComponents.get(ENTRY_COMP_TO_IDX.get("length")).text());
        
        final String guess = entryComponents.get(ENTRY_COMP_TO_IDX.get("guess")).children().get(0).text();
        
        return new MinimalEntry(index, clue, direction, row, col, length, guess);
    }

}
