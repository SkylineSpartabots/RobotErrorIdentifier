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

        inputta = new JTextArea(1, 25);
        final JScrollPane inputLine = new JScrollPane(inputta);

        final JPanel p = new JPanel();
        p.add(inputLine);

        titleText = new JLabel("<html><div style='text-align: center;'>" + "Input parameters: \n" + c.getParamDesc()
                + "</div></html>");
        titleText.setBounds(150, 50, 300, 100);

        inputf.add(sub);
        inputf.add(titleText);
        inputf.add(p);
        inputf.setVisible(true);

        sub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String[] input = getInput();
                inputSwitch(input, c);
                inputf.dispose();
            }
        });
    }

    public static String[] getInput() {
        if (!inputta.getText().equals("")) {
            final Scanner sc = new Scanner(inputta.getText());
            final String text = sc.nextLine();
            text.replaceAll("\\[", "");
            text.replaceAll("\\]", "");
            final String[] strings = text.split(",");
            for (int i = 0; i < strings.length; i++) {
                strings[i] = strings[i].trim();
            }
            sc.close();
            return strings;
        } else {
            return new String[0];
        }
    }

    public static void inputSwitch(final String[] input, final LoggerFilter.Commands c) {
        switch (c) {
        case preverr:
            LoggerFilter.prevErrors(input[0], input[1]);
            break;
        case showseq:
            LoggerFilter.showSeq();
            break;
        case logsinrange:
            LoggerFilter.logsInRange(input[0], input[1]);
            break;
        }
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