package com.zachl.errorlogger;

import java.awt.Dimension;

import javax.swing.*;

public class LoggerGUI {
    public static void main(String[] args) {
        JFrame f = new JFrame();
        setupFrame(f);   
        JTextArea textArea = new JTextArea(5,40);
        f.setVisible(true);
        //LoggerFilter.executeLogger();
    }
    private static void setupFrame(JFrame f) {
        f.setSize(new Dimension(1280, 720));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        JButton cmd = new JButton("CMD");
        //f.add(cmd);
    }
}