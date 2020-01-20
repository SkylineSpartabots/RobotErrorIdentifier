package com.zachl.errorlogger;

import com.zachl.errorlogger.gui.GraphManager;
import sun.rmi.runtime.Log;

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
 * A client that can be run at the end of matches to parse through .dsevents
 * files and output a ".txt" file that only contains important information about
 * robot malfunctions. Helpful for post-match diagnostics.
 * 
 * @version 2.1.0
 * @author Team 2976!
 */
public class LoggerFilter {
    /**
     * Location of all .dsevents files, filepath can be found through Driver Station
     * Log File Viewer.
     */
    //private static final String folderPath = "C:\\Users\\Public\\Documents\\FRC\\Log Files\\";
    private static final String folderPath = "info/";
    /**
     * Set to "" if you want to get the most recent file, set to a filename if you
     * want to read that specific file. Be sure to add ".dsevents" to the end of the
     * filename.
     */
    public static String fileName = "";
    /**
     * Whole path to file.
     */
    private static String wholePath = folderPath + fileName;
    /**
     * Upper bound to use when parsing for errors.
     */
    private static final String ALERT_KEY_UPPER_BOUND = "S_LOG";
    /**
     * Lower bound to use when parsing for errors.
     */
    private static final String ALERT_KEY_LOWER_BOUND = "E_LOG";

    public static final String[] MESSAGE_HEADS = { "###", "<<< Warning:", "!!! Error:", "<P><P><P> Sensor Reading:" };
    public static final String[] MESSAGE_ENDS = { "###", ">>>", "!!!", "<P><P><P>" };

    public static final String[] TYPE_KEYS = { "Message", "Warning", "Error", "Sensor Data" };
    public static final String[] SUBSYSTEM_KEYS = { "Drive", "Hopper", "Climb", "Intake", "Limelight", "Encoder"};

    public static ArrayList<String> KEYS_IN_ORDER = new ArrayList<>();

    private static String allText;

    private static LogList allLogs = new LogList();
    private static ArrayList<LogList> typeLogs = new ArrayList<>();
    private static ArrayList<LogList> subsystemLogs = new ArrayList<>();
    private static LogList toParse = new LogList();

    private static boolean compounding = false;

    /*public static void executeLogger() {
        readFile();
    }*/

    public static void executeLogger()
    {
        if (fileName.equals(""))
        {
            getMostRecentFile();
        }
        for(int i = 0; i < MESSAGE_ENDS.length; i++)
        {
            typeLogs.add(new LogList());
        }
        for(int i = 0; i < SUBSYSTEM_KEYS.length; i++)
        {
            subsystemLogs.add(new LogList());
        }
        readFile();
        //testCompound();
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
            fileName = "exampleEvents.dsevents";
            //fileName = mostRecentFile.getName();
            wholePath = folderPath + fileName;
        }
    }

    /**
     * Reads the file in through BufferedReader and concatenates each readLine()
     * onto a String called "allText".
     */
    private static void readFile() {
        try {
            final FileReader fr = new FileReader(wholePath);
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
            LoggerGUI.printToFrame("Failed to find file.");
            e.printStackTrace();
        } catch (final IOException e) {
            LoggerGUI.printToFrame("Failed to read file.");
            e.printStackTrace();
        }
    }

    /**
     * Parses through the .dsevents file as a String and looks for specific error
     * messages bounded by "ALERT_KEY_UPPER_BOUND" and "ALERT_KEY_LOWER_BOUND"
     * (These variables can be found at the top of the class as class constants). It
     * then adds these parsed and filtered error messages to an ArrayList<String>
     * object. Added more!
     * 
     * @param s -> The .dsevents file as a String.
     * @throws IOException
     */
    private static void parseData(String s) throws IOException {
        /*for (int i = 0; i < MESSAGE_ENDS.length; i++) {
            typeLogs.add(new LogList());
        }
        for (int i = 0; i < SUBSYSTEM_KEYS.length; i++) {
            subsystemLogs.add(new LogList());
        }*/
        s = s.trim();
        while (s.contains(ALERT_KEY_UPPER_BOUND) && s.contains(ALERT_KEY_LOWER_BOUND)) {
            final int a = s.indexOf(ALERT_KEY_UPPER_BOUND);
            final int b = s.indexOf(ALERT_KEY_LOWER_BOUND) + ALERT_KEY_LOWER_BOUND.length();
            String logLine = s.substring(a, b);
            logLine = logLine.trim();
            s = s.replaceFirst(logLine, "");

            logLine = logLine.replaceAll(ALERT_KEY_UPPER_BOUND, "");
            logLine = logLine.replaceAll(ALERT_KEY_LOWER_BOUND, "");

            for (int i = 0; i < MESSAGE_HEADS.length; i++)
            {
                if (logLine.contains(MESSAGE_HEADS[i]))
                {
                    logLine = logLine.replaceFirst(MESSAGE_HEADS[i], "");
                    logLine = logLine.replaceFirst(MESSAGE_ENDS[i], "");
                    logLine = logLine.trim();
                    typeLogs.get(i).messages.add(logLine);
                }
            }
            for (int i = 0; i < SUBSYSTEM_KEYS.length; i++) {
                if (logLine.contains(SUBSYSTEM_KEYS[i])) {
                    subsystemLogs.get(i).messages.add(logLine);
                }
            }
            allLogs.messages.add(logLine.trim());
        }

        for (int j = 0; j < MESSAGE_HEADS.length; j++) {
            typeLogs.get(j).values = (hashify(typeLogs.get(j).messages, typeLogs.get(j).timeStamps));
        }
        for (int i = 0; i < SUBSYSTEM_KEYS.length; i++) {
            subsystemLogs.get(i).values = hashify(subsystemLogs.get(i).messages, subsystemLogs.get(i).timeStamps);
        }

        allLogs.values = hashify(allLogs.messages, allLogs.timeStamps);
        writeToFile(allLogs.values);
    }

    /**
     * Creates an array of timestamps from the array of errors, and then puts them
     * into a HashMap that contains initial timestamp, final timestamp, and
     * frequency.
     * 
     * @param errorArray         -> ArrayList<String> of errors with timestamps
     *                           included.
     * @param //allLogs.timeStamps -> Arraylist<String> that timestamps will be moved
     *                           to by the end of the method.
     * @return A Hashmap with the error as a key (String), and a List<String> of
     *         initial timestamps, final timestamps, and frequencies for each error.
     */
    private static HashMap<String, List<String>> hashify(final ArrayList<String> errorArray,
            final ArrayList<String> timeStamps) {
        for (int i = 0; i < errorArray.size(); i++) {
            timeStamps.add(
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
                values.put(s, Arrays.asList(timeStamps.get(errorArray.indexOf(s)),
                        timeStamps.get(errorArray.lastIndexOf(s)), "1"));
                if (allLogs.messages.equals(errorArray)) {
                    KEYS_IN_ORDER.add(s);
                }
            }
        }
        return values;
    }

    /**
     * Creates a String[] from KEYS_IN_ORDER.
     * 
     * @return A String array that has the same elements of KEYS_IN_ORDER
     */
    public static String[] getErrors() {
        String[] errorArr = new String[KEYS_IN_ORDER.size()];
        for (int i = 0; i < KEYS_IN_ORDER.size(); i++) {
            errorArr[i] = KEYS_IN_ORDER.get(i);
        }
        return errorArr;
    }

    /**
     * Writes a HashMap of parsed errors to an output file in an elegant way. For
     * more information on the output file, check "README.txt" in the output folder.
     * 
     * @param values -> The HashMap object that will be printed to the file.
     * @throws IOException
     */
    private static void writeToFile(final HashMap<String, List<String>> values) throws IOException {
        final String fileName = LoggerFilter.fileName + " ROBOT_ERROR_IDENTIFIER";

        final String filePath = "output/mainoutput/" + fileName;
        final FileWriter fw = new FileWriter(filePath, false);
        final PrintWriter printer = new PrintWriter(fw);
        printer.println("Robot Malfunction(s):");
        for (String s : KEYS_IN_ORDER) {
            printer.println("\"" + s + "\"\nStart: " + values.get(s).get(0) + "   End: " + values.get(s).get(1)
                    + "   Frequency: " + values.get(s).get(2) + "\n");
        }
        printer.close();
        LoggerGUI.printToFrame(
                "Base output printed succesfully to file at " + new File("output/mainoutput/" + fileName).getAbsolutePath());
    }

    /**
     * Allows you to view errors preceeding one of your choice. Amount of previous
     * errors to view can be selected.
     */
    public static void prevErrors(String error, String s_prevNum) {
        if (allLogs.values.get(error) != null) {
            int prevNum;
            try {
                prevNum = Integer.parseInt(s_prevNum);
            } catch (final NumberFormatException e) {
                LoggerGUI.printToFrame("NaI inputted, defaulting to 5 previous errors");
                prevNum = 5;
            }
            LoggerGUI.printToFrame("Up to " + prevNum + " errors before/first occurence of \"" + error + "\"\n");
            int counter = 0;
            for (int i = 0; i <= allLogs.messages.indexOf(error); i++) {
                if (allLogs.messages.indexOf(error) - i <= prevNum) {
                    counter++;
                    if (allLogs.messages.indexOf(error) - i != 0) {
                        LoggerGUI.printToFrame(
                                counter + ": " + allLogs.messages.get(i) + " @t = " + allLogs.timeStamps.get(i));
                    } else {
                        LoggerGUI.printToFrame("\nError of Interest: " + allLogs.messages.get(i) + " @t = "
                                + allLogs.timeStamps.get(i));
                    }
                }
            }
        } else {
            LoggerGUI.printToFrame("Error does not exist, check spelling.");
        }
    }

    /**
     * Allows you to view a .txt file with all errors logged sequentially.
     */
    public static void showSeq() {
        try {
            final String filePath = "output\\commandoutput\\" + fileName + " ALLEVENTS";
            final FileWriter fw = new FileWriter(filePath, false);
            final PrintWriter printer = new PrintWriter(fw);
            printer.println("All Errors:");
            for (int i = 0; i < allLogs.messages.size(); i++) {
                printer.println(allLogs.messages.get(i) + " @t = " + allLogs.timeStamps.get(i));
            }
            LoggerGUI.printToFrame(
                    "Succesfully printed to file at " + "output\\commandoutput\\" + fileName + " ALLEVENTS.txt");
            printer.close();
        } catch (Exception e) {
            LoggerGUI.printToFrame("Failed to print all errors to file.");
            e.printStackTrace();
        }
    }

    /**
     * Displays a list of errors based on a start bound double and an end bound
     * double.
     */
    public static void logsInRange(String s_sb, String s_eb) {
        LogList finalParsed = new LogList();

        if (!compounding) {
            LoggerGUI.printToFrame("Parsing from all logs");
            toParse = allLogs;
        }

        double sb;
        try {
            String line = s_sb;
            sb = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            LoggerGUI.printToFrame("Not a valid double, defaulting to 0");
            sb = 0;
        }
        double eb;
        try {
            String line = s_eb;
            eb = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            LoggerGUI.printToFrame("Not a valid double, defaulting to 100");
            eb = 100;
        }
        try {
            LoggerGUI.printToFrame("Logs between timestamps " + sb + " and " + eb + "\n");
            for (int i = 0; i < toParse.timeStamps.size(); i++) {
                if ((Double.parseDouble(toParse.timeStamps.get(i))) >= sb
                        && (Double.parseDouble(toParse.timeStamps.get(i)) <= eb)) {
                    LoggerGUI.printToFrame(toParse.messages.get(i) + " @t = " + toParse.timeStamps.get(i));
                    finalParsed.messages.add(toParse.messages.get(i));
                    finalParsed.timeStamps.add(toParse.timeStamps.get(i));
                }
            }
            toParse = finalParsed;
            LoggerGUI.printToFrame("");
        } catch (NumberFormatException e) {
            LoggerGUI.printToFrame("Error with number formatting.");
        }
    }

    public static void logsByType(String s_type) {
        LogList finalParsed = new LogList();
        try {
            for (int i = 0; i < TYPE_KEYS.length; i++) {
                if (!compounding && s_type.equalsIgnoreCase(TYPE_KEYS[i])) {
                    LoggerGUI.printToFrame("Parsing from full type log");
                    if (typeLogs.get(i) != null)
                        finalParsed = typeLogs.get(i);
                    else {
                        LoggerGUI.printToFrame("Type log: " + s_type + " is null, defaulting to all logs");
                        finalParsed = allLogs;
                    }
                } else if (s_type.equalsIgnoreCase(TYPE_KEYS[i])) {
                    for (int j = 0; j < typeLogs.get(i).messages.size(); j++) {
                        for (int k = 0; k < toParse.messages.size(); k++) {
                            if (toParse.messages.get(k).equalsIgnoreCase(typeLogs.get(i).messages.get(j))
                                    && toParse.timeStamps.get(k).equalsIgnoreCase(typeLogs.get(i).timeStamps.get(j))) {
                                finalParsed.messages.add(toParse.messages.get(k));
                                finalParsed.timeStamps.add(toParse.timeStamps.get(k));
                                break;
                            }
                        }
                    }
                }
            }
            toParse = finalParsed;
        } catch (NullPointerException e) {
            LoggerGUI.printToFrame("Invalid log type, defaulting to error");
            s_type = "Error";
            toParse = typeLogs.get(2);
        }
        LoggerGUI.printToFrame("All messages of type: " + s_type);
        for (int i = 0; i < toParse.messages.size(); i++) {
            LoggerGUI.printToFrame(toParse.messages.get(i) + " @t = " + toParse.timeStamps.get(i));
        }
    }

    public static void logsBySubsystem(String s_type)
    {
        LogList finalParsed = new LogList();

        try {
            for (int i = 0; i < SUBSYSTEM_KEYS.length; i++) {
                if (!compounding && s_type.equalsIgnoreCase(SUBSYSTEM_KEYS[i])) {
                    LoggerGUI.printToFrame("Parsing from full subsystem log");
                    if (subsystemLogs.get(i) != null)
                        finalParsed = subsystemLogs.get(i);
                    else {
                        LoggerGUI.printToFrame( s_type + " log is null, defaulting to all logs");
                        finalParsed = allLogs;
                    }
                } else if (s_type.equalsIgnoreCase(SUBSYSTEM_KEYS[i])) {
                    for (int j = 0; j < subsystemLogs.get(i).messages.size(); j++) {
                        for (int k = 0; k < toParse.messages.size(); k++) {
                            if (toParse.messages.get(k).equalsIgnoreCase(subsystemLogs.get(i).messages.get(j))
                                    && toParse.timeStamps.get(k).equalsIgnoreCase(subsystemLogs.get(i).timeStamps.get(j))) {
                                finalParsed.messages.add(toParse.messages.get(k));
                                finalParsed.timeStamps.add(toParse.timeStamps.get(k));
                                break;
                            }
                        }
                    }
                }
                else if(s_type.equalsIgnoreCase(TYPE_KEYS[i]))
                    {
                        LoggerGUI.printToFrame("Parsing c o m p o u n d e d");
                        for(int j = 0; j < typeLogs.get(i).messages.size(); j++)
                        {
                            for(int k = 0; k < toParse.messages.size(); k++)
                            {
                                if(toParse.messages.get(k).equalsIgnoreCase(typeLogs.get(i).messages.get(j)) && toParse.timeStamps.get(k).equalsIgnoreCase(typeLogs.get(i).timeStamps.get(j)))
                                {
                                    finalParsed.messages.add(toParse.messages.get(k));
                                    finalParsed.timeStamps.add(toParse.timeStamps.get(k));
                                    break;
                                }
                            }
                        }
                    }
            }
            toParse = finalParsed;
        } catch (NullPointerException e) {
            LoggerGUI.printToFrame("Invalid log type, defaulting to error");
            s_type = "Error";
            toParse = subsystemLogs.get(2);
        }

        LoggerGUI.printToFrame("All messages of type: " + s_type);
        for (int i = 0; i < toParse.messages.size(); i++) {
            LoggerGUI.printToFrame(toParse.messages.get(i) + " @t = " + toParse.timeStamps.get(i));
        }
    }

    public static void logsByKeyword(String key)
    {
        LogList finalParsed = new LogList();
        LogList toParseTemp = allLogs;
        if(compounding)
        {
            toParseTemp = toParse;
        }
        for(int i = 0; i < toParseTemp.messages.size(); i++)
        {
            if(toParseTemp.messages.get(i).contains(key))
            {
                finalParsed.messages.add(toParseTemp.messages.get(i));
                finalParsed.timeStamps.add(toParseTemp.timeStamps.get(i));
            }
        }
        LoggerGUI.printToFrame("All messages containing keyword: " + key);
        for (int i = 0; i < toParseTemp.messages.size(); i++)
        {
            LoggerGUI.printToFrame(toParseTemp.messages.get(i) + " @t = " + toParseTemp.timeStamps.get(i));
        }
    }

    public static void createGraph(String type, String start, String end)
    {
        String[] types = {"Line Graph (All Messages over Time)", "Bar Graph (Message Types by Count)",
                "Pie Chart (Subsystem Messages by Count)", "Area Graph by Subsystem Messages over Time)"};

        double[] bounds = new double[2];
        try {
            bounds = new double[]{Double.valueOf(start), Double.valueOf(end)};
        }
        catch(NumberFormatException e)
        {
            LoggerGUI.printToFrame("Invalid log range entered, defaulting to all logs.");
            bounds[0] = 0;
            bounds[1] = allLogs.timeStamps.size();
        }
        ArrayList<LogList> toGraphList = null;
        switch(type)
        {
            case "Pie Chart (Subsystem Messages by Count)":
                LoggerGUI.printToFrame("Creating pie chart with subsystem logs between timestamps: " + start + " and " + end);
                toGraphList = subsystemLogs;
                break;
            case "Bar Graph (Message Types by Count)":
                LoggerGUI.printToFrame("Creating bar graph with type logs between timestamps: " + start + " and " + end);
                toGraphList = typeLogs;
                break;
            case "Line Graph (All Messages over Time)":
                LoggerGUI.printToFrame("Creating line graph with all logs between timestamps: " + start + " and " + end);
                ArrayList<LogList> tempLogs = new ArrayList<>();
                tempLogs.add(allLogs);
                toGraphList = tempLogs;
                break;
            case "Area Graph by Subsystem Messages over Time)":
                LoggerGUI.printToFrame("Creating area graph with subsystem logs between timestamps: " + start + " and " + end);
                toGraphList = subsystemLogs;
                break;
        }

        GraphManager.GraphType[] gTypes = GraphManager.GraphType.values();
        for(int i = 0; i < types.length; i++)
        {
            if(types[i].equalsIgnoreCase(type))
            {
                GraphManager.addGraph(gTypes[i], toGraphList, bounds);
                return;
            }
        }
    }

    public static void clearCompound()
    {
        LoggerGUI.printToFrame("Compounding data reset to all logs");
        toParse = allLogs;
    }

    public static void setCompounding(boolean c) {
        compounding = c;
        LoggerGUI.printToFrame("Compounding set to " + c);
    }

    public static void setFilePath(String path) {
        if(path.trim().equals("")) {
            getMostRecentFile();
        } else {
            wholePath = path;
        }   
    }
    
    public static String getWholePath() {
        return wholePath;
    }

    /**
     * An Enum that contains command names (these must be typed EXACTLY AS THEY ARE
     * into the console when prompted) and descriptions.
     */
    public enum Commands {

        preverr("Allows you to view errors preceeding one of your choice.", 2,
                "[Error to parse for <Error Name>], [Numbers of previous errors to display <int>]"),
        showseq("Outputs a list of all errors in order into a .txt file.", 0, "[No parameters <N/A>]"),
        logsinrange("Allows you to view all errors within two timestamps. COMPOUNDABLE.", 2,
                "[Start timestamp <int>], [End timestamp <int>]"),
        logsbytype("Allows you to view errors of a certain PrintStyle. COMPOUNDABLE.", 1, "[PrintStyle tolook for <Print Style>]"),
        logsbysubsystem("Allows you to view errors of a certain Subsystem. COMPOUNDABLE.", 1, "[Subsystem tolook for <Subsystem>]"),
        logsbykeyword("Allows you to view errors containing a given keyword or phrase. COMPOUNDABLE.", 1, "[Keyword to look for <Key Word>]"),
        creategraph("Creates a graph from errors and subsystem data given a graph type.", 3, "[GraphType to look for <Graph Type>], [Start of range to parse through <int>], [End of range to parse through <int>]");

        String desc;
        int paramNum;
        String paramDesc;

        private Commands(final String desc, final int params, final String paramDesc) {
            this.desc = desc;
            this.paramNum = params;
            this.paramDesc = paramDesc;
        }

        public int getParamNum() {
            return paramNum;
        }

        public String getDesc() {
            return desc;
        }

        public String getParamDesc() {
            return paramDesc;
        }
    }
}
