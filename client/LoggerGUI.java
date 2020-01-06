package client;

import java.awt.Dimension;

import javax.swing.*;

public class LoggerGUI {
    public static JFrame f;
    public static JButton cmd, hom, gen, dir, txt;
    public static JTextArea ta;

    public static void main(String[] args) {
        f = new JFrame();
        setupFrame();
        f.setVisible(true);
        printToFrame("Ready to go!!!");
        LoggerFilter.executeLogger();
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

        JLabel titleText = new JLabel();
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
}