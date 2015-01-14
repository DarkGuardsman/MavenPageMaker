package com.builtbroken.maven.page;

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
public class URLHelper
{
    final static Pattern linkPattern = Pattern.compile("(<a[^>]+>.+?</a>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


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
}
