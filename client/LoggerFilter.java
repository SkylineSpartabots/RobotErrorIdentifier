package client;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LoggerFilter {
    //TODO: N/A for now
    private static final String folderPath = "C:\\Users\\Public\\Documents\\FRC\\Log Files\\"; //Location of all .dsevents files
    private static final String fileName = ""; //Paste the name of the file you want to parse here

    public static void main(final String[] args) {
        try {
            final byte[] buffer = new byte[1024];
            final FileInputStream fis = new FileInputStream(folderPath + fileName + ".dsevents");
            String allText = "";
            while (fis.read(buffer) != -1) {
                String nextLine = null;
                nextLine = new String(buffer);
                allText += nextLine;
            }
            parseData(allText);
            fis.close();
        } catch (final FileNotFoundException e) {
            System.out.println("Failed to find file");
            e.printStackTrace();
        } catch (final IOException e) {
            System.out.println("Failed to read file");
            e.printStackTrace();
        }
    }

    public static void parseData(String s) throws IOException {
        final String alertKeyUpperBound = "!!!!!!!! Error:";
        final String alertKeyLowerBound = "!!!!!!!!";
        final ArrayList<String> allMessages = new ArrayList<String>();
        s = s.replaceAll(alertKeyUpperBound, "<S>");
        s = s.replaceAll(alertKeyLowerBound, "<E>");
        while (s.contains("<S>") && s.contains("<E>")) {
            final int a = s.indexOf("<S>");
            final int b = s.indexOf("<E>") + 3;
            final String logLine = s.substring(a, b);
            s = s.replaceFirst(logLine, "");
            allMessages.add(logLine);
        }
        writeToFile(allMessages);
    }

    public static void writeToFile(final ArrayList<String> list) throws IOException {
        final Date currentDate = new Date();
        final Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);
        final String fileName = "LOG_DATE_" + currentCal.get(Calendar.YEAR) + "_" + currentCal.get(Calendar.MONTH) + "_"
                + currentCal.get(Calendar.DAY_OF_MONTH) + "_TIME_" + currentCal.get(Calendar.HOUR_OF_DAY) + "_"
                + currentCal.get(Calendar.MINUTE) + "_" + currentCal.get(Calendar.SECOND);

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
