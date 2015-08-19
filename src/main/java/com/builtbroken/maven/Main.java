package com.builtbroken.maven;

import com.builtbroken.maven.page.Helpers;
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
        File home_folder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        File html_folder = new File(home_folder, "html");
        File settings_folder = new File(home_folder, "settings");

        String maven_url_string = "";
        String maven_group = "";
        String maven_id = "";
        String adfly_id = "";
        String config_path = "";
        String html_path = "";
        String build_separator = "";

        System.out.println("*****************************************************");
        System.out.println("\tMaven Web Page Generator ");
        System.out.println("\tVersion 0.3.12 ");
        System.out.println("*****************************************************\n");
        System.out.println("\tHome: " + home_folder);

        if (args != null && args.length > 0)
        {
            for (int i = 0; i < args.length; i++)
            {
                String s = args[i];
                System.out.println("Program Arg[" + i + "] " + s);
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
                        System.out.println("-Config  -> optional, Sets the path to the config file (ex ../mavenPageMaker/Settings.config");
                        System.out.println("-html  -> optional, Sets the path to the output html folder");
                        System.out.println("-Maven  -> Sets the maven url (ex www.mypage.com/maven");
                        System.out.println("-group  -> Sub path to the maven (ex com.mypage.projectarea");
                        System.out.println("-id  -> identifing name of the maven (ex projectname");
                        System.out.println("-adfly  -> optional, account ID to add an adfly url to the front of the download links");
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
                        else if (var.equalsIgnoreCase("config"))
                        {
                            config_path = next_s;
                        }
                        else if (var.equalsIgnoreCase("html"))
                        {
                            html_path = next_s;
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
        if (!settings_folder.exists())
        {
            settings_folder.mkdirs();
        }

        File config_file = null;
        if (config_path != null && !config_path.isEmpty())
        {
            config_file = Helpers.getFileFromString(home_folder, config_path);
            if (!config_file.isFile())
                config_file = new File(config_file, "settings.config");
        }
        else
        {
            config_file = new File(settings_folder, "settings.config");
        }
        System.out.println("\tConfig: " + config_file.getAbsolutePath());

        Config config = new Config(config_file);
        if (!config.load())
        {
            config.create();
        }

        //Program arguments override configs
        if (maven_url_string == null || maven_url_string.isEmpty())
            maven_url_string = config.maven_url_string();
        if (maven_id == null || maven_id.isEmpty())
            maven_id = config.maven_id();
        if (maven_group == null || maven_group.isEmpty())
            maven_group = config.maven_group();
        if (adfly_id == null || adfly_id.isEmpty())
            adfly_id = config.adfly_id();
        if (html_path == null || html_path.isEmpty())
            html_path = config.output_path();
        if (build_separator == null || build_separator.isEmpty())
            build_separator = config.build_separator();


        if (html_path != null && !html_path.isEmpty())
            html_folder = Helpers.getFileFromString(home_folder, html_path);

        PageBuilder build = new PageBuilder(html_folder, maven_url_string, config.alt_maven_url_string(), maven_group, maven_id);
        if (adfly_id != null && !adfly_id.isEmpty())
            build.setAdfly_id(adfly_id);
        if (build_separator != null && !build_separator.isEmpty())
            build.build_separator = build_separator;
        build.prefixedWithCatigory = config.prefixed_catigory();
        build.filesToUse = config.files();

        try
        {
            build.buildPage();
        } catch (MalformedURLException e)
        {
            System.out.println("\tError: Malformed URL Exception");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("\n*****************************************************");
    }
}
