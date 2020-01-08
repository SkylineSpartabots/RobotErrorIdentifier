package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import client.gui.*;

/**
 * GUI for LoggerFilter.java. (RUN THIS FILE).
 */
public class LoggerGUI {
    public static JFrame f, inputf = new JFrame();
    public static NamedJButton qui, cmd, gen, dir, txt;
    public static NamedJButton b, b2;
    public static JTextArea ta, inputta;
    public static JLabel titleText;
    public static JScrollPane scrollingta;
    public static JPanel commandPanel;
    public static JInternalFrame input;
    private static LoggerFilter.Commands[] arrayOfCmds;
    private static ArrayList<JButton> buttons = new ArrayList<>();

    public static void main(final String[] args) {
        f = new JFrame();
        setupFrame();
        f.setVisible(true);
        printToFrame("Robot Error Identifier Status: Ready");
        setupListeners();
    }

    private static void setupListeners() {
        gen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LoggerFilter.executeLogger();
                cmd.setEnabled(true);
                txt.setEnabled(true);
            }
        });

        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                makeButtons();
            }
        });

        qui.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                f.dispose();
            }
        });

        txt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Calendar c = Calendar.getInstance();
                final String filePath = "output\\savedfiles\\";
                final String fileName = LoggerFilter.fileName + " SAVED_INFO_" + c.get(Calendar.HOUR_OF_DAY) + "_"
                        + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND);
                FileWriter fw;
                try {
                    fw = new FileWriter(filePath + fileName, false);
                    final PrintWriter printer = new PrintWriter(fw);
                    printer.println("Saved info:");
                    printer.println(ta.getText());
                    printer.close();
                } catch (final IOException e1) {
                    printToFrame("Failed to save file.");
                    e1.printStackTrace();
                }
                printToFrame("Saved current console text.");
            }
        });
    }

    public static void printToFrame(final String s) {
        ta.append(s + "\n");
    }

    private static void setupFrame() {
        f.setSize(new Dimension(1280, 720));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setTitle("Robot Error Identifier");
        f.setLayout(new BorderLayout());

        titleText = new JLabel();
        titleText.setBounds(25, 10, 50, 50);
        titleText.setText("Output:");
        qui = new NamedJButton("Quit Button", "QUIT");
        qui.setBounds(25, 600, 150, 50);
        qui.setToolTipText("Quits the program.");
        cmd = new NamedJButton("Command Button", "COMMANDS");
        cmd.setBounds(285, 600, 150, 50);
        cmd.setEnabled(false);
        cmd.setToolTipText("Opens a list of commands for filtering.");
        gen = new NamedJButton("Generate Button", "GENERATE");
        gen.setBounds(565, 600, 150, 50);
        gen.setToolTipText("Parses file and generates basic output. Must be pressed first before COMMANDS or SAVE.");
        dir = new NamedJButton("Directory Button", "DIRECTORY");
        dir.setBounds(835, 600, 150, 50);
        dir.setToolTipText("N/A yet.");
        txt = new NamedJButton("Save Button", "SAVE");
        txt.setBounds(1105, 600, 150, 50);
        txt.setEnabled(false);
        txt.setToolTipText("Saves current console view into a .txt file.");
        ta = new JTextArea(35, 100);
        scrollingta = new JScrollPane(ta);
        final JPanel p = new JPanel();

        p.add(scrollingta);
        f.add(qui);
        f.add(cmd);
        f.add(gen);
        f.add(dir);
        f.add(txt);
        f.add(titleText);
        f.add(p);
        f.setVisible(true);
    }

    public static void openInput(final LoggerFilter.Commands c) {
        inputf = new JFrame();
        inputf.setSize(new Dimension(600, 400));
        inputf.setLocationRelativeTo(null);
        inputf.setResizable(false);
        inputf.setTitle("Input Panel");
        inputf.setLayout(new BorderLayout());
        final NamedJButton sub = new NamedJButton("Submit Button", "SUBMIT");
        sub.setBounds(225, 300, 150, 50);
        ArrayList<String> parsedDesc = parseDesc(c.getParamDesc());
        final JPanel p = new JPanel(new FlowLayout());
        ArrayList<JComboBox<Object>> allDrops = new ArrayList<>();
        ArrayList<JTextArea> allInputField = new ArrayList<>();
        int counter = 0;
        for (String s : parsedDesc) {
            switch (s) {
            case "Error Name":
                JComboBox<Object> jcbe = createDropdown(counter, LoggerFilter.getErrors());
                allDrops.add(jcbe);
                inputf.add(jcbe);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Print Style":
                JComboBox<Object> jcbp = createDropdown(counter, LoggerFilter.TYPE_KEYS);
                allDrops.add(jcbp);
                inputf.add(jcbp);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "int":
                JTextArea jta = createtField(counter);
                JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jsp.setBounds(275, 50 + (counter * 70), 50, 20);
                allInputField.add(jta);
                inputf.add(jsp);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "N/A":
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            default:
                printToFrame("Error with input panel generation");
            }
            counter++;
        }
        inputf.add(sub);
        inputf.add(p);
        inputf.setVisible(true);

        sub.addActionListener(new ActionListener() {
            ArrayList<String> input = new ArrayList<>();

            @Override
            public void actionPerformed(final ActionEvent e) {
                for (JComboBox<Object> j : allDrops) {
                    input.add(getInput(j));
                }
                for (JTextArea j : allInputField) {
                    input.add(getInput(j));
                }
                inputSwitch(input, c);
                inputf.dispose();
            }
        });
    }

    public static JComboBox<Object> createDropdown(int orderNum, String[] options) {
        JComboBox<Object> jcb = new JComboBox<>(options);
        jcb.setBounds(100, 50 + (orderNum * 70), 400, 20);
        return jcb;
    }

    public static JTextArea createtField(int orderNum) {
        JTextArea jta = new JTextArea();
        jta.setSize(5, 5);
        return jta;
    }

    public static JLabel createLabel(int orderNum, String desc) {
        String[] labelToAdd = desc.split("\\,");
        JLabel addLabel = new JLabel(labelToAdd[orderNum], SwingConstants.CENTER);
        addLabel.setBounds(0, 30 + (orderNum * 70), 600, 20);
        return addLabel;
    }

    public static ArrayList<String> parseDesc(final String s) {
        ArrayList<String> myList = new ArrayList<>();
        String description = s;
        while (description.contains("<") && description.contains(">")) {
            String inputType = description.substring(description.indexOf("<"), description.indexOf(">") + 1);
            description = description.replaceFirst(inputType, "");
            inputType = inputType.replaceAll("\\<", "");
            inputType = inputType.replaceAll("\\>", "");
            myList.add(inputType);
        }
        return myList;
    }

    public static String getInput(JTextArea textArea) {
        if (!textArea.getText().equals("")) {
            Scanner myScanner = new Scanner(textArea.getText());
            String reply = myScanner.nextLine();
            reply.trim();
            myScanner.close();
            return reply;
        } else {
            return "";
        }
    }

    public static String getInput(JComboBox<Object> dropDown) {
        String input = dropDown.getSelectedItem().toString();
        input.trim();
        return input;
    }

    public static void inputSwitch(final ArrayList<String> input, final LoggerFilter.Commands c) {
        switch (c) {
        case preverr:
            LoggerFilter.prevErrors(input.get(0), input.get(1));
            break;
        case showseq:
            LoggerFilter.showSeq();
            break;
        case logsinrange:
            LoggerFilter.logsInRange(input.get(0), input.get(1));
            break;
        case logsbytype:
            LoggerFilter.logsByType(input.get(0));
            break;
        }
        printToFrame("Command Complete.");
        printToFrame("--------------------------------------------------");
    }

    public static void makeButtons() {
        final JFrame tempJ = new JFrame();
        final int numOfCmnds = LoggerFilter.Commands.values().length;
        arrayOfCmds = LoggerFilter.Commands.values();
        for (int j = 0; j < numOfCmnds; j++) {
            final int finalJ = j;
            final String title = String.valueOf(arrayOfCmds[j]);
            buttons.add(new JButton(title));
            tempJ.setSize(new Dimension(950, 300 + (150 * (numOfCmnds / 5))));
            tempJ.setLocationRelativeTo(null);
            tempJ.setTitle("Command Panel");
            tempJ.setLayout(new BorderLayout());
            buttons.get(j).setBounds(40 + (j % 5 * 175), ((j / 5) * 75) + 75, 150, 50);
            buttons.get(j).setToolTipText(
                    arrayOfCmds[j].getDesc() + " Takes in " + arrayOfCmds[j].getParamNum() + " parameters.");
            buttons.get(j).setEnabled(true);
            tempJ.add(buttons.get(j));
            titleText = new JLabel();
            titleText.setBounds(25, 10, 150, 50);
            titleText.setText("Command List:");
            tempJ.add(titleText);
            if (buttons.get(j).getActionListeners().length < 1) {
                buttons.get(j).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        openInput(arrayOfCmds[finalJ]);
                    }
                });
            }
        }
        final JButton homeButton = new JButton("HOME");
        homeButton.setBounds(40, 150 + (75 * (numOfCmnds / 5)), 150, 50);
        homeButton.setToolTipText("Takes you back to the home screen.");
        homeButton.setEnabled(true);
        tempJ.add(homeButton);
        final JPanel jp = new JPanel();
        tempJ.add(jp);
        tempJ.setVisible(true);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tempJ.dispose();
            }
        });
    }
}