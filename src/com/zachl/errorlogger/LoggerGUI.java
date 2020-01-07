package com.zachl.errorlogger;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class LoggerGUI {
    public static JFrame f;
    public static JInternalFrame i, input;
    public static JPanel p;
    public static JLabel titleText;
    public static JButton cmd, hom, gen, dir, txt, b;
    public static JTextArea ta, inputta;
    private static LoggerFilter.Commands[] arrayOfCmds;
    private static ArrayList<JButton> buttons = new ArrayList<>();

    public static void main(String[] args) {
        f = new JFrame();
        setupFrame();
        f.setVisible(true);
        i = new JInternalFrame();
        setupInternalFrame();
        input = new JInternalFrame();
        printToFrame("Ready to go!!!");
        // setupListeners();
        makeButtons();
    }

    private static void setupListeners() {
        gen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoggerFilter.executeLogger();
            }
        });
    }

    public static void printToFrame(String s) {
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
        hom = new JButton("HOM");
        hom.setBounds(50, 600, 100, 50);
        cmd = new JButton("CMD");
        cmd.setBounds(310, 600, 100, 50);
        gen = new JButton("GEN");
        gen.setBounds(590, 600, 100, 50);
        dir = new JButton("DIR");
        dir.setBounds(860, 600, 100, 50);
        txt = new JButton("TXT");
        txt.setBounds(1130, 600, 100, 50);
        ta = new JTextArea(35, 100);
        JScrollPane scrollingta = new JScrollPane(ta);
        JPanel p = new JPanel();

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

    public static void setupInternalFrame() {
        i.setTitle("Commands");
        b = new JButton("Test");
        p = new JPanel();

        p.add(b);
        i.add(p);
        i.setSize(1280, 400);
        f.add(i);
        i.show();
        i.toFront();
    }

    public static void setupInputFrame() {
        input.setTitle("Inputs");
        input.setSize(360, 240);
        inputta = new JTextArea();
        inputta.setSize(360, 240);

        input.add(inputta);
    }

    public static String[] getInput() {
        Scanner sc = new Scanner(inputta.getText());
        String text = sc.nextLine();
        String[] strings = text.split(",");
        return strings;
    }

    public static void makeButtons() {
        int numOfCmnds = LoggerFilter.Commands.values().length;
        arrayOfCmds = LoggerFilter.Commands.values();
        for (int j = 0; j < numOfCmnds; j++) {
            final int finalJ = j;
            String title = String.valueOf(arrayOfCmds[j]);
            buttons.add(new JButton(title));
            buttons.get(j).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch (arrayOfCmds[finalJ]) {
                        case preverr:
                            input.show();
                            LoggerFilter.prevErrors(getInput()[0], getInput()[1]);
                            input.hide();
                            break;
                        case showseq:
                            break;
                        case logsinrange:
                            break;
                    }
                }
            });
            i.add(buttons.get(j));
        }

    }
}
