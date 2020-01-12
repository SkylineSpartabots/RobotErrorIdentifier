package client.extras;

import javax.swing.*;

public class NamedJButton extends JButton
{
    /**
     * NamedJButton 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    public NamedJButton(String id, String name)
    {
        super(name);
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}