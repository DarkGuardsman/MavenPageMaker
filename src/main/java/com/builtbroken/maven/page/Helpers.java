package com.builtbroken.maven.page;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by robert on 1/14/2015.
 */
public class Helpers
{
    final static Pattern linkPattern = Pattern.compile("(<a[^>]+>.+?</a>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    final static Pattern urlPattern = Pattern.compile("(<a href=\".+?\")", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    public static ArrayList<String> getLinksOnPage(String page_url)
    {
        ArrayList<String> list = new ArrayList();
        URL url = null;
        InputStream is = null;
        BufferedReader reader = null;
        try
        {
            url = new URL(page_url);
            is = url.openStream();
            reader = new BufferedReader(new InputStreamReader(is));
            if (reader != null)
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    list.addAll(getLinksOnLine(line));
                }

                reader.close();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (is != null)
                try
                {
                    is.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        return list;
    }

    public static ArrayList<String> getLinksOnLine(String line)
    {
        Matcher pageMatcher = linkPattern.matcher(line);

        ArrayList<String> links = new ArrayList<String>();
        while (pageMatcher.find())
        {
            links.add(pageMatcher.group());
        }
        return links;
    }

    public static ArrayList<String> getURLPathsFromPage(String page_url)
    {
        ArrayList<String> list = new ArrayList();

        for (String link : getLinksOnPage(page_url))
        {
            Matcher pageMatcher = urlPattern.matcher(link);
            while (pageMatcher.find())
            {
                list.add(pageMatcher.group().replace("\"", "").replace("<a href=", ""));
            }
        }
        return list;
    }

    public static ArrayList<String> getFilesEndingWithOnPage(String page_url, String... files)
    {
        ArrayList<String> list = new ArrayList();
        for (String link : getURLPathsFromPage(page_url))
        {
            if (files == null || files.length == 0)
                list.add(link);
            else
                for (String s : files)
                {
                    if (link.endsWith(s))
                        list.add(link);
                }
        }
        return list;
    }

    public static String getPomFile(String page_url)
    {
        for (String link : getURLPathsFromPage(page_url))
        {
            if (link.endsWith(".pom"))
                return link;
        }
        return null;
    }

    public static String convertStreamToString(InputStream in) throws IOException
    {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb= new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while(read != null) {
            sb.append("\n" + read);
            read =br.readLine();
        }

        return sb.toString();
    }

    public static Document getXMLFile(String url) throws ParserConfigurationException, IOException, SAXException
    {
        return getXMLFile(new URL(url));
    }

    /**
     * Gets an XML file from an url
     * @param url - url
     * @return Document that is an XML file
     * @throws ParserConfigurationException - thrown if the document can not be parsed as an XML
     * @throws IOException - thrown if the URL can not be opened
     * @throws SAXException
     */
    public static Document getXMLFile(URL url) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(url.openStream());
    }
}
