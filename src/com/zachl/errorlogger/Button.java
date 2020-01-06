package com.zachl.errorlogger;

import javax.swing.*;

public class Button extends JButton
{
    private String id;
    public Button(String id, String name)
    {
        super(name);
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
