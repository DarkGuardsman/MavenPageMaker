package com.builtbroken.maven.page;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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
    public final String url_string;
    public final String maven_url_string;
    public final String maven_group;
    public final String maven_id;
    public String adfly_id;

    private URL maven_xml_url = null;

    public List<String> file_patterns_to_load;

    private File output_folder;

    public PageBuilder(File output_folder, String maven_url, String maven_group, String maven_id)
    {
        this.output_folder = output_folder;
        this.maven_url_string = (!maven_url.startsWith("http://") ? "http://" : "") + maven_url + (!maven_url.endsWith("/") ? "/" : "");
        this.maven_group = maven_group;
        this.maven_id = maven_id;
        this.url_string = maven_url_string + maven_group + "/" + maven_id + "/";
        file_patterns_to_load = new ArrayList();
        file_patterns_to_load.add("$I-$V.jar");
        file_patterns_to_load.add("$I-$V-deobf.jar");
    }

    public void buildPage() throws MalformedURLException
    {
        System.out.println("Generating page for " + url_string);
        maven_xml_url = new URL(url_string + "maven-metadata.xml");
        if(!output_folder.exists())
        {
            output_folder.mkdirs();
        }
        buildHtmlTable(maven_xml_url);
        System.out.println("Done!");
    }

    public void buildHtmlTable(URL url)
    {
        try
        {
            Document doc = getXMLFile(url);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("version");
            if (nodeList != null)
            {

                // Map of major versions to list of sub versions
                HashMap<String, Page> version_map = new HashMap();

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
                            Version entry = new Version(version_line);
                            entry.setBuilder(this);
                            if (!version_map.containsKey(entry.getCategory()))
                            {
                                version_map.put(entry.getCategory(), new Page(this, entry.getCategory()));
                            }
                            version_map.get(entry.getCategory()).add(entry);
                        }
                    }
                }
                makeHtmlFile(version_map.values());
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

    public void makeHtmlFile(Collection<Page> pages) throws IOException
    {
        for (Page page : pages)
        {
            page.outputToFile(output_folder);
        }
    }

    public Document getXMLFile(URL url) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(url.openStream());
    }

    public String getAdfly_id()
    {
        return adfly_id;
    }

    public void setAdfly_id(String adfly_id)
    {
        this.adfly_id = adfly_id;
    }
}
