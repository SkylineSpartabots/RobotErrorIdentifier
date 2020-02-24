package roboterroridentifier;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import roboterroridentifier.LoggerFilter.LogList;

/**
 * A class to manage overviews.
 */
public class OverviewManager {
    private static JFrame sliderFrame;
    private static JPanel jp;
    private static JSlider sliderBar;
    private static JLabel jlb;
    private static JTextArea jta;
    private static JComboBox<Object> jcb;
    private static int tValue;
    private static LogList mData;
    private static JFrame imageFrame = new JFrame();;
    private static ImagePanel[] allImagePanels = new ImagePanel[0];
    private static String[] panelNames;
    private static JComboBox<Object> viewChooser;
    private static ArrayList<String> activeActuators = new ArrayList<>();
    private static int xPos;
    private static int yPos;
    private static JLabel pointLabel = new JLabel("Last clicked point: X, Y");

    public static void createSliderWindow(final LogList data) {
        mData = data;
        allImagePanels = Arrays.copyOf(allImagePanels, OverviewManager.ImageStorage.values().length);
        for (int i = 0; i < OverviewManager.ImageStorage.values().length; i++) {
            allImagePanels[i] = new ImagePanel(OverviewManager.ImageStorage.values()[i].getName(),
                    OverviewManager.ImageStorage.values()[i].getPath());
        }
        sliderFrame = new JFrame("Slider Frame");
        jp = new JPanel();
        jp.setLayout(new FlowLayout());
        sliderBar = new JSlider(0, GraphManager.maxSec(data), 0);
        jlb = new JLabel();
        final SliderListener s = new SliderListener();
        sliderBar.addChangeListener(s);
        final String[] SUBSYSTEM_KEYS_EXTENDED = new String[LoggerFilter.SUBSYSTEM_KEYS.length + 1];
        SUBSYSTEM_KEYS_EXTENDED[0] = "All";
        for (int i = 1; i < SUBSYSTEM_KEYS_EXTENDED.length; i++) {
            SUBSYSTEM_KEYS_EXTENDED[i] = LoggerFilter.SUBSYSTEM_KEYS[i - 1];
        }
        jcb = new JComboBox<>(SUBSYSTEM_KEYS_EXTENDED);

        sliderBar.setBounds(50, 25, 200, 50);
        sliderBar.setPaintTrack(true);
        sliderBar.setPaintTicks(true);
        sliderBar.setPaintLabels(true);
        sliderBar.setMajorTickSpacing(25);
        sliderBar.setMinorTickSpacing(5);
        sliderBar.setBackground(LoggerGUI.spartaGreen);
        sliderBar.setForeground(LoggerGUI.plainWhite);

        jlb.setBounds(275, 25, 100, 50);
        jlb.setText("@t = " + sliderBar.getValue());

        jcb.setBounds(50, 125, 300, 20);
        jcb.setBackground(LoggerGUI.spartaGreen);
        jcb.setForeground(LoggerGUI.plainWhite);

        jta = new JTextArea();
        final JScrollPane tlviewer = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tlviewer.setBounds(0, 150, 400, 400);

        jcb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent a) {
                jlb.setText("@t = " + sliderBar.getValue() + " - " + (sliderBar.getValue() + 1));
                updateErrors(sliderBar.getValue());
                updateGraphics();
            }
        });
        pointLabel.setBounds(125, 75, 400, 50);
        sliderFrame.add(sliderBar);
        sliderFrame.add(jlb);
        sliderFrame.add(jcb);
        sliderFrame.add(tlviewer);
        sliderFrame.add(pointLabel);
        sliderFrame.add(jp);
        sliderFrame.setSize(400, 600);
        sliderFrame.setResizable(false);
        sliderFrame.setVisible(true);
    }

    public static int getTValue() {
        return tValue;
    }

    public static void updateErrors(final int t) {
        activeActuators.clear();
        final LogList timedLog = new LogList();
        final int bottomBound = t;
        final int topBound = t + 1;
        for (int j = 0; j < mData.timeStamps.size(); j++) {
            final double ts = Double.parseDouble(mData.timeStamps.get(j));
            if (ts > bottomBound && ts <= topBound) {
                timedLog.messages.add(mData.messages.get(j));
                timedLog.timeStamps.add(mData.timeStamps.get(j));
            }
        }
        final ArrayList<LogList> subLogs = new ArrayList<>();
        for (int i = 0; i < LoggerFilter.SUBSYSTEM_KEYS.length + 1; i++) {
            subLogs.add(new LogList());
        }
        for (int i = 0; i < timedLog.messages.size(); i++) {
            boolean inAnything = false;
            for (int j = 0; j < LoggerFilter.SUBSYSTEM_KEYS.length; j++) {
                if (timedLog.messages.get(i).contains(LoggerFilter.SUBSYSTEM_KEYS[j])) {
                    subLogs.get(j).messages.add(timedLog.messages.get(i));
                    subLogs.get(j).timeStamps.add(timedLog.timeStamps.get(i));
                    inAnything = true;
                }
            }
            if (!inAnything) {
                subLogs.get(LoggerFilter.SUBSYSTEM_KEYS.length).messages.add(timedLog.messages.get(i));
                subLogs.get(LoggerFilter.SUBSYSTEM_KEYS.length).timeStamps.add(timedLog.timeStamps.get(i));
            }
        }
        jta.setText("");
        for (int i = 0; i < LoggerFilter.SUBSYSTEM_KEYS.length; i++) {
            if (checkAllowedDisplay(i)) {
                jta.append("Logs in " + LoggerFilter.SUBSYSTEM_KEYS[i] + ":\n");
                for (int j = 0; j < subLogs.get(i).messages.size(); j++) {
                    jta.append(subLogs.get(i).messages.get(j) + " @t = " + subLogs.get(i).timeStamps.get(j) + "\n");
                    for (int k = 0; k < LoggerFilter.ACTUATOR_NAMES.size(); k++) {
                        if (subLogs.get(i).messages.get(j).contains("@" + LoggerFilter.ACTUATOR_NAMES.get(k) + "@")) {
                            if (!activeActuators.contains(LoggerFilter.ACTUATOR_NAMES.get(k))) {
                                activeActuators.add(LoggerFilter.ACTUATOR_NAMES.get(k));
                            }
                        }
                    }
                }
                jta.append("\n");
            }
        }
        jta.append("Logs in " + "Other" + ":\n");
                for (int j = 0; j < subLogs.get(LoggerFilter.SUBSYSTEM_KEYS.length).messages.size(); j++) {
                    jta.append(subLogs.get(LoggerFilter.SUBSYSTEM_KEYS.length).messages.get(j) + " @t = " + subLogs.get(LoggerFilter.SUBSYSTEM_KEYS.length).timeStamps.get(j) + "\n");
                    for (int k = 0; k < LoggerFilter.ACTUATOR_NAMES.size(); k++) {
                        if (subLogs.get(LoggerFilter.SUBSYSTEM_KEYS.length).messages.get(j).contains("@" + LoggerFilter.ACTUATOR_NAMES.get(k) + "@")) {
                            if (!activeActuators.contains(LoggerFilter.ACTUATOR_NAMES.get(k))) {
                                activeActuators.add(LoggerFilter.ACTUATOR_NAMES.get(k));
                            }
                        }
                    }
                }
                jta.append("\n");
    }

    public static boolean checkAllowedDisplay(final int n) {
        boolean canDo = false;
        if (jcb.getSelectedItem().toString().equals("All")) {
            canDo = true;
        } else {
            if (jcb.getSelectedItem().toString().equals(LoggerFilter.SUBSYSTEM_KEYS[n])) {
                canDo = true;
            } else {
                canDo = false;
            }
        }
        return canDo;
    }

    public static void createOverview(final LogList data) {
        panelNames = new String[allImagePanels.length];
        for (int i = 0; i < panelNames.length; i++) {
            panelNames[i] = allImagePanels[i].getName();
        }
        viewChooser = new JComboBox<Object>(panelNames);
        viewChooser.setBounds(100, 0, 400, 20);
        viewChooser.setBackground(LoggerGUI.spartaGreen);
        viewChooser.setForeground(LoggerGUI.plainWhite);
        imageFrame.add(viewChooser);
        viewChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateGraphics();
            }
        });
        updateGraphics();
    }

    public static void updateGraphics() {
        for (final ImagePanel ip : allImagePanels) {
            imageFrame.remove(ip);
        }
        for (int i = 0; i < allImagePanels.length; i++) {
            if (viewChooser.getSelectedItem().toString().equals(allImagePanels[i].getName())) {
                imageFrame.add(allImagePanels[i]);
                imageFrame.setName(allImagePanels[i].getName());
                if (allImagePanels[i].getMouseListeners().length < 1) {
                    allImagePanels[i].addMouseListener(new ImageClickListener());
                }

            }
        }
        imageFrame.repaint();
        imageFrame.pack();
        imageFrame.setVisible(true);
        imageFrame.setLocationRelativeTo(null);
        sliderFrame.requestFocus();
    }

    public static class SliderListener implements ChangeListener {
        @Override
        public void stateChanged(final ChangeEvent e) {
            jlb.setText("@t = " + sliderBar.getValue() + " - " + (sliderBar.getValue() + 1));
            updateErrors(sliderBar.getValue());
            updateGraphics();
            sliderBar.requestFocusInWindow();
        }
    }

    public static class ImageClickListener implements MouseListener {

        @Override
        public void mouseClicked(final MouseEvent p) {
            pointLabel.setText("Last clicked point: " + p.getX() + ", " + p.getY());
        }

        @Override
        public void mouseEntered(final MouseEvent arg0) {

        }

        @Override
        public void mouseExited(final MouseEvent arg0) {

        }

        @Override
        public void mousePressed(final MouseEvent arg0) {

        }

        @Override
        public void mouseReleased(final MouseEvent arg0) {

        }
    }

    public static class ImagePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private BufferedImage mImage;
        private final String mName;
        private final HashMap<String, List<Integer>> aCoords = new HashMap<String, List<Integer>>();

        public ImagePanel(final String name, final String filePath) {
            super.setName(name);
            mName = name;
            try {
                mImage = ImageIO.read(new File(filePath));
            } catch (final IOException e) {
                LoggerGUI.printToFrame("Could not find image.");
            }
            manualDimension();
        }

        public void manualDimension() {
            ImageCoordinates.setupCoordinates(mName, aCoords);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500 + 100);
        }

        @Override
        public void paintComponent(final Graphics g) {
            final Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(mImage, 0, 20, 500, 600, this);
            g2d.setStroke(new BasicStroke(10));
            g2d.setColor(Color.RED);
            for (int j = 0; j < activeActuators.size(); j++) {
                if (aCoords.containsKey(activeActuators.get(j))) {
                    xPos = aCoords.get(activeActuators.get(j)).get(0);
                    yPos = aCoords.get(activeActuators.get(j)).get(1);
                    g2d.drawOval(xPos - 50, yPos - 50, 100, 100);
                }
            }
            g2d.dispose();
        }

        public String getName() {
            return mName;
        }

        public HashMap<String, List<Integer>> getCoords() {
            return aCoords;
        }
    }

    public enum ImageStorage {
        DRIVETRAIN("Drivetrain", "images\\2976 ROBOT_DRIVETRAIN.png"),
        HOPPER("Hopper", "images\\2976 ROBOT_HOPPER.png"), INTAKE("Intake", "images\\2976 ROBOT_INTAKE.png"),
        SHOOTER_CLIMB_FRONT("Shooter/Climb", "images\\2976 ROBOT_SHOOTER_CLIMB.png");

        public String mName;
        public String mPath;

        private ImageStorage(final String name, final String path) {
            mName = name;
            mPath = path;
        }

        public String getName() {
            return mName;
        }

        public String getPath() {
            return mPath;
        }
    }
}