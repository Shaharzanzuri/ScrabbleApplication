package Logic;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

public class BooksDirectoryReader {

    private String[] fileNameArray;

    //private static BooksDirectoryReader = null;

    public BooksDirectoryReader() {
        String directoryPath = "./src/main/resources/books";
        File directory = new File(directoryPath);
        // Get all files in the directory
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    // Add the file name to the list
                    fileNames.add(directory + "\\"+file.getName());
                }
            }
        }
        // Convert the list to an array of strings
        fileNameArray = fileNames.toArray(new String[0]);
    }

    public String[] getBooks() {
        return fileNameArray;
    }


}



