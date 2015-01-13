package com.builtbroken.maven;

import com.builtbroken.maven.page.PageBuilder;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by robert on 12/30/2014.
 */
public class Main
{
    //-maven http://ci.builtbroken.com/maven -group icbm -id ICBM -adfly 2380428
    public static void main(String... args) throws ParserConfigurationException, URISyntaxException
    {
        String maven_url_string = "";
        String maven_group = "";
        String maven_id = "";
        String adfly_id = "";
        File html_folder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "html");
        File home_folder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "mavenPageMaker");

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
                        if (valid)
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
        }
        else
        {
            if (!home_folder.exists())
            {
                home_folder.mkdirs();
            }
            System.out.println("Searching " + home_folder + " for configs");
            Config config = new Config(new File(home_folder, "settings.config"));
            if (!config.load())
            {
                config.create();
            }
            maven_url_string = config.maven_url_string();
            maven_id = config.maven_id();
            maven_group = config.maven_group();
            adfly_id = config.adfly_id();
            String html_path = config.output_path();
            if (html_path == null || html_path.isEmpty())
            {
                System.out.println("File path in the config can not be empty");
                System.exit(-1);
            }
            else if (html_path.startsWith("../"))
            {
               html_folder = home_folder.getParentFile();
               for( int i = 1; i < html_path.split("../").length; i++)
               {
                   html_folder = html_folder.getParentFile();
               }
                html_folder = new File(html_folder, html_path.replace("../", ""));
            }
            else if (html_path.substring(1, html_path.length()).startsWith(":/"))
            {
                html_folder = new File(html_path);
            }
            else
            {
                html_folder = new File(home_folder.getParent(), html_path);
            }
        }

        PageBuilder build = new PageBuilder(html_folder, maven_url_string, maven_group, maven_id);
        if (adfly_id != null && !adfly_id.isEmpty())
            build.setAdfly_id(adfly_id);

        try
        {
            build.buildPage();
        } catch (MalformedURLException e)
        {
            System.out.println("Bad URL");
            e.printStackTrace();
        }
    }
}
