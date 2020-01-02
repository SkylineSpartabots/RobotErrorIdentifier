package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private static final String ALERT_KEY_UPPER_BOUND = "S_LOG";
    /**
     * Lower bound to use when parsing for errors.
     */
    private static final String ALERT_KEY_LOWER_BOUND = "E_LOG";

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
     * Reads the file in through BufferedReader and concatenates each readLine() onto a String called "allText".
     */
    public static void readFile() {
        try {
            final FileReader fr = new FileReader(folderPath + fileName);
            final BufferedReader br = new BufferedReader(fr);
            String allText = "";
            String contentLine = br.readLine();
	        while (contentLine != null) {
                allText += contentLine;
                contentLine = br.readLine();           
            }
            parseData(allText.replaceAll("\\|", "<P>"));
            br.close();
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
        s = "START " + s.trim() + " END";
        while (s.contains(ALERT_KEY_UPPER_BOUND) && s.contains(ALERT_KEY_LOWER_BOUND)) {
            final int a = s.indexOf(ALERT_KEY_UPPER_BOUND);
            final int b = s.indexOf(ALERT_KEY_LOWER_BOUND) + 5;
            String logLine = s.substring(a, b);
            s = s.replaceFirst(logLine, "");
            logLine = logLine.replaceAll(ALERT_KEY_UPPER_BOUND, "");
            logLine = logLine.replaceAll(ALERT_KEY_LOWER_BOUND, "");
            logLine = logLine.replaceAll("########", "");
            logLine = logLine.replaceAll("<<<<<<<< Warning:", "");
            logLine = logLine.replaceAll(">>>>>>>>", "");
            logLine = logLine.replaceAll("!!!!!!!! Error:", "");
            logLine = logLine.replaceAll("!!!!!!!!", "");
            logLine = logLine.replaceAll("<P><P><P> Sensor Reading:", "");
            logLine = logLine.replaceAll("<P><P><P>", "");
            allMessages.add(logLine.trim());
        }
        ArrayList<String> timeStampArray = new ArrayList<>();
        HashMap<String, List<String>> values = hashify(allMessages, timeStampArray);
        writeToFile(values);
    }
    /**
     * Creates an array of timestamps from the array of errors, a nd then puts them into a HashMap that contains initial timestamp, final timestamp, and frequency.
     * @param errorArray -> ArrayList<String> of errors with timestamps included.
     * @param timeStampArray -> Arraylist<String> that timestamps will be moved to by the end of the method.
     * @return A Hashmap with the error as a key (String), and a List<String> of initial timestamps, final timestamps, and frequencies for each error.
     */
    public static HashMap<String, List<String>> hashify(ArrayList<String> errorArray , ArrayList<String> timeStampArray) {
        for (int i = 0; i < errorArray.size(); i++) {
            timeStampArray.add(errorArray.get(i).substring(errorArray.get(i).indexOf("<") + 1, errorArray.get(i).indexOf(">")));
            errorArray.set(i, (errorArray.get(i).replace(errorArray.get(i).substring(errorArray.get(i).indexOf("<"), errorArray.get(i).indexOf(">") + 1), "")).trim());
        }
        HashMap<String, List<String>> values = new HashMap<>();
        for(String s : errorArray) {
            if(values.containsKey(s)) {
                values.put(s, Arrays.asList(values.get(s).get(0), values.get(s).get(1), "" + ((Integer.parseInt(values.get(s).get(2))) + 1)));
            } else {
                values.put(s, Arrays.asList(timeStampArray.get(errorArray.indexOf(s)), timeStampArray.get(errorArray.lastIndexOf(s)),"1"));
            }
        }
        return values;
    }
    /**
     * Writes an ArrayList<String> of parsed errors to an output file.
     * For more information on the output file, check "README.txt" in the output folder.
     * @param list -> The ArrayList<String> object that will be printed to the file.
     * @throws IOException
     */
    public static void writeToFile(HashMap<String, List<String>> values) throws IOException {
        final String fileName = LoggerFilter.fileName + " ROBOT_ERROR_IDENTIFIER";

        final String filePath = "output\\" + fileName;
        final FileWriter fw = new FileWriter(filePath, false);
        final PrintWriter printer = new PrintWriter(fw);
        printer.println("Robot Malfunction(s):");
        values.forEach((key,value) -> printer.println("The error \"" + key + 
        "\" occurred first at timestamp " + value.get(0) +
        " and last at timestamp " + value.get(1) +
        ".\nIt occurred a total of " + value.get(2) +
        " times during this session. \n"));
        printer.close();
    }
}
