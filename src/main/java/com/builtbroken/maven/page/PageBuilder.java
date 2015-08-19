package com.builtbroken.maven.page;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robert on 1/1/2015.
 */
public class PageBuilder
{
    //Maven info
    public final String maven_group;
    public final String maven_id;

    //Primary maven URL data
    public final String url_string;
    public final String maven_url_string;

    //Secondary maven URL data
    public final String maven_url_string2;
    public final String url_string2;

    private String file_entry_template;
    private String version_entry_template;
    private String page_template;
    public String spacer_entry = "";
    public String build_separator = "b";
    public String[] filesToUse = null;
    public boolean prefixedWithCatigory = true;

    public String adfly_id;

    private URL maven_xml_url = null;

    public List<String> file_patterns_to_load;

    private File output_folder;

    public PageBuilder(File output_folder, String maven_url, String maven_url_alt, String maven_group, String maven_id)
    {
        this.maven_group = maven_group.replace(".", "/");
        this.maven_id = maven_id;
        this.output_folder = output_folder;

        this.maven_url_string = (!maven_url.startsWith("http") ? "http://" : "") + maven_url + (!maven_url.endsWith("/") ? "/" : "");
        this.url_string = maven_url_string + this.maven_group + "/" + this.maven_id + "/";

        this.maven_url_string2 = (!maven_url_alt.startsWith("http") ? "http://" : "") + maven_url_alt + (!maven_url_alt.endsWith("/") ? "/" : "");
        this.url_string2 = maven_url_string2 + this.maven_group + "/" + this.maven_id + "/";

        file_patterns_to_load = new ArrayList();
        file_patterns_to_load.add("$I-$V.jar");
        file_patterns_to_load.add("$I-$V-deobf.jar");
    }

    public void buildPage() throws MalformedURLException
    {
        System.out.println("\tXML: " + url_string + "maven-metadata.xml");
        maven_xml_url = new URL(url_string + "maven-metadata.xml");
        if (!output_folder.exists())
        {
            output_folder.mkdirs();
        }
        try
        {
            buildHtmlTable(maven_xml_url);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println();
            System.out.println();
            System.out.println("\tXML2: " + url_string2 + "maven-metadata.xml");
            maven_xml_url = new URL(url_string2 + "maven-metadata.xml");
            try
            {
                buildHtmlTable(maven_xml_url);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }

    }

    public void buildHtmlTable(URL url) throws IOException
    {
        System.out.println("\n\tStarting to build HTML table");
        try
        {
            Document doc = Helpers.getXMLFile(url);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("version");

            if (nodeList != null)
            {
                System.out.println("\tFound " + nodeList.getLength() + " version elements in XML");
                // Map of major versions to list of sub versions
                HashMap<String, Page> version_map = new HashMap();

                //Loop threw all version elements
                for (int b = 1; b < nodeList.getLength(); b++)
                {
                    Node node = nodeList.item(b);

                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element block = (Element) node;
                        String version_line = block.getTextContent();
                        System.out.println("\tEntry[" + b + "] = " + version_line);

                        Version entry = new Version(this, version_line);
                        System.out.println("\t\tVersion: " + entry.getVersion() + " Build: " + entry.getBuild());
                        System.out.println("\t\tFolder: " + entry.getFileURLPath());
                        if (!version_map.containsKey(entry.getCategory()))
                        {
                            version_map.put(entry.getCategory(), new Page(this, entry.getCategory()));
                        }
                        version_map.get(entry.getCategory()).add(entry);
                    }
                }
                makeHtmlFile(version_map.values());
            }
            else
            {
                System.out.println("Error node list for version returned null");
            }
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        } catch (SAXException e)
        {
            e.printStackTrace();
        }
    }

    public void makeHtmlFile(Collection<Page> pages) throws IOException
    {
        for (Page page : pages)
        {
            page.outputToFile(output_folder);
        }
    }


    public String getAdfly_id()
    {
        return adfly_id;
    }

    public String getFileEntryTemplate()
    {
        if (file_entry_template == null)
        {
            InputStream is = this.getClass().getResourceAsStream("/templates/file.php");
            try
            {
                file_entry_template = Helpers.convertStreamToString(is);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                is.close();
            } catch (IOException e)
            {
            }
        }
        return file_entry_template;
    }

    public String getVersionEntryTemplate()
    {
        if (version_entry_template == null)
        {
            InputStream is = this.getClass().getResourceAsStream("/templates/entry.php");
            try
            {
                version_entry_template = Helpers.convertStreamToString(is);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                is.close();
            } catch (IOException e)
            {
            }
        }
        return version_entry_template;
    }

    public String getPageTemplate()
    {
        if (page_template == null)
        {
            InputStream in = this.getClass().getResourceAsStream("/templates/page.php");
            try
            {
                page_template = Helpers.convertStreamToString(in);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                in.close();
            } catch (IOException e)
            {
            }
        }
        return page_template;
    }


    public void setAdfly_id(String adfly_id)
    {
        this.adfly_id = adfly_id;
    }
}
