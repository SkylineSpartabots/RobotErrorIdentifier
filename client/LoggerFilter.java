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
import java.util.Scanner;

/**
 * A client that can be run at the end of matches to parse through .dsevents
 * files and output a ".txt" file that only contains important information about
 * robot malfunctions. Helpful for post-match diagnostics.
 * 
 * @version 1.1
 * @author Team 2976!
 */
public class LoggerFilter {
    /**
     * Location of all .dsevents files, filepath can be found through Driver Station
     * Log File Viewer.
     */
    private static final String folderPath = "C:\\Users\\Public\\Documents\\FRC\\Log Files\\";
    /**
     * Set to "" if you want to get the most recent file, set to a filename if you
     * want to read that specific file. Be sure to add ".dsevents" to the end of the
     * filename.
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
    /**
     * HashMap to store errors, firt and last occurence timestamps, and frequency.
     */
    private static String allText;
    private static ArrayList<String> allMessages = new ArrayList<>();
    private static ArrayList<String> timeStampArray = new ArrayList<>();
    private static HashMap<String, List<String>> values = new HashMap<>();

    public static void main(final String[] args) {
        if (fileName.equals("")) {
            getMostRecentFile();
        }
        readFile();
    }

    /**
     * Uses the .lastModified(); method to get the name of the most recently created
     * .dsevents file. Only does this if the class variable "fileName" is blank
     * ("").
     */
    public static void getMostRecentFile() {
        final File directory = new File(folderPath);
        final File[] allFiles = directory.listFiles();
        long lastModTime = allFiles[0].lastModified();
        File mostRecentFile = allFiles[0];
        for (final File f : allFiles) {
            if (f.lastModified() > lastModTime) {
                lastModTime = f.lastModified();
                mostRecentFile = f;
            }
            fileName = mostRecentFile.getName();
        }
    }

    /**
     * Reads the file in through BufferedReader and concatenates each readLine()
     * onto a String called "allText".
     */
    public static void readFile() {
        try {
            final FileReader fr = new FileReader(/* folderPath + fileName */ "info\\2019_12_03 20_02_03 Tue.dsevents");
            final BufferedReader br = new BufferedReader(fr);
            allText = "";
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
     * Parses through the .dsevents file as a String and looks for specific error
     * messages bounded by "ALERT_KEY_UPPER_BOUND" and "ALERT_KEY_LOWER_BOUND"
     * (These variables can be found at the top of the class as class constants). It
     * then adds these parsed and filtered error messages to an ArrayList<String>
     * object.
     * 
     * @param s -> The .dsevents file as a String.
     * @throws IOException
     */
    public static void parseData(String s) throws IOException {
        s = "START " + s.trim() + " END";
        while (s.contains(ALERT_KEY_UPPER_BOUND) && s.contains(ALERT_KEY_LOWER_BOUND)) {
            final int a = s.indexOf(ALERT_KEY_UPPER_BOUND);
            final int b = s.indexOf(ALERT_KEY_LOWER_BOUND) + 5;
            String logLine = s.substring(a, b);
            s = s.replaceFirst(logLine, "");
            logLine = logLine.replaceAll(ALERT_KEY_UPPER_BOUND, "");
            logLine = logLine.replaceAll(ALERT_KEY_LOWER_BOUND, "");
            logLine = logLine.replaceAll("###", "");
            logLine = logLine.replaceAll("<<< Warning:", "");
            logLine = logLine.replaceAll(">>>", "");
            logLine = logLine.replaceAll("!!! Error:", "");
            logLine = logLine.replaceAll("!!!", "");
            logLine = logLine.replaceAll("<P><P><P> Sensor Reading:", "");
            logLine = logLine.replaceAll("<P><P><P>", "");
            allMessages.add(logLine.trim());
        }
        values = hashify(allMessages, timeStampArray);
        writeToFile(values);
    }

    /**
     * Creates an array of timestamps from the array of errors, a nd then puts them
     * into a HashMap that contains initial timestamp, final timestamp, and
     * frequency.
     * 
     * @param errorArray     -> ArrayList<String> of errors with timestamps
     *                       included.
     * @param timeStampArray -> Arraylist<String> that timestamps will be moved to
     *                       by the end of the method.
     * @return A Hashmap with the error as a key (String), and a List<String> of
     *         initial timestamps, final timestamps, and frequencies for each error.
     */
    public static HashMap<String, List<String>> hashify(ArrayList<String> errorArray,
            ArrayList<String> timeStampArray) {
        for (int i = 0; i < errorArray.size(); i++) {
            timeStampArray.add(
                    errorArray.get(i).substring(errorArray.get(i).indexOf("<") + 1, errorArray.get(i).indexOf(">")));
            errorArray.set(i, (errorArray.get(i).replace(
                    errorArray.get(i).substring(errorArray.get(i).indexOf("<"), errorArray.get(i).indexOf(">") + 1),
                    "")).trim());
        }
        HashMap<String, List<String>> values = new HashMap<>();
        for (String s : errorArray) {
            if (values.containsKey(s)) {
                values.put(s, Arrays.asList(values.get(s).get(0), values.get(s).get(1),
                        "" + ((Integer.parseInt(values.get(s).get(2))) + 1)));
            } else {
                values.put(s, Arrays.asList(timeStampArray.get(errorArray.indexOf(s)),
                        timeStampArray.get(errorArray.lastIndexOf(s)), "1"));
            }
        }
        return values;
    }

    /**
     * Writes a HashMap of parsed errors to an output file in an elegant way. For
     * more information on the output file, check "README.txt" in the output folder.
     * 
     * @param values -> The HashMap object that will be printed to the file.
     * @throws IOException
     */
    public static void writeToFile(HashMap<String, List<String>> values) throws IOException {
        final String fileName = LoggerFilter.fileName + " ROBOT_ERROR_IDENTIFIER";

        final String filePath = "output\\" + fileName;
        final FileWriter fw = new FileWriter(filePath, false);
        final PrintWriter printer = new PrintWriter(fw);
        printer.println("Robot Malfunction(s):");
        values.forEach((key, value) -> printer.println("\"" + key + "\"\nStart: " + value.get(0) + "   End: "
                + value.get(1) + "   Frequency: " + value.get(2) + "\n"));
        printer.close();
        System.out.println("Printed succesfully to file at " + new File("output\\" + fileName).getAbsolutePath());
        moreDebugging();
    }

    /**
     * A method that allows for further debugging through the use of commands.
     * Output will be printed to the CONSOLE, not a file.
     */
    public static void moreDebugging() {
        System.out.println("Type \"quit\" to exit");
        System.out.println("Type \"help\" to see a list of options");
        System.out.println("----------");
        boolean exit = false;
        Scanner sc = new Scanner(System.in);
        while (exit != true) {
            System.out.print("Command: \n> ");
            String cmd = sc.nextLine();
            if (cmd.equalsIgnoreCase("help")) {
                for (Commands c : Commands.values()) {
                    System.out.print(c.toString() + ": " + c.getDesc() + "\n");
                }
            } else if (cmd.equalsIgnoreCase("quit")) {
                System.out.println("Exited");
                break;
            } else {
                try {
                    Commands c = Commands.valueOf(cmd);
                    switch (c) {
                    case PREV_ERRORS:
                        prevErrors(sc);
                        System.out.println("Command complete");
                        break;
                    default:
                    }
                } catch (final Exception e) {
                    System.out.println("Command does not exist");
                }
            }
            System.out.println("----------");
        }
        sc.close();
    }

    /**
     * Allows you to view errors preceeding one of your choice.
     * 
     * @param sc -> The Scanner to be used to scan for user input.
     */
    public static void prevErrors(Scanner sc) {
        System.out.print("Error to get additional information for:\n> ");
        String error = sc.nextLine();
        if (values.get(error) != null) {
            System.out.print("Amount of previous errors needed: \n> ");
            int prevNum;
            try {
                prevNum = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("NaI inputted, defaulting to 5 previous errors");
                prevNum = 5;
            }
            System.out.println(prevNum + " errors before and first occurence of \"" + error + "\"");
            for (int i = 0; i <= allMessages.indexOf(error); i++) {
                if (allMessages.indexOf(error) - i <= prevNum) {
                    System.out.println(allMessages.get(i) + " " + timeStampArray.get(i));
                }
            }
        } else {
            System.out.println("Error does not exist, check spelling.");
        }
    }

    /**
     * An Enum that contains command names (these must be typed EXACTLY AS THEY ARE
     * into the console when prompted) and descriptions.
     */
    public enum Commands {
        PREV_ERRORS("Allows you to view errors preceeding one of your choice."), BLANK_COMMAND("N/A");

        String desc;

        private Commands(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
