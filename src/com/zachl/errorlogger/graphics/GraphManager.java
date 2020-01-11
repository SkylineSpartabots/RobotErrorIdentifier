package com.zachl.errorlogger.graphics;

import com.zachl.errorlogger.LogList;
import com.zachl.errorlogger.LoggerFilter;
import com.zachl.errorlogger.LoggerGUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.util.ArrayList;

public class GraphManager
{
    //public static String[] GRAPH_KEYS = {"Line", "Bar", "Pie", "Area"};
    public enum GraphType
    {
        LINE(new Line()),
        BAR(new Bar()),
        PIE(new Pie()),
        AREA(new Area());

        private GraphDraw graph;
        private AxisType axis;

        public GraphDraw getGraph()
        {
            return graph;
        }

        public AxisType getAxis()
        {
            return axis;
        }

        GraphType(GraphDraw graph)
        {
            this.graph = graph;
        }

        GraphType(GraphDraw graph, AxisType axis)
        {
            this.graph = graph;
            this.axis = axis;
        }
    }

    public enum AxisType
    {
        TYPE("Error"),
        SUBSYSTEM("Drive"),
        KEYWORD("Encoder");

        private String value;

        public String getValue()
        {
            return value;
        }

        AxisType(String value)
        {
            this.value = value;
        }
    }

    public interface GraphDraw
    {
        void draw(ArrayList<LogList> data);
    }

    public static void addGraph(GraphType type, ArrayList<LogList> dataSet)
    {
        type.getGraph().draw(dataSet);
    }

    public static class Bar implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data)
        {

        }
    }
    public static class Line implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data)
        {

        }
    }

    public static class Area implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data)
        {

        }
    }

    public static class Pie implements GraphDraw
    {
        @Override
        public void draw(ArrayList<LogList> data)
        {
            DefaultPieDataset objDataset = new DefaultPieDataset();
            String[] labels = LoggerFilter.TYPE_KEYS;

            LoggerGUI.printToFrame("" + data.size());
            for(int i = 0; i < data.size(); i++)
            {
                //objDataset.setValue(labels[i], data.get(i).messages.size());
            }

            objDataset.setValue(labels[0], data.get(0).messages.size());
            objDataset.setValue("HTC",15);
            objDataset.setValue("Samsung",24);
            objDataset.setValue("LG",7);

            JFreeChart pieChart = ChartFactory.createPieChart
                    (
                    "Message Types by Number",   //Chart title
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
