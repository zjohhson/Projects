package crossword.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JComponent;

import crossword.Direction;
import crossword.MinimalEntry;
import crossword.MinimalPuzzle;
import crossword.MinimalPuzzleParser;

public class NewCrosswordCanvas extends JComponent{
    

    private Optional<MinimalPuzzle> minimalPuzzle = Optional.empty();
    private List<String> allActivePlayers = new ArrayList<>();

    /**
     * Sets a new minimal puzzle object
     * @param minimalPuzzle a Minimal Puzzle Object
     */
    public void setMinimalPuzzle(Optional<MinimalPuzzle> minimalPuzzle) {
        this.minimalPuzzle = minimalPuzzle.isPresent() ? minimalPuzzle : Optional.empty();
    }
    
    /**
     * Sets a new list of active players playing on this canvas
     * @param allActivePlayers a list of strings representing playerIDs
     */
    public void setActivePlayerList(List<String> allActivePlayers) {
        this.allActivePlayers = new ArrayList<>(allActivePlayers);
    }
    
    /**
     * Returns a defensive copy of this minimalPuzzle
     * @return minimal puzzle object representing the current minimal puzzle on this canvas
     */
    public MinimalPuzzle getMinimalPuzzle() {
        return minimalPuzzle.isPresent() ? minimalPuzzle.get() : new MinimalPuzzle(List.of(), "test", "nothing");
    }
    
    /**
     * Returns a defensive copy of this allActivePlayers
     * @return list of strings representing playerIDs of players currently playing on this canvas
     */
    public List<String> getAllActivePlayers() {
        return new ArrayList<>(allActivePlayers);
    }
    
    
    /**
     * Horizontal offset from corner for first cell.
     */
    private final int originX = 25;
    /**
     * Vertical offset from corner for first cell.
     */
    private final int originY = 80;
    /**
     * Size of each cell in crossword. Use this to rescale your crossword to have
     * larger or smaller cells.
     */
    private final int delta = 30;

    /**
     * Font for letters in the crossword.
     */
    private final Font mainFont = new Font("Arial", Font.PLAIN, delta * 4 / 5);

    /**
     * Font for small indices used to indicate an ID in the crossword.
     */
    private final Font indexFont = new Font("Arial", Font.PLAIN, delta / 3);

    /**
     * Font for small indices used to indicate an ID in the crossword.
     */
    private final Font textFont = new Font("Arial", Font.PLAIN, 16);

    /**
     * Draw a cell at position (row, col) in a crossword.
     * @param row Row where the cell is to be placed.
     * @param col Column where the cell is to be placed.
     * @param g Graphics environment used to draw the cell.
     */
    private void drawCell(int row, int col, Graphics g) {
        g.drawRect(originX + col * delta,
                   originY + row * delta, delta, delta);
    }

    /**
     * Place a letter inside the cell at position (row, col) in a crossword.
     * @param letter Letter to add to the cell.
     * @param row Row position of the cell.
     * @param col Column position of the cell.
     * @param g Graphics environment to use.
     */
    private void letterInCell(String letter, int row, int col, Graphics g) {
        g.setFont(mainFont);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(letter, originX + col * delta + delta / 6,
                             originY + row * delta + fm.getAscent() + delta / 10);
    }
    
    /**
     * Add a vertical ID for the cell at position (row, col).
     * @param id ID to add to the position.
     * @param row Row position of the cell.
     * @param col Column position of the cell.
     * @param g Graphics environment to use.
     */
    private void verticalId(String id, int row, int col, Graphics g) {
        g.setFont(indexFont);
        g.drawString(id, originX + col * delta + delta / 8,
                         originY + row * delta - delta / 15);
    }

    /**
     * Add a horizontal ID for the cell at position (row, col).
     * @param id ID to add to the position.
     * @param row Row position of the cell.
     * @param col Column position of the cell.
     * @param g Graphics environment to use.
     */
    private void horizontalId(String id, int row, int col, Graphics g) {
        g.setFont(indexFont);
        FontMetrics fm = g.getFontMetrics();
        int maxwidth = fm.charWidth('0') * id.length();
        g.drawString(id, originX + col * delta - maxwidth - delta / 8,
                         originY + row * delta + fm.getAscent() + delta / 15);
    }

    // The three methods that follow are meant to show you one approach to writing
    // in your canvas. They are meant to give you a good idea of how text output and
    // formatting work, but you are encouraged to develop your own approach to using
    // style and placement to convey information about the state of the game.

    private int line = 0;
    
    // The Graphics interface allows you to place text anywhere in the component,
    // but it is useful to have a line-based abstraction to be able to just print
    // consecutive lines of text.
    // We use a line counter to compute the position where the next line of code is
    // written, but the line needs to be reset every time you paint, otherwise the
    // text will keep moving down.
    private void resetLine() {
        line = 0;
    }

    // This code illustrates how to write a single line of text with a particular
    // color.
    private void println(String s, Graphics g) {
        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();
        // Before changing the color it is a good idea to record what the old color
        // was.
        Color oldColor = g.getColor();
        g.setColor(new Color(100, 0, 0));
        g.drawString(s, originX + 500, originY + line * fm.getAscent() * 6 / 5);
        // After writing the text you can return to the previous color.
        g.setColor(oldColor);
        ++line;
    }
    
    // This code illustrates how to write a single line of text with a particular
    // color.
    private void printlnHints(String s, Graphics g) {
        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();
        // Before changing the color it is a good idea to record what the old color
        // was.
        Color oldColor = g.getColor();
        g.setColor(new Color(100, 0, 0));
        g.drawString(s, originX + 400, originY + line * fm.getAscent() * 6 / 5);
        // After writing the text you can return to the previous color.
        g.setColor(oldColor);
        ++line;
    }
    
    // This code illustrates how to write a single line of text with a particular
    // color.
    private void printlnPlayers(String s, Graphics g) {
        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();
        // Before changing the color it is a good idea to record what the old color
        // was.
        Color oldColor = g.getColor();
        g.setColor(new Color(100, 0, 0));
        g.drawString(s, originX + 800, originY + line * fm.getAscent() * 6 / 5);
        // After writing the text you can return to the previous color.
        g.setColor(oldColor);
        ++line;
    }

    // This code shows one approach for fancier formatting by changing the
    // background color of the line of text.
    private void printlnFancy(String s, Graphics g) {

        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getAscent() * 6 / 5;
        int xpos = originX + 500;
        int ypos = originY + line * lineHeight;

        // Before changing the color it is a good idea to record what the old color
        // was.
        Color oldColor = g.getColor();

        g.setColor(new Color(0, 0, 0));
        g.fillRect(xpos, ypos - fm.getAscent(), fm.stringWidth(s), lineHeight);
        g.setColor(new Color(200, 200, 0));
        g.drawString(s, xpos, ypos);
        // After writing the text you can return to the previous color.
        g.setColor(oldColor);
        ++line;
    }

    private int x = 1;

    /**
     * Simple demo code just to illustrate how to paint cells in a crossword puzzle.
     * The paint method is called every time the JComponent is refreshed, or every
     * time the repaint method of this class is called.
     * We added some state just to allow you to see when the class gets repainted,
     * although in general you wouldn't want to be mutating state inside the paint
     * method.
     */
    @Override
    public void paint(Graphics g) {
        if (minimalPuzzle.isPresent()) {
            MinimalPuzzle puzzle = minimalPuzzle.get();
            drawPuzzle(puzzle, g);
            drawHints(puzzle, g);
            drawPlayerList(allActivePlayers, g);
        }
        g.setColor(new Color(0, 0, 255));
//        for (int i = 0; i < x; ++i) {
//            drawCell(i, i, g);
//            letterInCell(Character.toString(i + 65), i, i, g);
//            verticalId(Integer.toString(i), i, i, g);
//            horizontalId(Integer.toString(i), i, i, g);
//            resetLine();
//            println("This is an example of adding text to the canvas.", g);
//            println("You can use formatting to convey information about the state of the game.", g);
//            println("Remember, this code is mostly here to show you how things work.", g);
//            println("Make it your own.", g);
//            printlnFancy("It's ok to get fancy with format.", g);
//            printlnFancy("Have some fun with your UI!", g);
//        }
//        x = x + 1;
    }
    
    /**
     * Add an index ID for the cell at position (row, col) for DOWN.
     * @param id ID to add to the position.
     * @param row Row position of the cell.
     * @param col Column position of the cell.
     * @param g Graphics environment to use.
     */
    private void indexIdDown(String id, int row, int col, Graphics g) {
        g.setFont(indexFont);
        g.drawString(id, originX + col * delta + delta / 8,
                         originY + row * delta + delta / 3);
    }
    
    /**
     * Add an index ID for the cell at position (row, col) for ACROSS.
     * @param id ID to add to the position.
     * @param row Row position of the cell.
     * @param col Column position of the cell.
     * @param g Graphics environment to use.
     */
    private void indexIdAcross(String id, int row, int col, Graphics g) {
        g.setFont(indexFont);
        g.drawString(id, originX + col * delta + delta / 8 + 5*delta/8,
                         originY + row * delta + delta / 3);
    }
    
    /**
     * Draw the grid for the puzzle which the client will play; black cells correspond to cells that contain no letters,
     * white cells correspond to cells that contain letters. We add each guess of MinimalPuzzle according to its proper location.
     * @param puzzle a MinimalPuzzle
     * @param g
     */
    public void drawPuzzle(MinimalPuzzle puzzle, Graphics g) {
        int numRows = puzzle.getNumRows();
        int numCols = puzzle.getNumCols();
        for (Integer row = 0; row < numRows; row++) {
            for (Integer col = 0; col < numCols; col++) {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(originX + col * delta, originY + row * delta, delta, delta);
                g.setColor(new Color(255, 255, 255));
                drawCell(row, col, g);
            }
        }
        for (MinimalEntry entry: puzzle.getEntryList()) {
            int startingRow = entry.getRow();
            int startingCol = entry.getCol();
            Direction direction = entry.getDirection();
            int length = entry.getLength();
//            String guess = entry.getGuess();
//            System.out.println("drawPuzzle(): guess: " + guess);
            if (direction.equals(Direction.DOWN)) {
                for (Integer i = 0; i < length; i++) {
                    g.setColor(new Color(255, 255, 255));
                    g.fillRect(originX + startingCol * delta,  originY + (startingRow+i) * delta,  delta,  delta);
                    g.setColor(new Color(0, 0, 0));
                    drawCell(startingRow+i, startingCol, g);                    
//                    if (guess.charAt(i) != '*') {
//                        letterInCell(Character.toString(guess.charAt(i)), startingRow+i, startingCol, g);
//                    }                        
//                    if (i == 0) {
//                        indexIdDown(""+entry.getIndex(), startingRow + i, startingCol, g);}
                }
            }
            else {
                for (Integer i = 0; i < length; i++) {
                    g.setColor(new Color(255, 255, 255));
                    g.fillRect(originX + (startingCol+i) * delta,  originY + startingRow * delta,  delta,  delta);
                    g.setColor(new Color(0, 0, 0));
                    drawCell(startingRow, startingCol+i, g);
//                    if (guess.charAt(i) != '*') {
//                        letterInCell(Character.toString(guess.charAt(i)), startingRow, startingCol+i, g);
//                    }
//                    if (i == 0) {
//                        indexIdAcross(""+entry.getIndex(), startingRow, startingCol + i, g);}
                }
            }            
        }
        
        for (MinimalEntry entry: puzzle.getEntryList()) {
            int startingRow = entry.getRow();
            int startingCol = entry.getCol();
            Direction direction = entry.getDirection();
            int length = entry.getLength();
            if (direction.equals(Direction.DOWN)) {
                for (Integer i = 0; i < length; i++) {
                    if (i == 0) {
                        indexIdDown(""+entry.getIndex(), startingRow + i, startingCol, g);}
                }
            }
            else {
                for (Integer i = 0; i < length; i++) {
                    if (i == 0) {
                        indexIdAcross(""+entry.getIndex(), startingRow, startingCol + i, g);}
                }
            }            
        }
        
        for (MinimalEntry entry: puzzle.getEntryList()) {
            int startingRow = entry.getRow();
            int startingCol = entry.getCol();
            Direction direction = entry.getDirection();
            int length = entry.getLength();
            String guess = entry.getGuess();
            
            if (direction.equals(Direction.DOWN)) {
                for (Integer i = 0; i < length; i++) {                    
                    if (guess.charAt(i) != '*') {
                        letterInCell(Character.toString(guess.charAt(i)), startingRow+i, startingCol, g);
                    }
                }
            }
            else {
                for (Integer i = 0; i < length; i++) {
                    if (guess.charAt(i) != '*') {
                        letterInCell(Character.toString(guess.charAt(i)), startingRow, startingCol+i, g);
                    }
                }
            }
        }
        
    }
    
    
    /**
     * Draw the hints of a puzzle in separate lines, grouped in Across and Down.
     * 
     * @param minpuzzle the minimal puzzle to draw from
     * @param g Graphics environment to use
     */
    public void drawHints(MinimalPuzzle minpuzzle, Graphics g) {
        final String acrossString = "Across:";
        final String downString = "Down:";
        
        final Map<Integer, String> indexToHintAcross = new HashMap<>();
        final Map<Integer, String> indexToHintDown = new HashMap<>();
        
        final List<MinimalEntry> entryList = minpuzzle.getEntryList();
        
        for (MinimalEntry entry : entryList) {
            if (entry.getDirection().equals(Direction.ACROSS)) {
                indexToHintAcross.put(entry.getIndex(), entry.getHint());
            } else {
                indexToHintDown.put(entry.getIndex(), entry.getHint());
            }
        }
        
        resetLine();
        
        printlnHints(acrossString, g);        
        for (int index : indexToHintAcross.keySet()) {
            String hint = indexToHintAcross.get(index);
            String stringToPrint = index + ". " + hint;
            printlnHints(stringToPrint, g);
        }
        
        printlnHints(downString, g);        
        for (int index : indexToHintDown.keySet()) {
            String hint = indexToHintDown.get(index);
            String stringToPrint = index + ". " + hint;
            printlnHints(stringToPrint, g);
        }        
    }
    
    /**
     * Draw all players that are currently playing within the puzzle
     * @param minpuzzle the minimal puzzle to draw from
     * @param g Graphics environment to use
     */
    public void drawPlayerList(List<String> allActivePlayers, Graphics g) {
        resetLine();
        printlnPlayers("Active Players:", g);
        for (String player: allActivePlayers) {
            printlnPlayers(player, g);
        }
    }

    
}