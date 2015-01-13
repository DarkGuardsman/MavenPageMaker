package com.builtbroken.maven;

import java.io.*;
import java.util.Properties;

public class Config
{
    public Properties props = new Properties();

    File file;

    public Config(File file)
    {
        this.file = file;
    }

    public void create()
    {
        props = new Properties();
        try
        {
            if(!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            props.setProperty("maven", "ci.builtbroken.com/maven");
            props.setProperty("group", "icbm");
            props.setProperty("id", "ICBM");
            props.setProperty("adfly", "2380428");
            props.store(out, "");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean load()
    {
        if(file.exists())
        {
            try
            {
                InputStream inputStream = new FileInputStream(file);
                if (inputStream != null)
                {
                    try
                    {
                        props.load(inputStream);
                    } catch (IOException e)
                    {
                        System.out.println("Failed to load config file");
                        e.printStackTrace();
                    }
                    return true;
                }
            }
            catch (FileNotFoundException e)
            {
            }
        }
        return false;
    }

    public String maven_url_string()
    {
        return props.getProperty("maven");
    }

    public String maven_group()
    {
        return props.getProperty("group");
    }

    public String maven_id()
    {
        return props.getProperty("id");
    }

    public String adfly_id()
    {
        return props.getProperty("adfly");
    }
}