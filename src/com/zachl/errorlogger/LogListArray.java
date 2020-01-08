package com.zachl.errorlogger;

import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogListArray
{
    public ArrayList<ArrayList<String>> messageList;
    public ArrayList<ArrayList<String>> timeStampList;
    public ArrayList<HashMap<String, List<String>>> valueList;

    public LogListArray()
    {
        messageList = new ArrayList<ArrayList<String>>();
        timeStampList = new ArrayList<ArrayList<String>>();
        valueList = new ArrayList<HashMap<String, List<String>>>();
    }
    /*public LogListArray(ArrayList<String> messages, ArrayList<String> timeStamps, HashMap<String, List<String>> values)
    {
        this.messages = messages;
        this.timeStamps = timeStamps;
        this.values = values;
    }*/
}
