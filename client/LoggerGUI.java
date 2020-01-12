package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import java.awt.event.*;
import java.awt.*;

/**
 * GUI for LoggerFilter.java. Creates a java swing JFrame that contaisn
 * everything you will ever need for robot error parsing!
 */
public class LoggerGUI {
    /**
     * JFrames that will be displayed for the homescreen and for input.
     */
    public static JFrame f, inputf = new JFrame();
    /**
     * NamedJButtons to navigate around the GUI that will be on the homescreen.
     */
    public static NamedJButton qui, cmd, gen, dir, txt;
    /**
     * JTextArea for the main output console.
     */
    public static JTextArea ta;
    /**
     * JScrollPane for the main output console JTextArea.
     */
    public static JScrollPane scrollingta;
    /**
     * JLabel that is reused for titles.
     */
    public static JLabel titleText;
    /**
     * An array of commands generated from the "Commands" enum in LoggerFilter.
     */
    private static LoggerFilter.Commands[] arrayOfCmds;
    /**
     * An array of command buttons. Can be expanded to fit more buttons.
     */
    private static ArrayList<JButton> buttons = new ArrayList<>();

    public static void main(final String[] args) {
        makeDirs();
        LoggerFilter.getConfig();
        setLookAndFeel();
        f = new JFrame();
        setupFrame();
        f.setVisible(true);
        printToFrame("Robot Error Identifier (and other fun cheerios) made with love by Team 2976, The Spartabots!");
        printToFrame("Status: Ready");
        if (LoggerFilter.fileName.equals("")) {
            LoggerFilter.getMostRecentFile();
        }
        printToFrame("File to scan: " + LoggerFilter.getWholePath());
        setupListeners();
    }

    /**
     * Makes output directories if they don't exist.
     */
    private static void makeDirs() {
        final File[] outputFolder = { new File("output"), new File("output\\commandoutput"),
                new File("output\\mainoutput"), new File("output\\savedfiles") };
        for (final File f : outputFolder) {
            if (!f.exists()) {
                f.mkdir();
            }
        }
    }

    /**
     * Sets the look and feel of the java swing panel. This changes all buttons and
     * UI elements and may mess up proportions. Edit with caution.
     */
    public static void setLookAndFeel() {
        try {
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up button listeners on the homescreen.
     */
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
                printToFrame(
                        "Saved current console text to: " + new File(filePath + fileName).getAbsolutePath() + ".txt");
            }
        });
        dir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFrame tempJ = new JFrame();
                tempJ.setSize(new Dimension(300, 300));
                tempJ.setLocationRelativeTo(null);
                tempJ.setTitle("Directory Panel");
                tempJ.setLayout(new BorderLayout());
                tempJ.setResizable(false);
                final NamedJButton chg = new NamedJButton("Submit Button", "CHANGE");
                chg.setBounds(75, 150, 150, 50);
                final JLabel jlb = new JLabel("File to parse (full filepath):", SwingConstants.CENTER);
                jlb.setBounds(0, 0, 300, 50);
                final JTextArea jta = new JTextArea(1, 5);
                final JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                jsp.setBounds(50, 40, 200, 35);
                tempJ.add(chg);
                tempJ.add(jsp);
                tempJ.add(jlb);
                final JPanel p = new JPanel();
                tempJ.add(p);
                tempJ.setVisible(true);
                if (chg.getActionListeners().length < 1) {
                    chg.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            LoggerFilter.setFilePath(jta.getText().trim().replaceAll("\\\\", "\\\\\\\\"));
                            if (jta.getText().trim().equals("")) {
                                printToFrame("Got the most recent file.");
                                printToFrame("Set file to parse to: " + LoggerFilter.getWholePath());
                            } else {
                                printToFrame("Set file to parse to: " + jta.getText().trim());
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Strings passed into this command are printed into the GUI output screen.
     * Great for debugging and for user-viewable output.
     * 
     * @param s -> The string to print to the GUI screen.
     */
    public static void printToFrame(final String s) {
        ta.append(s + "\n");
    }

    /**
     * Sets up the main frame and all of its buttons.
     */
    private static void setupFrame() {
        f.setSize(new Dimension(1280, 720));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setTitle("Robot Error Identifier");
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
        dir.setToolTipText("Allows you to pick the file you want to parse.");
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

    /**
     * Parses the description of the input parameters of a certain command (which
     * can be found as the third parameter in the "Commands" enum, and puts the
     * important elements from that String into an array. This array is later used
     * to create specific inputboxes for unique commands.
     * 
     * @param s -> The String parameter from the specific value from the "Commands"
     *          enum.
     */
    public static ArrayList<String> parseDesc(final String s) {
        final ArrayList<String> myList = new ArrayList<>();
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

    /**
     * If the "COMMANDS" button is pressed, this method generates an
     * infinitely-expanding programatically generated sequences of command buttons
     * that are based off of the "Commands" enum in LoggerFilter.
     */
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
        final JButton compoundButton = new JButton("COMPOUNDING: OFF");
        compoundButton.setBounds(215, 150 + (75 * (numOfCmnds / 5)), 150, 50);
        compoundButton.setToolTipText("Enables and disables compounding.");
        compoundButton.setEnabled(true);
        tempJ.add(homeButton);
        tempJ.add(compoundButton);
        final JPanel jp = new JPanel();
        tempJ.add(jp);
        tempJ.setVisible(true);
        if (homeButton.getActionListeners().length < 1) {
            homeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    tempJ.dispose();
                }
            });
        }
        if (compoundButton.getActionListeners().length < 1) {
            compoundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (compoundButton.getText().equals("COMPOUNDING: OFF")) {
                        LoggerFilter.setCompunding(true);
                        compoundButton.setText("COMPOUNDING: ON");
                    } else if (compoundButton.getText().equals("COMPOUNDING: ON")) {
                        LoggerFilter.setCompunding(false);
                        compoundButton.setText("COMPOUNDING: OFF");
                    }
                }
            });
        }
    }

    /**
     * If a command button is pressed, this method is called. It will parse through
     * certain variables stored wihin Strings in the "Commands" enum and generates
     * input boxes accordingly. If the "submit" button is pressed, then the input is
     * passed into the "inputSwitch" method.
     * 
     * @param c -> Element of the "Commands" enum that relates to the button
     *          pressed.
     */
    public static void openInput(final LoggerFilter.Commands c) {
        inputf = new JFrame();
        inputf.setSize(new Dimension(600, 400));
        inputf.setLocationRelativeTo(null);
        inputf.setResizable(false);
        inputf.setTitle("Input Panel");
        inputf.setLayout(new BorderLayout());
        final NamedJButton sub = new NamedJButton("Submit Button", "SUBMIT");
        sub.setBounds(225, 300, 150, 50);
        final ArrayList<String> parsedDesc = parseDesc(c.getParamDesc());
        final JPanel p = new JPanel(new FlowLayout());
        final ArrayList<JComboBox<Object>> allDrops = new ArrayList<>();
        final ArrayList<JTextArea> allInputField = new ArrayList<>();
        final Action submit = new AbstractAction("SUBMIT") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                final ArrayList<String> input = new ArrayList<>();
                for (final JComboBox<Object> j : allDrops) {
                    input.add(getInput(j));
                }
                for (final JTextArea j : allInputField) {
                    input.add(getInput(j));
                }
                inputSwitch(input, c);
                inputf.dispose();
            }
        };
        int counter = 0;
        for (final String s : parsedDesc) {
            switch (s) {
            case "Error Name":
                final JComboBox<Object> jcbe = createDropdown(counter, LoggerFilter.getErrors());
                allDrops.add(jcbe);
                inputf.add(jcbe);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Print Style":
                final JComboBox<Object> jcbp = createDropdown(counter, LoggerFilter.TYPE_KEYS);
                allDrops.add(jcbp);
                inputf.add(jcbp);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Subsystem Name":
                final JComboBox<Object> jcbs = createDropdown(counter, LoggerFilter.SUBSYSTEM_KEYS);
                allDrops.add(jcbs);
                inputf.add(jcbs);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "String":
                final JTextArea jtas = createtField(counter);
                final JScrollPane jsps = new JScrollPane(jtas, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jsps.setBounds(225, 50 + (counter * 70), 150, 20);
                jtas.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), submit);
                allInputField.add(jtas);
                inputf.add(jsps);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "int":
                final JTextArea jtai = createtField(counter);
                final JScrollPane jspi = new JScrollPane(jtai, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jspi.setBounds(275, 50 + (counter * 70), 50, 20);
                jtai.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), submit);
                allInputField.add(jtai);
                inputf.add(jspi);
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
                for (final JComboBox<Object> j : allDrops) {
                    input.add(getInput(j));
                }
                for (final JTextArea j : allInputField) {
                    input.add(getInput(j));
                }
                inputSwitch(input, c);
                inputf.dispose();
            }
        });

    }

    /**
     * Creates a dropdown meant for the programatic input panel and returns it.
     * 
     * @param orderNum -> Where (vertically) this inputbox should go.
     * @param options  -> The options for the dropdown.
     * @return The dropdown (JComboBox<Object>) needed for the inputpanel.
     */
    public static JComboBox<Object> createDropdown(final int orderNum, final String[] options) {
        final JComboBox<Object> jcb = new JComboBox<>(options);
        jcb.setBounds(100, 50 + (orderNum * 70), 400, 20);
        return jcb;
    }

    /**
     * Creates a text area meant for the programatic input panel and returns it.
     * 
     * @param orderNum -> Where (vertically) this inputbox should go.
     * @return The text area (JTextArea) needed for the inputpanel.
     */
    public static JTextArea createtField(final int orderNum) {
        final JTextArea jta = new JTextArea();
        jta.setSize(5, 5);
        return jta;
    }

    /**
     * Creates a descriptor JLabel for the programatic inputboxes and returns it.
     * 
     * @param orderNum -> Where (vertically) this label should go.
     * @param desc     -> The description, gotten from a String paramter within the
     *                 "Commands" enum that should be applied to the JLabel.
     * @return The JLabel appropriate for a certain inputbox.
     */
    public static JLabel createLabel(final int orderNum, final String desc) {
        final String[] labelToAdd = desc.split("\\,");
        final JLabel addLabel = new JLabel(labelToAdd[orderNum], SwingConstants.CENTER);
        addLabel.setBounds(0, 30 + (orderNum * 70), 600, 20);
        return addLabel;
    }

    /**
     * Gets the input from a JComboBox<Object> (dropdown menu) and returns it as a
     * String.
     * 
     * @param dropDown -> The JComboBox<Object> to read.
     * @return The content from the JComboBox<Object>.
     */
    public static String getInput(final JComboBox<Object> dropDown) {
        final String input = dropDown.getSelectedItem().toString();
        input.trim();
        return input;
    }

    /**
     * Gets the input from a JTextArea and returns it as a String.
     * 
     * @param textArea -> The textarea to read.
     * @return The content from the JTextArea.
     */
    public static String getInput(final JTextArea textArea) {
        if (!textArea.getText().equals("")) {
            final Scanner myScanner = new Scanner(textArea.getText());
            final String reply = myScanner.nextLine();
            reply.trim();
            myScanner.close();
            return reply;
        } else {
            return "";
        }
    }

    /**
     * Input from programatically generated input boxes as well as the type of
     * command is passed into here, and commands are sent out to LoggerFIlter
     * accordingly.
     * 
     * @param input -> The inputarray to parse through.
     * @param c     -> The type of command.
     */
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
        case logsbysubsystem:
            LoggerFilter.logsBySubsystem(input.get(0));
            break;
        case logsbykeyword:
            LoggerFilter.logsByKeyword(input.get(0));
            break;
        }
        printToFrame("Command Complete.");
        printToFrame("--------------------------------------------------");
    }

    /**
     * An inner class that allows for the generation of unique JButtons with an id
     * parameter.
     */
    public static class NamedJButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final String id;

        public NamedJButton(final String id, final String name) {
            super(name);
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}