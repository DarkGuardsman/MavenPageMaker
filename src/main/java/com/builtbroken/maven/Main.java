package com.builtbroken.maven;

import com.builtbroken.maven.page.PageBuilder;

import javax.xml.parsers.ParserConfigurationException;
import java.net.MalformedURLException;

/**
 * Created by robert on 12/30/2014.
 */
public class Main
{
    public static void main(String... args) throws ParserConfigurationException
    {
        String maven_url_string = "";
        String maven_group = "";
        String maven_id = "";
        String adfly_id = "";

        if (args != null && args.length > 0)
        {
            for (int i = 0; i < args.length; i++)
            {
                String s = args[i];
                boolean valid = (i + 1 < args.length) && !args[i + 1].contains("-");
                String next_s = valid ? args[i + 1] : null;
                if (s.startsWith("-"))
                {
                    String var = s.replace("-", "");
                    if (var.equalsIgnoreCase("help"))
                    {
                        System.out.println("In order for the program to run correctly " +
                                "you need to add some arguments. This way we can build " +
                                "the page information correctly.\n\n");
                        System.out.println("Valid program arguments");
                        System.exit(0);
                    }
                    else if (next_s != null)
                    {
                        if(valid)
                        {
                            i++;
                        }
                        if (var.equalsIgnoreCase("maven"))
                        {
                            maven_url_string = next_s;
                        }
                        else if (var.equalsIgnoreCase("group"))
                        {
                            maven_group = next_s;
                        }
                        else if (var.equalsIgnoreCase("id"))
                        {
                            maven_id = next_s;
                        }
                        else if (var.equalsIgnoreCase("adfly"))
                        {
                            adfly_id = next_s;
                        }
                    }
                }
                else
                {
                    throw new IllegalArgumentException("Invalid program argument " + s);
                }
            }

            PageBuilder build = new PageBuilder(maven_url_string, maven_group, maven_id);
            if(!adfly_id.isEmpty())
                build.setAdfly_id(adfly_id);

            try
            {
                build.buildPage();
            }
            catch (MalformedURLException e)
            {
                System.out.println("Bad URL");
                e.printStackTrace();
            }
        }
        else
        {
            throw new IllegalArgumentException("No program argument given run with -help");
        }


    }


}
