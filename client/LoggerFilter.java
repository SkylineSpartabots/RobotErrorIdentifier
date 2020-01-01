package client;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * A client that can be run at the end of matches to parse through .dsevents files and output a ".txt" file that only contains important information about robot malfunctions.
 * Helpful for post-match diagnostics.
 * @version
 * 1.0s
 * @author
 * Team 2976!
 */
public class LoggerFilter {
    /**
     * Location of all .dsevents files, filepath can be found through Driver Station Log File Viewer. 
     */
    private static final String folderPath = "C:\\Users\\Public\\Documents\\FRC\\Log Files\\";
    /**
     * Set to "" if you want to get the most recent file, set to a filename if you want to read that specific file. 
     * Be sure to add ".dsevents" to the end of the filename.
     */
    private static String fileName = "";
    /**
     * Upper bound to use when parsing for errors.
     */
    private static final String ALERT_KEY_UPPER_BOUND = "!!!!!!!! Error:";
    /**
     * Lower bound to use when parsing for errors.
     */
    private static final String ALERT_KEY_LOWER_BOUND = "!!!!!!!!";

    public static void main(final String[] args) {
        if(fileName.equals("")) {
            getMostRecentFile();
        }
        readFile();
    }
    /**
     * Uses the .lastModified(); method to get the name of the most recently created .dsevents file.
     * Only does this if the class variable "fileName" is blank ("").
     */
    public static void getMostRecentFile() {
        final File directory = new File(folderPath);
        final File[] allFiles = directory.listFiles();
        long lastModTime = allFiles[0].lastModified();
        File mostRecentFile = allFiles[0];
        for (final File f : allFiles) {
            if(f.lastModified() > lastModTime) {
                lastModTime = f.lastModified();
                mostRecentFile = f;
            }
            fileName = mostRecentFile.getName();
        }
    }
    /**
     * Reads the file in as a stream of bytes and concatenates the String information from these bytes onto a String called "allText".
     */
    public static void readFile() {
        try {
            final byte[] buffer = new byte[1024];
            final FileInputStream fis = new FileInputStream(folderPath + fileName);
            String allText = "";
            while (fis.read(buffer) != -1) {
                String nextLine = null;
                nextLine = new String(buffer);
                allText += nextLine;
            }
            parseData(allText);
            fis.close();
        } catch (final FileNotFoundException e) {
            System.out.println("Failed to find file.");
            e.printStackTrace();
        } catch (final IOException e) {
            System.out.println("Failed to read file.");
            e.printStackTrace();
        }
    }
    /**
     * Parses through the .dsevents file as a String and looks for specific error messages bounded by "ALERT_KEY_UPPER_BOUND" and "ALERT_KEY_LOWER_BOUND"
     * (These variables can be found at the top of the class as class constants). 
     * It then adds these parsed and filtered error messages to an ArrayList<String> object.
     * @param s -> The .dsevents file as a String.
     * @throws IOException
     */
    public static void parseData(String s) throws IOException {
        final ArrayList<String> allMessages = new ArrayList<String>();
        s = s.replaceAll(ALERT_KEY_UPPER_BOUND, "<S>");
        s = s.replaceAll(ALERT_KEY_LOWER_BOUND, "<E>");
        while (s.contains("<S>") && s.contains("<E>")) {
            final int a = s.indexOf("<S>");
            final int b = s.indexOf("<E>") + 3;
            final String logLine = s.substring(a, b);
            s = s.replaceFirst(logLine, "");
            allMessages.add(logLine);
        }
        writeToFile(allMessages);
    }
    /**
     * Writes an ArrayList<String> of parsed errors to an output file.
     * For more information on the output file, check "README.txt" in the output folder.
     * @param list -> The ArrayList<String> object that will be printed to the file.
     * @throws IOException
     */
    public static void writeToFile(final ArrayList<String> list) throws IOException {
        final String fileName = LoggerFilter.fileName + " ROBOT_ERROR_IDENTIFIER";

        final String filePath = "output\\" + fileName;
        final FileWriter fw = new FileWriter(filePath, false);
        final PrintWriter printer = new PrintWriter(fw);
        printer.println("Robot Malfunction(s):");
        for (final String s : list) {
            printer.println(s);
        }
        printer.close();
    }
}
