package com.builtbroken.maven;

import java.io.*;
import java.util.Properties;

public class Config
{
    String propFileName = "config.properties";
    Properties prop = new Properties();

    public void create()
    {
        Properties props = new Properties();
        try
        {
            FileOutputStream out = new FileOutputStream(new File(".", propFileName));
            props.setProperty("maven", "http://ci.builtbroken.com/maven");
            props.setProperty("group", "icbm");
            props.setProperty("id", "ICBM");
            props.setProperty("adfly", "2380428");
            props.store(out, "");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null)
        {
            try
            {
                prop.load(inputStream);
            } catch (IOException e)
            {
                System.out.println("Failed to load config file");
                e.printStackTrace();
            }
        }
    }
}