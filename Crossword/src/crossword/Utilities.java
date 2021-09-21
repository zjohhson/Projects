package crossword;


public class Utilities {
    
    /**
     * Given a filepath that uses backward slashes, extracts the final filename from the path WITHOUT the extension
     * i.e. \puzzles\simple.puzzle -> simple
     * @param filePath the file path starting from the folder where the puzzles are located.
     * @return a String of the filename without file extensions from the filePath
     */
    public static String getFileNameWithoutExtension(String filePath) {
        final int fileNameStartIndex = filePath.lastIndexOf("\\") + 1;
        final String removeExtensionRegex = "[.][^.]+$";
        final String fileNameWithExtension = (fileNameStartIndex == -1) ? filePath : filePath.substring(fileNameStartIndex); 
        final String fileNameWithoutExtension = fileNameWithExtension.replaceFirst(removeExtensionRegex, "");        
        return fileNameWithoutExtension;
    }
    
    /**
     * Given a filepath that uses forward slashses, extracts the final filename from the path WITHOUT the extension
     * i.e. /puzzles/simple.puzzle -> simple
     * @param filePath the file path starting from the folder where the puzzles are located.
     * @return a String of the filename without file extensions from the filePath
     */
    public static String getFileNameWithoutExtensionWithForwardSlash(String filePath) {
        final int fileNameStartIndex = filePath.lastIndexOf("/") + 1;
        final String removeExtensionRegex = "[.][^.]+$";
        final String fileNameWithExtension = (fileNameStartIndex == -1) ? filePath : filePath.substring(fileNameStartIndex); 
        final String fileNameWithoutExtension = fileNameWithExtension.replaceFirst(removeExtensionRegex, "");        
        return fileNameWithoutExtension;
    }
}
