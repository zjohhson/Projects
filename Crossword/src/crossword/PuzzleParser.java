package crossword;


import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;

public class PuzzleParser {
    
    /**
     * Main method. Parses and then reprints an example puzzle.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example puzzle can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {
        final String input = ">> \"Easy\" \"An easy puzzle to get started\"  (star, \"twinkle twinkle\", ACROSS, 1, 0)";
        System.out.println(input);
        final Puzzle puzzle = PuzzleParser.parse(input);
        System.out.println(puzzle);
    }
    
    // a helper map mapping each of the entry's components to each index in a components list
    private static final Map<String, Integer> ENTRY_COMP_TO_IDX = Map.of("wordname",  0,
                                                                         "clue",      1,
                                                                         "direction", 2,
                                                                         "row",       3,
                                                                         "col",       4);
    
    // the nonterminals of the grammar
    private static enum PuzzleGrammar {
        FILE, NAME, DESCRIPTION, ENTRY, WORDNAME,  CLUE, DIRECTION, ROW, COL, 
        STRING, INT, WHITESPACE
    }

    private static Parser<PuzzleGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @return parser for the grammar in Puzzle.g
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<PuzzleGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/crossword/Puzzle.g");
            return Parser.compile(grammarFile, PuzzleGrammar.FILE);
            
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
     * Parse a string into a puzzle.
     * 
     * @param string string to parse
     * @return Puzzle parsed from the entire string
     * @throws UnableToParseException if the string doesn't match the Puzzle grammar
     */
    public static Puzzle parse(final String string) throws UnableToParseException {
        // parse the example into a parse tree
        final ParseTree<PuzzleGrammar> parseTree = parser.parse(string);

        // display the parse tree in various ways, for debugging only
//         System.out.println("parse tree " + parseTree);
        // Visualizer.showInBrowser(parseTree);

        // make an AST from the parse tree
        final Puzzle puzzle = makeAbstractSyntaxTree(parseTree);
        
        return puzzle;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in Puzzle.g
     * @return abstract syntax tree corresponding to the parseTree
     */
    private static Puzzle makeAbstractSyntaxTree(final ParseTree<PuzzleGrammar> parseTree) {
        switch (parseTree.name()) {
        
        case FILE: // FILE ::= ">>" NAME DESCRIPTION ENTRY*;
        {
            final List<ParseTree<PuzzleGrammar>> children = parseTree.children();
            
            // omitting quotation marks
            String name = children.get(0).children().get(0).text();
            String description = children.get(1).children().get(0).text();
            
            List<Entry> entryList = new ArrayList<>();
            for (int i = 2; i < children.size(); i++) {
                List<ParseTree<PuzzleGrammar>> entryComponents = children.get(i).children();
                Entry entry = getEntryfromChildren(entryComponents);
                entryList.add(entry);
            }
            
//            System.out.println(name);
//            System.out.println(description);
//            System.out.println("first entry:");
//            System.out.println(entryList.get(0).getWord());
//            System.out.println(entryList.get(0).getHint());
//            System.out.println(entryList.get(0).getRow());
//            System.out.println(entryList.get(0).getCol());
            
            Puzzle puzzle = new Puzzle(entryList, name, description);
            return puzzle;
        }            
        
        default:
            throw new AssertionError("should never get here");
        }

    }
    
    /**
     * Get an entry out of the given entry components
     * @param entryComponents the children in the parse tree corresponding to the entry's components
     * @return an entry corresponding to the given entry components
     */
    private static Entry getEntryfromChildren(List<ParseTree<PuzzleGrammar>> entryComponents) {
        
        assert entryComponents.size() == ENTRY_COMP_TO_IDX.size();
        
        final String wordname = entryComponents.get(ENTRY_COMP_TO_IDX.get("wordname")).text();
        
        final String clue = entryComponents.get(ENTRY_COMP_TO_IDX.get("clue")).children().get(0).text();
        
        final String directionString = entryComponents.get(ENTRY_COMP_TO_IDX.get("direction")).text();        
        Direction direction = directionString.equals("DOWN") ? Direction.DOWN : Direction.ACROSS;
        
        final int row = Integer.parseInt(entryComponents.get(ENTRY_COMP_TO_IDX.get("row")).text());
        
        final int col = Integer.parseInt(entryComponents.get(ENTRY_COMP_TO_IDX.get("col")).text());
        
        return new Entry(wordname, clue, direction, row, col);
    }
    

}
