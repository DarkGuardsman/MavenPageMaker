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
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            props.setProperty("maven", "ci.builtbroken.com/maven");
            props.setProperty("alt_maven", "builtbroken.com/maven");
            props.setProperty("group", "dev.builtbroken.icbm");
            props.setProperty("id", "ICBM");
            props.setProperty("adfly", "2380428");
            props.setProperty("build_separator", "b");
            props.setProperty("prefixed_category", "true");
            //props.setProperty("version_parse", "@MC@-@MAJOR@.@MINOR@.@REV@b@BUILD@");
            props.setProperty("output_location", "html");
            props.setProperty("files", ".jar");
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
        if (file.exists())
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
            } catch (FileNotFoundException e)
            {
            }
        }
        return false;
    }

    public String maven_url_string()
    {
        return props.getProperty("maven");
    }

    public String alt_maven_url_string()
    {
        return props.getProperty("alt_maven");
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

    public String[] files()
    {
        String s = props.getProperty("files");
        if (s != null && !s.isEmpty())
        {
            s = s.replace(" ", "");
            if (s.contains(","))
            {
                return s.split(",");
            }
            else
            {
                return new String[]{s};
            }
        }
        return null;
    }

    public boolean prefixed_catigory()
    {
        String s = props.getProperty("prefixed_category");
        return s != null && !s.isEmpty() ? s.equalsIgnoreCase("true") ? true : false : false;
    }

    public String build_separator()
    {
        return props.getProperty("build_separator");
    }

    //public String version_parse() { return props.getProperty("version_parse");}

    public String output_path() { return props.getProperty("output_location");}
}