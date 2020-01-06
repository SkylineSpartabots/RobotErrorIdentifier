package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.swing.*;
import java.awt.event.*;

import client.gui.*;

public class LoggerGUI {
    public static JFrame f;
    public static NamedJButton hom, cmd, gen, dir, txt;
    public static NamedJButton b;
    /**
     * TODO: Make an array of NamedJButtons that we can call .addActionListener on,
     * and if pressed, call an openInput() method that akes in an integer parameter
     * that programatically generates input fields and returns an ArrayList of those
     * values that can be passed into LoggerFilter methods
     */
    public static JTextArea ta;
    public static JLabel titleText;
    public static JScrollPane scrollingta;
    public static JPanel commandPanel;
    public static JInternalFrame commandiFrame;

    public static void main(final String[] args) {
        f = new JFrame();
        setupFrame();
        setupInternalFrame();
        f.setVisible(true);
        printToFrame("Robot Error Identifier Status: Ready");
        setupListeners();
    }

    private static void setupListeners() {
        gen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LoggerFilter.executeLogger();
            }
        });

        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ta.setVisible(false);
                scrollingta.setVisible(false);
                titleText.setVisible(false);
                cmd.setVisible(false);
                gen.setVisible(false);
                dir.setVisible(false);
                txt.setVisible(false);
                setupInternalFrame();
                commandiFrame.show();
            }
        });

        hom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ta.setVisible(true);
                scrollingta.setVisible(true);
                titleText.setVisible(true);
                cmd.setVisible(true);
                gen.setVisible(true);
                dir.setVisible(true);
                txt.setVisible(true);
                commandiFrame.hide();
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
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        titleText = new JLabel();
        titleText.setBounds(25, 10, 50, 50);
        titleText.setText("Output:");
        hom = new NamedJButton("Home Button", "HOME");
        hom.setBounds(25, 600, 150, 50);
        cmd = new NamedJButton("Command Button", "COMMANDS");
        cmd.setBounds(285, 600, 150, 50);
        gen = new NamedJButton("Generate Button", "GENERATE");
        gen.setBounds(565, 600, 150, 50);
        dir = new NamedJButton("Directory Button", "DIRECTORY");
        dir.setBounds(835, 600, 150, 50);
        txt = new NamedJButton("Save Button", "SAVE");
        txt.setBounds(1105, 600, 150, 50);
        ta = new JTextArea(35, 100);
        scrollingta = new JScrollPane(ta);
        final JPanel p = new JPanel();

        p.add(scrollingta);
        f.add(hom);
        f.add(cmd);
        f.add(gen);
        f.add(dir);
        f.add(txt);
        f.add(titleText);
        f.add(p);
        f.setVisible(true);
    }

    private static void setupInternalFrame() {
        commandiFrame = new JInternalFrame();
        commandiFrame.setTitle("Commands");
        commandPanel = new JPanel();
        b = new NamedJButton("preverr", "Previous Error");
        commandPanel.add(b);
        commandiFrame.add(commandPanel);
        commandiFrame.setSize(1280, 400);
        f.add(commandiFrame);
    }
}