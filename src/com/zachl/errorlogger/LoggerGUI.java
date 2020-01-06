package com.zachl.errorlogger;

import gui.NamedJButton;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.*;
import java.awt.event.*;


public class LoggerGUI {
    public static JFrame f;
    public static NamedJButton hom, cmd, gen, dir, txt;
    public static NamedJButton b;
    public static JTextArea ta;
    public static JLabel titleText;
    public static JScrollPane scrollingta;
    public static JPanel commandPanel;
    public static JInternalFrame commandiFrame;

    public static void main(String[] args) {
        f = new JFrame();
        setupFrame();
        setupInternalFrame();
        f.setVisible(true);
        printToFrame("Ready to go!!!");
        setupListeners();
    }

    private static void setupListeners() {
        gen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoggerFilter.executeLogger();
			}
        });
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            public void actionPerformed(ActionEvent e) {
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
        hom = new NamedJButton("Home Button", "HOM");
        hom.setBounds(50, 600, 100, 50);
        cmd = new NamedJButton("Command Button", "CMD");
        cmd.setBounds(310, 600, 100, 50);
        gen = new NamedJButton("Generate Button", "GEN");
        gen.setBounds(590, 600, 100, 50);
        dir = new NamedJButton("Directory Button", "DIR");
        dir.setBounds(860, 600, 100, 50);
        txt = new NamedJButton("Text Button", "TXT");
        txt.setBounds(1130, 600, 100, 50);
        ta = new JTextArea(35, 100);
        scrollingta = new JScrollPane(ta);
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
}