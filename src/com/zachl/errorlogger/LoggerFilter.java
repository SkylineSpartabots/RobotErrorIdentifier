package com.zachl.errorlogger;

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
 * @version 1.3
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
     *
     */
    private static final String[] MESSAGE_HEADS = {"###", "<<< Warning:", "!!! Error:", "<P><P><P> Sensor Reading:"};
    /**
     *
     */
    private static final String[] MESSAGE_ENDS = {"###", ">>>", "!!!", "<P><P><P>"};
    /**
     * HashMap to store errors, first and last occurence timestamps, and frequency.
     */

    private static String allText;
    private static ArrayList<String> allMessages = new ArrayList<>();
    private static ArrayList<ArrayList<String>> typeMessageLists = new ArrayList<>();
    private static ArrayList<ArrayList<String>> typeTimestampLists = new ArrayList<>();
    private static ArrayList<String> timeStampArray = new ArrayList<>();
    private static ArrayList<String> keysInOrder = new ArrayList<>();
    private static HashMap<String, List<String>> values = new HashMap<>();
    private static ArrayList<HashMap<String, List<String>>> typeValues = new ArrayList<>();

/*
    public static void main(final String[] args)
    {
        if (fileName.equals(""))
        {
            getMostRecentFile();
        }
        for(int i = 0; i < MESSAGE_ENDS.length; i++)
        {
            typeMessageLists.add(new ArrayList<>());
            typeTimestampLists.add(new ArrayList<>());
            typeValues.add(new HashMap<String, List<String>>());
        }
        readFile();
    }
*/

    public static void executeLogger()
    {
        if (fileName.equals(""))
        {
            getMostRecentFile();
        }
        readFile();
    }

    /**
     * Uses the .lastModified(); method to get the name of the most recently created
     * .dsevents file. Only does this if the class variable "fileName" is blank
     * ("").
     */
    private static void getMostRecentFile() {
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
    private static void readFile() {
        try
        {
            final FileReader fr = new FileReader(/* folderPath + fileName */ "exampleErrors.txt");
            final BufferedReader br = new BufferedReader(fr);
            allText = "";
            String contentLine = null/*br.readLine()*/;
            while ((contentLine = br.readLine()) != null) {
                allText += contentLine;
                //contentLine = br.readLine();
            }
            parseData(allText.replaceAll("\\|", "<P>"));
            //Unnecessary bc the try catch closes it anyway
            //br.close();
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
    private static void parseData(String s) throws IOException
    {
        s = "START " + s.trim() + " END";
        while (s.contains(ALERT_KEY_UPPER_BOUND) && s.contains(ALERT_KEY_LOWER_BOUND))
        {
            final int a = s.indexOf(ALERT_KEY_UPPER_BOUND);
            //Removed "+ 5" after alert key lower bound and replaced it w string length
            final int b = s.indexOf(ALERT_KEY_LOWER_BOUND) + ALERT_KEY_LOWER_BOUND.length();
            String logLine = s.substring(a, b);
            logLine = logLine.trim();
            s = s.replaceFirst(logLine, "");
            System.out.println(s);

            for(int i = 0; i < MESSAGE_HEADS.length; i++)
            {
                if(logLine.contains(MESSAGE_HEADS[i]))
                {
                    logLine = logLine.replaceFirst(MESSAGE_HEADS[i], "");
                    logLine = logLine.replaceFirst(MESSAGE_ENDS[i], "");
                    logLine = logLine.trim();
                    typeMessageLists.get(i).add(logLine);
                }
            }
            allMessages.add(logLine.trim());
        }

        for(int j = 0; j < 4; j++)
        {
            typeValues.set(j, hashify(typeMessageLists.get(j), typeTimestampLists.get(j)));
        }

        values = hashify(allMessages, timeStampArray);
        writeToFile(values);
    }

    /**
     * Creates an array of timestamps from the array of errors, and then puts them
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
    private static HashMap<String, List<String>> hashify(final ArrayList<String> errorArray,
            final ArrayList<String> timeStampArray) {
        for (int i = 0; i < errorArray.size(); i++) {
            timeStampArray.add(
                    errorArray.get(i).substring(errorArray.get(i).indexOf("<") + 1, errorArray.get(i).indexOf(">")));
            errorArray.set(i, (errorArray.get(i).replace(
                    errorArray.get(i).substring(errorArray.get(i).indexOf("<"), errorArray.get(i).indexOf(">") + 1),
                    "")).trim());
        }
        final HashMap<String, List<String>> values = new HashMap<>();
        for (final String s : errorArray) {
            if (values.containsKey(s)) {
                values.get(s).set(2, "" + ((Integer.parseInt(values.get(s).get(2))) + 1));
            } else {
                values.put(s, Arrays.asList(timeStampArray.get(errorArray.indexOf(s)),
                        timeStampArray.get(errorArray.lastIndexOf(s)), "1"));
                keysInOrder.add(s);
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
    private static void writeToFile(final HashMap<String, List<String>> values) throws IOException
    {
        final String fileName = LoggerFilter.fileName + " ROBOT_ERROR_IDENTIFIER";

        //final String filePath = "output\\" + fileName;
        final FileWriter fw = new FileWriter(fileName, false);
        final PrintWriter printer = new PrintWriter(fw);
        printer.println("Robot Malfunction(s):");
        for (String s : keysInOrder) {
            printer.println("\"" + s + "\"\nStart: " + values.get(s).get(0) + "   End: " + values.get(s).get(1)
                    + "   Frequency: " + values.get(s).get(2) + "\n");
        }
        printer.close();
        System.out.println("Printed succesfully to file at " + new File("output\\" + fileName).getAbsolutePath());
        //moreDebugging();
    }

    /**
     * A method that allows for further debugging through the use of commands.
     * (Not needed because of GUI)
     * Called after the inital output file is created. Output will be printed to the
     * console OR to "output\commandoutputs".
     */
    private static void moreDebugging() {
        System.out.println("Type \"quit\" to exit");
        System.out.println("Type \"help\" to see a list of commands");
        System.out.println("----------");
        final boolean exit = false;
        final Scanner sc = new Scanner(System.in);
        while (exit != true) {
            System.out.print("Command: \n> ");
            final String cmd = sc.nextLine();
            if (cmd.equalsIgnoreCase("help")) {
                System.out.println("List of Commands:");
                for (final Commands c : Commands.values()) {
                    System.out.print(c.toString() + ": " + c.getDesc() + "\n");
                }
            } else if (cmd.equalsIgnoreCase("quit")) {
                System.out.println("Exited");
                break;
            } else {
                try {
                    final Commands c = Commands.valueOf(cmd);
                    switch (c) {
                    case preverr:
                        prevErrors(sc);
                        System.out.println("Command complete");
                        break;
                    case showseq:
                        showSeq(sc);
                        System.out.println("Command complete");
                        break;
                    case logsinrange:
                        logsInRange(sc);
                        System.out.println("Command complete");
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
     * Allows you to view errors preceeding one of your choice. Amount of previous
     * errors to view can be selected.
     * 
     * @param sc -> The Scanner to be used to scan for user input.
     */
    public static void prevErrors(final Scanner sc) {
        System.out.print("Error to get additional information for:\n> ");
        final String error = sc.nextLine();
        if (values.get(error) != null) {
            System.out.print("Amount of previous errors needed: \n> ");
            int prevNum;
            try {
                prevNum = Integer.parseInt(sc.nextLine());
            } catch (final NumberFormatException e) {
                System.out.println("NaI inputted, defaulting to 5 previous errors");
                prevNum = 5;
            }
            System.out.println("Up to " + prevNum + " errors before/first occurence of \"" + error + "\"\n");
            int counter = 0;
            for (int i = 0; i <= allMessages.indexOf(error); i++) {
                if (allMessages.indexOf(error) - i <= prevNum) {
                    counter++;
                    if (allMessages.indexOf(error) - i != 0) {
                        System.out.println(counter + ": " + allMessages.get(i) + " @t = " + timeStampArray.get(i));
                    } else {
                        System.out.println(
                                "\nError of Interest: " + allMessages.get(i) + " @t = " + timeStampArray.get(i));
                    }
                }
            }
        } else {
            System.out.println("Error does not exist, check spelling.");
        }
    }

    /**
     * Allows you to view a .txt file with all errors logged sequentially.
     * 
     * @param sc -> The Scanner to be used to scan for user input.
     */
    public static void showSeq(final Scanner sc) {
        try {
            final String filePath = "output\\commandoutputs\\" + fileName + " ALLEVENTS";
            final FileWriter fw = new FileWriter(filePath, false);
            final PrintWriter printer = new PrintWriter(fw);
            printer.println("All Errors:");
            for (int i = 0; i < allMessages.size(); i++) {
                printer.println(allMessages.get(i) + " @t = " + timeStampArray.get(i));
            }
            printer.close();
        } catch (Exception e) {
            System.out.println("Failed to print all errors to file.");
            e.printStackTrace();
        }
    }

    /**
     * Displays a list of errors based on a start bound double and an end bound
     * double.
     * 
     * @param sc -> The Scanner to be used to scan for user input.
     */
    public static void logsInRange(final Scanner sc) {
        System.out.print("First timestamp bound (inclusive): \n> ");
        double sb;
        try {
            String line = sc.nextLine();
            sb = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Not a valid double, defaulting to 0");
            sb = 0;
        }
        System.out.print("Last timestamp bound (inclusive): \n> ");
        double eb;
        try {
            String line = sc.nextLine();
            eb = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Not a valid double, defaulting to 100");
            eb = 100;
        }
        try {
            System.out.println("Logs between timestamps " + sb + " and " + eb + "\n");
            for (int i = 0; i < timeStampArray.size(); i++) {
                if ((Double.parseDouble(timeStampArray.get(i))) >= sb
                        && (Double.parseDouble(timeStampArray.get(i)) <= eb)) {
                    System.out.println(allMessages.get(i) + " @t = " + timeStampArray.get(i));
                }
            }
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("Error with number formatting.");
        }
    }

    /**
     * Allows you to view errors preceeding one of your choice. Amount of previous
     * errors to view can be selected.
     */
    public static void prevErrors(String error, String s_prevNum) {
        System.out.print("Error to get additional information for:\n> ");
        if (values.get(error) != null) {
            System.out.print("Amount of previous errors needed: \n> ");
            int prevNum;
            try {
                prevNum = Integer.parseInt(s_prevNum);
            } catch (final NumberFormatException e) {
                System.out.println("NaI inputted, defaulting to 5 previous errors");
                prevNum = 5;
            }
            System.out.println("Up to " + prevNum + " errors before/first occurence of \"" + error + "\"\n");
            int counter = 0;
            for (int i = 0; i <= allMessages.indexOf(error); i++) {
                if (allMessages.indexOf(error) - i <= prevNum) {
                    counter++;
                    if (allMessages.indexOf(error) - i != 0) {
                        System.out.println(counter + ": " + allMessages.get(i) + " @t = " + timeStampArray.get(i));
                    } else {
                        System.out.println(
                                "\nError of Interest: " + allMessages.get(i) + " @t = " + timeStampArray.get(i));
                    }
                }
            }
        } else {
            System.out.println("Error does not exist, check spelling.");
        }
    }

    /**
     * Allows you to view a .txt file with all errors logged sequentially.
     */
    public static void showSeq() {
        try {
            final String filePath = "output\\commandoutputs\\" + fileName + " ALLEVENTS";
            final FileWriter fw = new FileWriter(filePath, false);
            final PrintWriter printer = new PrintWriter(fw);
            printer.println("All Errors:");
            for (int i = 0; i < allMessages.size(); i++) {
                printer.println(allMessages.get(i) + " @t = " + timeStampArray.get(i));
            }
            printer.close();
        } catch (Exception e) {
            System.out.println("Failed to print all errors to file.");
            e.printStackTrace();
        }
    }

    /**
     * Displays a list of errors based on a start bound double and an end bound
     * double.
     */
    public static void logsInRange(String s_sb, String s_eb) {
        System.out.print("First timestamp bound (inclusive): \n> ");
        double sb;
        try {
            String line = s_sb;
            sb = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Not a valid double, defaulting to 0");
            sb = 0;
        }
        System.out.print("Last timestamp bound (inclusive): \n> ");
        double eb;
        try {
            String line = s_eb;
            eb = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Not a valid double, defaulting to 100");
            eb = 100;
        }
        try {
            System.out.println("Logs between timestamps " + sb + " and " + eb + "\n");
            for (int i = 0; i < timeStampArray.size(); i++) {
                if ((Double.parseDouble(timeStampArray.get(i))) >= sb
                        && (Double.parseDouble(timeStampArray.get(i)) <= eb)) {
                    System.out.println(allMessages.get(i) + " @t = " + timeStampArray.get(i));
                }
            }
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("Error with number formatting.");
        }
    }

    /**
     * An Enum that contains command names (these must be typed EXACTLY AS THEY ARE
     * into the console when prompted) and descriptions.
     */
    public enum Commands {
        preverr("Allows you to view errors preceeding one of your choice."),
        showseq("Outputs a list of all errors in order into a .txt file."),
        logsinrange("Allows you to view all errors within two timestamps.");

        String desc;

        private Commands(final String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}