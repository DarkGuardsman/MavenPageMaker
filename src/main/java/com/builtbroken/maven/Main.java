package com.builtbroken.maven;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by robert on 12/30/2014.
 */
public class Main
{
    /**
     * URL to the maven xml doc, used to get version numbers and file locations
     */
    private static URL maven_xml_url = null;

    /**
     * String value of URL to maven home path
     */
    private static String url_string;
    /**
     * URL to the maven home path
     */
    private static String maven_url_string;
    /**
     * Maven group group, added to maven_url
     */
    private static String maven_group;
    /**
     * Maven group id, added to maven_url
     */
    private static String maven_id;

    /**
     * Pattern to create links with
     */
    private static List<String> file_patterns_to_load;

    public static void main(String... args) throws ParserConfigurationException
    {
        //Temp data, todo use args to set this data
        maven_url_string = "http://ci.builtbroken.com/maven/";
        maven_group = "icbm";
        maven_id = "ICBM";
        file_patterns_to_load = new ArrayList();
        file_patterns_to_load.add("$I-$V.jar");
        file_patterns_to_load.add("$I-$V-deobf.jar");

        //Create data to use for loading the xml and creating links
        url_string = maven_url_string + maven_group + "/" + maven_id + "/";

        try
        {

            maven_xml_url = new URL(url_string + "maven-metadata.xml");
            System.out.println("Generating page for " + maven_xml_url);
            buildHtmlTable(maven_xml_url);
            System.out.println("Done!");
        } catch (MalformedURLException e)
        {
            System.out.println("Failed to generate page due to bad url");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void buildHtmlTable(URL url)
    {
        try
        {
            Document doc = getXMLFile(url);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("version");
            if (nodeList != null)
            {

                // Map of major versions to list of sub versions
                HashMap<String, List<String>> version_map = new HashMap();

                //Loop threw all version elements
                for (int b = 0; b < nodeList.getLength(); b++)
                {
                    Node node = nodeList.item(b);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element block = (Element) node;
                        String version_line = block.getTextContent();
                        if (version_line.contains("-") && version_line.contains("b"))
                        {
                            String mc_version = version_line.substring(0, version_line.indexOf("-"));
                            String version = version_line.substring(version_line.indexOf("-") + 1, version_line.indexOf("b"));
                            String build = version_line.substring(version_line.indexOf("b") + 1, version_line.length());

                            //Turn pattern into links
                            List<String> file_names = new ArrayList();
                            String html = "<tr><td>" + version + "</br>#" + build + "</td>" + version_line + "<td><ul>";
                            for (String pattern : file_patterns_to_load)
                            {
                                //Creates the file link
                                String link_name = pattern;
                                link_name = link_name.replace("$I", maven_id);
                                link_name = link_name.replace("$V", version_line);


                                //Creates display name
                                String display_name = pattern;
                                display_name = display_name.replace("$I", maven_id);
                                display_name = display_name.replace("$V", "");
                                display_name = display_name.replace("-", "");

                                link_name = url_string + link_name;
                                html += "<li><a href=\"" + link_name + "\" target=\"_blank\">" + display_name + "</a></li>";
                            }
                            html += "</ul></td></tr>";
                            if (!version_map.containsKey(mc_version))
                            {
                                version_map.put(mc_version, new ArrayList<String>());
                            }
                            version_map.get(mc_version).add(html);
                        }
                        else
                        {
                            System.out.print("Line: " + version_line + "\n Is not in the correct format. It should be 0.0.0-0.0.0b0 with the first secion being the MC version, the second being the mod version, and last being build number");
                        }
                    }
                }
                File file = new File("downloads.php");
                System.out.println("File: " + file.getAbsolutePath());
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                for(Map.Entry<String, List<String>> entry : version_map.entrySet())
                {
                    output.write("<div class=\"maven-downloads\">");
                    output.write("  <h3> Minecraft " + entry.getKey() + "Downloads </h3>");
                    output.write("  <table id=\"maven-build-table-" + entry.getKey() + "\">");
                    output.write("      <thead><tr><td>Version</td><td>Files</td></tr></thead>");
                    for(String line: entry.getValue())
                    {
                         output.write("      " + line);
                    }

                    output.write("  </table>");
                    output.write("</div>");
                }
                output.close();
            }
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (SAXException e)
        {
            e.printStackTrace();
        }
    }

    public static Document getXMLFile(URL url) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(url.openStream());
    }
}
