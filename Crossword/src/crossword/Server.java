/* Copyright (c) 2019-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package crossword;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Crossword puzzle server runner
 */
public class Server {
    
    private static final int PORT = 4949;
    
    /**
     * Start a Crossword Extravaganza server using the given arguments.
     *
     * <p> Command-line usage:
     * <pre> java memory.ServerMain FILEPATH </pre>
     * where:
     * 
     * <p> FILEPATH is the path to a valid puzzle file, which will be loaded as 
     *     the starting puzzle
     *     
     * <p> For example, to start a web server on a randomly-chosen port using the
     *     puzzle in {@code puzzle/simple.puzzle}:
     * <pre> 0 puzzles/simple.puzzle </pre>
     * 
     * @param args The command line arguments should include only the folder where
     *             the puzzles are located.
     * @throws IOException if an error occurs parsing a puzzle file or starting a server
     */
    public static void main(String[] args) throws IOException{
        final Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
        
        // Filters for .puzzle files
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".puzzle");
            }
        };
        
        final File folder = new File(arguments.remove());
        final File[] listOfFiles = folder.listFiles(filter);
        
        final Map<String, Puzzle> puzzleNameToPuzzle = new HashMap<>();
        final Map<String, String> puzzleNameToPuzzleDirectory = new HashMap<>();
        for (File file : listOfFiles) {
            // Do stuff with each file
            String filePath = file.getPath();
            String fileName;
            if (filePath.contains("\\")) {
                fileName = Utilities.getFileNameWithoutExtension(filePath);
            }
            else {
                fileName = Utilities.getFileNameWithoutExtensionWithForwardSlash(filePath);
                filePath.replaceAll("/", "[\\]");
            }
            try {
                Puzzle puzzle = Puzzle.parseFromFile(filePath);
                puzzleNameToPuzzle.put(fileName, puzzle);
                puzzleNameToPuzzleDirectory.put(fileName, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (AssertionError e) {
                System.out.println(filePath + " is an inconsistent puzzle");
            }
        }
        new WebServer(puzzleNameToPuzzle, puzzleNameToPuzzleDirectory, PORT).start();
        
    }
  
}
