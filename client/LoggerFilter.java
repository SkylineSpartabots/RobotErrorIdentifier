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

    private static final String folderPath = "C:\\Users\\Public\\Documents\\FRC\\Log Files\\";
    private static final String fileName = "2019_09_10 20_42_12 Tue"; //Paste the name of the file you want to parse here

    public static void main(String[] args) {
        try {
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(folderPath + fileName + ".dsevents");
            String allText = "";
            while (fis.read(buffer) != -1) {
                String nextLine = null;
                nextLine = new String(buffer);
                allText += nextLine;
            }
            parseData(allText);
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("Failed to find file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to read file");
            e.printStackTrace();
        }
    }

    public static void parseData(String s) throws IOException {
        String alertKeyUpperBound = "!!!!!!!! Error:";
        String alertKeyLowerBound = "!!!!!!!!";
        ArrayList<String> allMessages = new ArrayList<String>();
        s = s.replaceAll(alertKeyUpperBound, "<S>");
        s = s.replaceAll(alertKeyLowerBound, "<E>");
        while (s.contains("<S>") && s.contains("<E>")) {
            int a = s.indexOf("<S>");
            int b = s.indexOf("<E>") + 3;
            String logLine = s.substring(a, b);
            s = s.replaceFirst(logLine, "");
            allMessages.add(logLine);
        }
        writeToFile(allMessages);
    }

    public static void writeToFile(ArrayList<String> list) throws IOException{
        Date currentDate = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);
        String fileName = "LOG_DATE_" + 
        currentCal.get(Calendar.YEAR) + 
        "_" + 
        currentCal.get(Calendar.MONTH) + 
        "_" + 
        currentCal.get(Calendar.DAY_OF_MONTH) +
        "_TIME_" +
        currentCal.get(Calendar.HOUR_OF_DAY) +
        "_" +
        currentCal.get(Calendar.MINUTE) +
        "_" +
        currentCal.get(Calendar.SECOND);

        String filePath = "output\\" + fileName;
        FileWriter fw = new FileWriter(filePath, false);
        PrintWriter printer = new PrintWriter(fw);
        printer.println("Robot Malfunction(s):");
        for(String s : list) {
            printer.println(s);
        }
        printer.close();
    }
}
