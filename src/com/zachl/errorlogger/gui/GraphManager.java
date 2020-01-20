package com.zachl.errorlogger.gui;

import com.zachl.errorlogger.LogList;
import com.zachl.errorlogger.LoggerFilter;
import com.zachl.errorlogger.LoggerGUI;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.ValueAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;

import java.util.ArrayList;
import java.util.concurrent.atomic.DoubleAccumulator;

public class GraphManager
{
    public enum GraphType
    {
        LINE(new Line()),
        BAR(new Bar()),
        PIE(new Pie()),
        AREA(new Area());

        public GraphDraw getGraph() {
            return graph;
        }

        private GraphDraw graph;

        GraphType(GraphDraw graph)
        {
            this.graph = graph;
        }
    }

    public interface GraphDraw
    {
        void draw(ArrayList<LogList> data, double[] bounds);
    }

    public static void addGraph(GraphType type, ArrayList<LogList> dataSet, double[] bounds)
    {
        type.getGraph().draw(dataSet, bounds);
    }

    public static int[] getInterval(ArrayList<LogList> data)
    {
        int[] intervals = new int[2];
        int maxSize = 0;
        for(int i = 0; i < data.size(); i++)
        {
            if(maxSize < data.get(i).timeStamps.size())
                maxSize = data.get(i).timeStamps.size();
        }
        intervals[0] = maxSize / 4;
        intervals[1] = maxSize / intervals[0];
        return intervals;
    }
    public static class Bar implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data, double[] bounds)
        {
            DefaultCategoryDataset objDataset = new DefaultCategoryDataset();
            String[] labels = LoggerFilter.TYPE_KEYS;
            int[] sizesInRange = new int[LoggerFilter.TYPE_KEYS.length];

            for(int i = 0; i < labels.length; i++)
            {
                for(int j = 0; j < data.get(i).timeStamps.size(); j++)
                {
                    if(Double.valueOf(data.get(i).timeStamps.get(j)) > bounds[0] && Double.valueOf(data.get(i).timeStamps.get(j)) < bounds[1])
                    {
                        sizesInRange[i]++;
                    }
                }

                objDataset.setValue(sizesInRange[i], labels[i], labels[i]);
            }

            JFreeChart objChart = ChartFactory.createBarChart(
                    "Type Message Bar Graph",     //Chart title
                    "Type",     //Domain axis label
                    "Number of Messages",         //Range axis label
                    objDataset,         //Chart Data
                    PlotOrientation.VERTICAL, // orientation
                    true,             // include legend?
                    true,             // include tooltips?
                    false             // include URLs?
            );

            ChartFrame frame = new ChartFrame("SubsystemBar", objChart);
            frame.pack();
            frame.setVisible(true);
        }
    }
    public static class Line implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data, double[] bounds)
        {
            DefaultCategoryDataset objDataset = new DefaultCategoryDataset();
            objDataset.addValue(0, "All Messages", "" + bounds[0]);

            ArrayList<LogList> dataInRange = new ArrayList<>();
            dataInRange.add(new LogList());

            for(int j = 0; j < data.get(0).timeStamps.size(); j++)
            {
                if(Double.valueOf(data.get(0).timeStamps.get(j)) > bounds[0] && Double.valueOf(data.get(0).timeStamps.get(j)) < bounds[1])
                {
                    dataInRange.get(0).timeStamps.add(data.get(0).timeStamps.get(j));
                }
            }
            int[] errors = new int[getInterval(dataInRange)[1]];
            int timestampIndex = 0;
            int nullIndex = 0;
            for(int i = 0; i < errors.length; i++)
            {
                for(int j = 0; j < getInterval(dataInRange)[0]; j++)
                {
                    if(Double.valueOf(dataInRange.get(0).timeStamps.get(timestampIndex)) == nullIndex)
                    {
                        errors[i] += 1;
                        timestampIndex++;
                    }
                    nullIndex++;
                }
            }
            for(int i = 0; i < errors.length; i++)
            {
                objDataset.addValue(errors[i], "All Messages", "" + (getInterval(data)[0]) * (i + 1));
            }

            JFreeChart objChart = ChartFactory.createLineChart(
                    "All Messages Line Graph",     //Chart title
                    "Subsystem",     //Domain axis label
                    "Number of Messages",         //Range axis label
                    objDataset,         //Chart Data
                    PlotOrientation.VERTICAL, // orientation
                    true,             // include legend?
                    true,             // include tooltips?
                    false             // include URLs?
            );

            ChartFrame frame = new ChartFrame("AllMessagesLine", objChart);
            frame.pack();
            frame.setVisible(true);
        }
    }

    public static class Area implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data, double[] bounds)
        {
            DefaultCategoryDataset objDataset = new DefaultCategoryDataset();
            String[] labels = LoggerFilter.SUBSYSTEM_KEYS;
            for(int i = 0; i < data.size(); i++)
            {
                Double maxStamp = 0d;
                for (int j = 0; j < data.get(i).timeStamps.size(); j++) {
                    if (Double.valueOf(data.get(i).timeStamps.get(j)) > maxStamp) {
                        maxStamp = Double.valueOf(data.get(i).timeStamps.get(j));
                    }
                }
                int index = 0;
                int emptyIndex = (int)bounds[0];
                int total = 0;
                double nextStamp = /*Double.valueOf(data.get(i).timeStamps.get(0))*/ bounds[0];

                while (index < maxStamp && emptyIndex < (int)bounds[1])
                {
                    if ((int) nextStamp == emptyIndex)
                    {
                        total++;
                        objDataset.addValue(total, labels[i], "" + emptyIndex);
                        index++;
                        if (index < data.get(i).timeStamps.size())
                            nextStamp = Double.valueOf(data.get(i).timeStamps.get(index));
                        else
                            break;
                    } else
                        {
                        objDataset.addValue(total, labels[i], "" + emptyIndex);
                    }

                    emptyIndex++;
                }
            }

            JFreeChart objChart = ChartFactory.createAreaChart(
                    "Subsystem Message Area Graph",     //Chart title
                    "Subsystem",     //Domain axis label
                    "Number of Messages",         //Range axis label
                    objDataset,         //Chart Data
                    PlotOrientation.VERTICAL, // orientation
                    true,             // include legend?
                    true,             // include tooltips?
                    false             // include URLs?
            );

            ChartFrame frame = new ChartFrame("SubsystemArea", objChart);
            frame.pack();
            frame.setVisible(true);
        }
    }

    public static class Pie implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data, double[] bounds)
        {
            DefaultPieDataset objDataset = new DefaultPieDataset();
            String[] labels = LoggerFilter.SUBSYSTEM_KEYS;

            int[] sizesInRange = new int[labels.length];

            for(int i = 0; i < labels.length; i++) {
                for (int j = 0; j < data.get(i).timeStamps.size(); j++) {
                    if (Double.valueOf(data.get(i).timeStamps.get(j)) > bounds[0] && Double.valueOf(data.get(i).timeStamps.get(j)) < bounds[1]) {
                        sizesInRange[i]++;
                    }
                }
            }
            for(int i = 0; i < data.size(); i++)
            {
                objDataset.setValue(labels[i], sizesInRange[i]);
            }

            JFreeChart pieChart = ChartFactory.createPieChart
                    (
                    "Subsystem Type Messages by Count",   //Chart title
                    objDataset,          //Chart Data
                    true,               // include legend?
                    true,               // include tooltips?
                    false               // include URLs?
            );

            ChartFrame frame = new ChartFrame("TypePie", pieChart);
            frame.pack();
            frame.setVisible(true);
            LoggerGUI.printToFrame("Pie graph of message types constructed successfully");
        }
    }
}
