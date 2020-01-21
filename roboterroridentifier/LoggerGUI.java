package roboterroridentifier;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import roboterroridentifier.LoggerFilter.LogList;

/**
 * GUI for LoggerFilter.java. Creates a java swing JFrame that contains
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

    /**
     * Colors to be used in the GUI.
     */
    public static Color spartaGreen = new Color(51, 90, 64);
    public static Color lightGreen = new Color(255, 255, 255);
    public static Color textAreaGreen = new Color(162, 180, 168);

    /**
     * Executes the GUI!
     */
    public static void executeGUI() {
        makeDirs();
        LoggerFilter.getConfig();
        setLookAndFeel();
        f = new JFrame();
        setupFrame();
        final Color c = Color.black;
        f.setBackground(c);
        f.setVisible(true);
        printToFrame("Robot Error Identifier (and other fun cheerios) made with love by Team 2976, The Spartabots!");
        printToFrame("Hotkeys: CTRL + {Q, C, G, D, S} for the buttons below.");
        if (LoggerFilter.fileName.equals("")) {
            LoggerFilter.getMostRecentFile();
        }
        setupListeners();
        printToFrame("File to scan: " + LoggerFilter.getWholePath());
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
     * A method that takes in a text area and adds a keybound commands to it that
     * trigger buttons with CTRL + a key defined in the NamedJButton class.
     * 
     * @param ta         -> Text area to add actions bound to keymaps to.
     * @param allButtons -> ArrayList<NamedJButton> of all NamedJButtons that should
     *                   have hotkeys assigned to them.
     */
    private static void adaptiveListener(final JTextArea ta, final ArrayList<NamedJButton> allButtons) {
        for (int i = 0; i < allButtons.size(); i++) {
            final NamedJButton jb = allButtons.get(i);
            final Action a = new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    jb.doClick();
                }
            };
            ta.getInputMap().put(KeyStroke.getKeyStroke(allButtons.get(i).getHotkey()), a);
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
                final JButton chg = new JButton("CHANGE");
                chg.setBounds(75, 150, 150, 50);
                final JLabel jlb = new JLabel("File to parse (full filepath):", SwingConstants.CENTER);
                jlb.setBounds(0, 0, 300, 50);
                final JTextArea jta = new JTextArea(1, 5);
                final JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                jsp.setBounds(50, 40, 200, 35);
                chg.setBackground(spartaGreen);
                chg.setForeground(lightGreen);
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
                                gen.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }

    public static ArrayList<String> messages = new ArrayList<>();
    public static int numOfLinesAllowed;

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
     * Strings passed into this command are sent into an ArrayList<String> of
     * messages. outputAccordingly() should be called outside of the iterative for
     * loop in which this overloaded version of printToFrame() is called.
     * 
     * @param s          -> The string to print to either the GUI screen or an
     *                   external file.
     * @param isAdaptive -> Should this be checked for line length?
     */
    public static void printToFrame(final String s, final boolean isAdaptive) {
        messages.add(s);
    }

    /**
     * Reads in all the messages in the ArrayList<String> messages and sees how many
     * lines there are in it. If there are more lines than Console Overflow Limit
     * (found in "config.txt") allows for, the output will go to an external file
     * with a timestamp.
     */
    public static void outputAccordingly() {
        printToFrame("");
        numOfLinesAllowed = LoggerFilter.overflowLineMax;
        final String filePath = "output\\commandoutput\\";
        final Calendar c = Calendar.getInstance();
        final String fileName = "LARGE_OUTPUT_" + c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_"
                + c.get(Calendar.SECOND);
        if (messages.size() != 0) {
            final int i = messages.size();

            if (i <= numOfLinesAllowed) {
                for (int j = 0; j < messages.size(); j++) {
                    ta.append(messages.get(j) + "\n");
                }
            } else {
                printToFrame(
                        "Output too large to display in console window. Outputted lines to " + filePath + fileName);
                FileWriter fw = null;
                try {
                    fw = new FileWriter(filePath + fileName, false);
                } catch (final IOException e) {
                    System.out.println("Failed to find large output file.");
                }
                final PrintWriter printer = new PrintWriter(fw);
                for (int j = 0; j < messages.size(); j++) {
                    printer.println(messages.get(j));
                }
                printer.close();
            }
            messages.clear();
        }
        printToFrame("");
    }

    /**
     * Sets up the main frame and all of its buttons.
     */
    private static void setupFrame() {
        final ArrayList<NamedJButton> mainButtons = new ArrayList<>();
        f.setSize(new Dimension(1280, 720));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setTitle("Robot Error Identifier");
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        titleText = new JLabel();
        titleText.setBounds(25, 10, 50, 50);
        titleText.setText("Output:");
        titleText.setFont(new Font(Font.DIALOG, Font.BOLD, titleText.getFont().getSize()));

        qui = new NamedJButton("Quit Button", "QUIT", "control Q");
        qui.setBounds(25, 600, 150, 50);
        qui.setToolTipText("Quits the program.");
        qui.setBackground(spartaGreen);
        qui.setForeground(lightGreen);
        qui.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));

        cmd = new NamedJButton("Command Button", "COMMANDS", "control C");
        cmd.setBounds(285, 600, 150, 50);
        cmd.setEnabled(false);
        cmd.setToolTipText("Opens a list of commands for filtering.");
        cmd.setBackground(spartaGreen);
        cmd.setForeground(lightGreen);
        cmd.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));

        gen = new NamedJButton("Generate Button", "GENERATE", "control G");
        gen.setBounds(565, 600, 150, 50);
        gen.setToolTipText("Parses file and generates basic output. Must be pressed first before COMMANDS or SAVE.");
        gen.setBackground(spartaGreen);
        gen.setForeground(lightGreen);
        gen.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));

        dir = new NamedJButton("Directory Button", "DIRECTORY", "control D");
        dir.setBounds(835, 600, 150, 50);
        dir.setToolTipText("Allows you to pick the file you want to parse.");
        dir.setBackground(spartaGreen);
        dir.setForeground(lightGreen);
        dir.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));

        txt = new NamedJButton("Save Button", "SAVE", "control S");
        txt.setBounds(1105, 600, 150, 50);
        txt.setEnabled(false);
        txt.setToolTipText("Saves current console view into a .txt file.");
        txt.setBackground(spartaGreen);
        txt.setForeground(lightGreen);
        txt.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));

        ta = new JTextArea(35, 100);
        scrollingta = new JScrollPane(ta);
        final JPanel p = new JPanel();
        ta.setBackground(textAreaGreen);

        mainButtons.add(qui);
        mainButtons.add(cmd);
        mainButtons.add(gen);
        mainButtons.add(dir);
        mainButtons.add(txt);

        adaptiveListener(ta, mainButtons);

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
     * infinitely-expanding programmatically generated sequences of command buttons
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
            buttons.get(j).setBackground(spartaGreen);
            buttons.get(j).setForeground(lightGreen);
            buttons.get(j).setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));
            tempJ.add(buttons.get(j));
            titleText = new JLabel();
            titleText.setBounds(25, 10, 150, 50);
            titleText.setText("Command List:");
            tempJ.add(titleText);
            if (buttons.get(j).getActionListeners().length < 1) {
                buttons.get(j).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        openInput(arrayOfCmds[finalJ], "ENTER");
                    }
                });
            }
        }
        final JButton homeButton = new JButton("HOME");
        homeButton.setBounds(40, 150 + (75 * (numOfCmnds / 5)), 150, 50);
        homeButton.setToolTipText("Takes you back to the home screen.");
        homeButton.setEnabled(true);
        homeButton.setBackground(spartaGreen);
        homeButton.setForeground(lightGreen);
        homeButton.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));
        final JButton compoundButton = new JButton("COMPOUNDING: OFF");
        compoundButton.setBounds(215, 150 + (75 * (numOfCmnds / 5)), 200, 50);
        compoundButton.setToolTipText("Enables and disables compounding.");
        compoundButton.setEnabled(true);
        compoundButton.setBackground(spartaGreen);
        compoundButton.setForeground(lightGreen);
        compoundButton.setFont(new Font(Font.DIALOG, Font.PLAIN, qui.getFont().getSize()));
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
                        LoggerFilter.setCompounding(true);
                        compoundButton.setText("COMPOUNDING: ON");
                    } else if (compoundButton.getText().equals("COMPOUNDING: ON")) {
                        LoggerFilter.setCompounding(false);
                        compoundButton.setText("COMPOUNDING: OFF");
                    }
                }
            });
        }
    }

    /**
     * If a command button is pressed, this method is called. It will parse through
     * certain variables stored within Strings in the "Commands" enum and generates
     * input boxes accordingly. If the "submit" button is pressed, then the input is
     * passed into the "inputSwitch" method.
     * 
     * @param c             -> Element of the "Commands" enum that relates to the
     *                      button pressed.
     * @param hotkeyCounter -> The hotkey to map the submit button to.
     */
    public static void openInput(final LoggerFilter.Commands c, final String hotkeyCounter) {
        inputf = new JFrame();
        inputf.setSize(new Dimension(600, 400));
        inputf.setLocationRelativeTo(null);
        inputf.setResizable(false);
        inputf.setTitle("Input Panel");
        inputf.setLayout(new BorderLayout());
        final NamedJButton sub = new NamedJButton("Submit Button", "SUBMIT", hotkeyCounter);
        sub.setBounds(225, 300, 150, 50);
        sub.setBackground(spartaGreen);
        sub.setForeground(lightGreen);
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
                jcbe.setBackground(spartaGreen);
                jcbe.setForeground(lightGreen);
                allDrops.add(jcbe);
                inputf.add(jcbe);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Print Style":
                final JComboBox<Object> jcbp = createDropdown(counter, LoggerFilter.TYPE_KEYS);
                jcbp.setBackground(spartaGreen);
                jcbp.setForeground(lightGreen);
                allDrops.add(jcbp);
                inputf.add(jcbp);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Subsystem Name":
                final JComboBox<Object> jcbs = createDropdown(counter, LoggerFilter.SUBSYSTEM_KEYS);
                jcbs.setBackground(spartaGreen);
                jcbs.setForeground(lightGreen);
                allDrops.add(jcbs);
                inputf.add(jcbs);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Actuator Name":
                final JComboBox<Object> jcbc = createDropdown(counter, LoggerFilter.getActuators());
                jcbc.setBackground(spartaGreen);
                jcbc.setForeground(lightGreen);
                allDrops.add(jcbc);
                inputf.add(jcbc);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "String":
                final JTextArea jtas = createtField(counter);
                final JScrollPane jsps = new JScrollPane(jtas, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jtas.setBackground(textAreaGreen);
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
                jtai.setBackground(textAreaGreen);
                jspi.setBounds(275, 50 + (counter * 70), 50, 20);
                jtai.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), submit);
                allInputField.add(jtai);
                inputf.add(jspi);
                inputf.add(createLabel(counter, c.getParamDesc()));
                break;
            case "Graph Type":
                final String[] types = { "Line Graph (All Messages over Time)", "Bar Graph (Message Types by Count)",
                        "Pie Chart (Subsystem Messages by Count)", "Area Graph by Subsystem Messages over Time)" };
                final JComboBox<Object> jcbg = createDropdown(counter, types);
                jcbg.setBackground(spartaGreen);
                jcbg.setForeground(lightGreen);
                allDrops.add(jcbg);
                inputf.add(jcbg);
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
     * Creates a dropdown meant for the programmatic input panel and returns it.
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
     * Creates a text area meant for the programmatic input panel and returns it.
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
     * Creates a descriptor JLabel for the programmatic inputboxes and returns it.
     * 
     * @param orderNum -> Where (vertically) this label should go.
     * @param desc     -> The description, gotten from a String parameter within the
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
     * Input from programmatically generated input boxes as well as the type of
     * command is passed into here, and commands are sent out to LoggerFilter
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
        case logsbyactuator:
            LoggerFilter.logsByKeyword(input.get(0));
            break;
        case creategraph:
            LoggerFilter.createGraph(input.get(0), input.get(1), input.get(2));
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
        private final String hotkey;

        public NamedJButton(final String id, final String name, final String hotkey) {
            super(name);
            this.id = id;
            this.hotkey = hotkey;
        }

        public String getId() {
            return id;
        }

        public String getHotkey() {
            return hotkey;
        }
    }

    /**
     * An inner class that allows for graph generation and display.
     */
    public static class GraphManager {
        public enum GraphType {
            LINE(new Line()), BAR(new Bar()), PIE(new Pie()), AREA(new Area());

            public GraphDraw getGraph() {
                return graph;
            }

            private final GraphDraw graph;

            GraphType(final GraphDraw graph) {
                this.graph = graph;
            }
        }

        public interface GraphDraw {
            void draw(ArrayList<LogList> data, double[] bounds);
        }

        public static void addGraph(final GraphType type, final ArrayList<LogList> dataSet, final double[] bounds) {
            type.getGraph().draw(dataSet, bounds);
        }

        public static int[] getInterval(final ArrayList<LogList> data) {
            final int[] intervals = new int[2];
            int maxSize = 0;
            for (int i = 0; i < data.size(); i++) {
                if (maxSize < data.get(i).timeStamps.size())
                    maxSize = data.get(i).timeStamps.size();
            }
            try {
                intervals[0] = maxSize / 4;
                intervals[1] = maxSize / intervals[0];
            } catch (Exception e) {
                intervals[0] = 1;
                intervals[1] = maxSize / intervals[0];
            }
            return intervals;
        }

        public static class Bar implements GraphDraw {
            @Override
            public void draw(final ArrayList<LogList> data, final double[] bounds) {
                final DefaultCategoryDataset objDataset = new DefaultCategoryDataset();
                final String[] labels = LoggerFilter.TYPE_KEYS;
                final int[] sizesInRange = new int[LoggerFilter.TYPE_KEYS.length];

                for (int i = 0; i < labels.length; i++) {
                    for (int j = 0; j < data.get(i).timeStamps.size(); j++) {
                        if (Double.valueOf(data.get(i).timeStamps.get(j)) > bounds[0]
                                && Double.valueOf(data.get(i).timeStamps.get(j)) < bounds[1]) {
                            sizesInRange[i]++;
                        }
                    }

                    objDataset.setValue(sizesInRange[i], labels[i], labels[i]);
                }

                final JFreeChart objChart = ChartFactory.createBarChart("Type Message Bar Graph", // Chart title
                        "Time", // Domain axis label
                        "Number of Messages", // Range axis label
                        objDataset, // Chart Data
                        PlotOrientation.VERTICAL, // orientation
                        true, // include legend?
                        true, // include tooltips?
                        false // include URLs?
                );

                final ChartFrame frame = new ChartFrame("SubsystemBar", objChart);
                frame.pack();
                frame.setVisible(true);
            }
        }

        public static class Line implements GraphDraw {
            @Override
            public void draw(final ArrayList<LogList> data, final double[] bounds) {
                final DefaultCategoryDataset objDataset = new DefaultCategoryDataset();
                objDataset.addValue(0, "All Messages", "" + bounds[0]);

                final ArrayList<LogList> dataInRange = new ArrayList<>();
                dataInRange.add(new LogList());

                for (int j = 0; j < data.get(0).timeStamps.size(); j++) {
                    if (Double.valueOf(data.get(0).timeStamps.get(j)) > bounds[0]
                            && Double.valueOf(data.get(0).timeStamps.get(j)) < bounds[1]) {
                        dataInRange.get(0).timeStamps.add(data.get(0).timeStamps.get(j));
                    }
                }
                final int[] errors = new int[getInterval(dataInRange)[1]];
                int timestampIndex = 0;
                int nullIndex = 0;
                for (int i = 0; i < errors.length; i++) {
                    for (int j = 0; j < getInterval(dataInRange)[0]; j++) {
                        if (Double.valueOf(dataInRange.get(0).timeStamps.get(timestampIndex)) == nullIndex) {
                            errors[i] += 1;
                            timestampIndex++;
                        }
                        nullIndex++;
                    }
                }
                for (int i = 0; i < errors.length; i++) {
                    objDataset.addValue(errors[i], "All Messages", "" + (getInterval(data)[0]) * (i + 1));
                }

                final JFreeChart objChart = ChartFactory.createLineChart("All Messages Line Graph", // Chart title
                        "Time", // Domain axis label
                        "Number of Messages", // Range axis label
                        objDataset, // Chart Data
                        PlotOrientation.VERTICAL, // orientation
                        true, // include legend?
                        true, // include tooltips?
                        false // include URLs?
                );

                final ChartFrame frame = new ChartFrame("AllMessagesLine", objChart);
                frame.pack();
                frame.setVisible(true);
            }
        }

        public static class Area implements GraphDraw {
            @Override
            public void draw(final ArrayList<LogList> data, final double[] bounds) {
                final DefaultCategoryDataset objDataset = new DefaultCategoryDataset();
                final String[] labels = LoggerFilter.SUBSYSTEM_KEYS;
                for (int i = 0; i < data.size(); i++) {
                    Double maxStamp = 0d;
                    for (int j = 0; j < data.get(i).timeStamps.size(); j++) {
                        if (Double.valueOf(data.get(i).timeStamps.get(j)) > maxStamp) {
                            maxStamp = Double.valueOf(data.get(i).timeStamps.get(j));
                        }
                    }
                    int index = 0;
                    int emptyIndex = (int) bounds[0];
                    int total = 0;
                    double nextStamp = bounds[0];

                    while (index < maxStamp && emptyIndex < (int) bounds[1]) {
                        if ((int) nextStamp == emptyIndex) {
                            total++;
                            objDataset.addValue(total, labels[i], "" + emptyIndex);
                            index++;
                            if (index < data.get(i).timeStamps.size())
                                nextStamp = Double.valueOf(data.get(i).timeStamps.get(index));
                            else
                                break;
                        } else {
                            objDataset.addValue(total, labels[i], "" + emptyIndex);
                        }

                        emptyIndex++;
                    }
                }

                final JFreeChart objChart = ChartFactory.createAreaChart("Subsystem Message Area Graph", // Chart title
                        "Time", // Domain axis label
                        "Number of Messages", // Range axis label
                        objDataset, // Chart Data
                        PlotOrientation.VERTICAL, // orientation
                        true, // include legend?
                        true, // include tooltips?
                        false // include URLs?
                );

                final ChartFrame frame = new ChartFrame("SubsystemArea", objChart);
                frame.pack();
                frame.setVisible(true);
            }
        }

        public static class Pie implements GraphDraw {
            @Override
            public void draw(final ArrayList<LogList> data, final double[] bounds) {
                final DefaultPieDataset objDataset = new DefaultPieDataset();
                final String[] labels = LoggerFilter.SUBSYSTEM_KEYS;

                final int[] sizesInRange = new int[labels.length];

                for (int i = 0; i < labels.length; i++) {
                    for (int j = 0; j < data.get(i).timeStamps.size(); j++) {
                        if (Double.valueOf(data.get(i).timeStamps.get(j)) > bounds[0]
                                && Double.valueOf(data.get(i).timeStamps.get(j)) < bounds[1]) {
                            sizesInRange[i]++;
                        }
                    }
                }
                for (int i = 0; i < data.size(); i++) {
                    objDataset.setValue(labels[i], sizesInRange[i]);
                }

                final JFreeChart pieChart = ChartFactory.createPieChart("Subsystem Type Messages by Count", // Chart title
                        objDataset, // Chart Data
                        true, // include legend?
                        true, // include tooltips?
                        false // include URLs?
                );

                final ChartFrame frame = new ChartFrame("TypePie", pieChart);
                frame.pack();
                frame.setVisible(true);
                LoggerGUI.printToFrame("Pie graph of message types constructed successfully");
            }
        }
    }
}