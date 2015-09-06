package com.builtbroken.maven.page;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 1/1/2015.
 */
public class Version
{
    private String category = "";
    private String version = "";
    private String build = "";
    private String originalVersionString = "";
    private PageBuilder builder;
    private Page page;

    public Version(PageBuilder builder, String version_line)
    {
        this.builder = builder;
        this.originalVersionString = version_line;
        int index = 0;
        if (builder.prefixedWithCatigory && version_line.indexOf("-") != -1)
        {
            index = version_line.indexOf("-") + 1;
            setCategory(version_line.substring(0, version_line.indexOf("-")));
        }
        else
        {
            System.out.println("Version line '" + version_line + "' was not prefixed with anything ending in - ");
        }
        if (version_line.contains(builder.build_separator))
        {
            setVersion(version_line.substring(index, version_line.lastIndexOf(builder.build_separator)));
            setBuild(version_line.substring(version_line.lastIndexOf(builder.build_separator) + 1, version_line.length()));
        }
        else
        {
            setVersion(version_line.substring(index, version_line.length()));
        }

    }

    public String toHtml()
    {
        System.out.println("\tBuilding html for version " + originalVersionString);
        //Turn pattern into links
        List<String> file_names;
        if (builder.filesToUse != null)
        {
            file_names = new ArrayList();
            for (String s : builder.filesToUse)
            {
                file_names.add(builder.maven_id + "-" + originalVersionString + s);
            }
        }
        else
        {
            file_names = Helpers.getFilesEndingWithOnPage(getFileURLPath(), ".jar");
        }

        StringBuilder files_string = new StringBuilder();
        String html = builder.getVersionEntryTemplate();
        String date = null;
        try
        {
            date = getDataFromPom(getFileURLPath());
        } catch (FileNotFoundException e2)
        {
            //No pom normally means no other files
            System.out.println("\t\tPom file not found");
            return null;
        } catch (IOException e)
        {
            if (e.getMessage().contains("Server returned HTTP response code: 403 for URL"))
            {
                try
                {
                    date = getDataFromPom(builder.url_string2 + originalVersionString);
                } catch (FileNotFoundException e2)
                {
                    //No pom normally means no other files
                    System.out.println("\t\tPom file not found");
                    return null;
                } catch (IOException e2)
                {
                    if (e2.getMessage().contains("Server returned HTTP response code: 403 for URL"))
                    {
                        System.out.println(e2.getMessage());
                        //https://calclavia.s3.amazonaws.com/maven/universalelectricity/Universal-Electricity/3.1.0.75/Universal-Electricity-3.1.0.75.pom
                    }
                    else
                    {
                        e2.printStackTrace();
                    }
                }

            }
            else
            {
                e.printStackTrace();
            }
        }

        html = html.replace("#Date", date == null ? "Unknown" : date);

        //Inject version into template
        html = html.replace("#Version", version);
        html = html.replace("#Build", build);

        //Inject files into template
        int missingFilesCount = 0;
        for (String file : file_names)
        {
            String file_string = builder.getFileEntryTemplate();
            String originalFileURL = getFileURLPath() + "/" + file;
            if (!exists(originalFileURL))
            {
                originalFileURL = getFileURLPath2() + "/" + file;
                if (!exists(originalFileURL))
                {
                    missingFilesCount += 1;
                    System.out.println("\t\tFile not found " + file);
                    continue;
                }
            }

            /*
            try
            {
                File parent = new File(builder.output_folder.getParent() + "/" + builder.maven_id);
                File fileFile = new File(parent, file);
                if (!parent.exists())
                    parent.mkdirs();
                if (!fileFile.exists())
                {
                    URL website = new URL(originalFileURL);
                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(fileFile);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            } */
            if (builder.adfly_id != null && !builder.adfly_id.isEmpty())
            {
                file_string = file_string.replace("#URL", "http://adf.ly/" + builder.adfly_id + "/" + originalFileURL.replace("https://", "").replace("http://", ""));
            }
            else
            {
                file_string = file_string.replace("#URL", originalFileURL);

            }
            String displayName = file;

            //Clean up URL display name to remove version numbers and other junk
            if (displayName.contains("deobf"))
            {
                //TODO may have to change this if more than one file contains deobf
                displayName = "Developer.jar";
            }
            else
            {
                displayName = displayName.replace(originalVersionString, "");
                displayName = displayName.replace("dev", "");
                displayName = displayName.replace("-", "");
            }
            //Inject displayer name
            file_string = file_string.replace("#Name", displayName);

            //Add file entry to builder
            files_string.append("\n" + file_string);
        }

        if (missingFilesCount == file_names.size())
        {
            System.out.println("\t\tNot files found for " + originalVersionString);
            return null;
        }
        //Inject file string into template
        html = html.replace("#File", files_string);

        return html;
    }

    public static boolean exists(String URLName)
    {
        try
        {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private String getDataFromPom(String path) throws IOException
    {
        try
        {
            String pom = builder.maven_id + "-" + originalVersionString + ".pom";
            Document doc = Helpers.getXMLFile(path + "/" + pom);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("description");
            if (nodeList != null && nodeList.getLength() > 0)
            {
                String s = nodeList.item(0).getTextContent();

                s = s.replace("Created on ", "");
                //2015 01 14 17 55 43
                String year = s.substring(0, 4);
                String month = s.substring(4, 6);
                String day = s.substring(6, 8);
                String hour = s.substring(8, 10);
                String min = s.substring(10, 12);
                //String sec = s.substring(12, 14);

                return month + "/" + day + "/" + year + "  " + hour + ":" + min;
            }
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        } catch (SAXException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileURLPath()
    {
        return builder.url_string + originalVersionString;
    }

    public String getFileURLPath2()
    {
        return builder.url_string2 + originalVersionString;
    }


    //================================
    //=== Getters and Setters ========
    //================================

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getBuild()
    {
        return build;
    }

    public void setBuild(String build)
    {
        this.build = build;
    }

    public String getOriginalVersionString()
    {
        return originalVersionString;
    }

    public void setOriginalVersionString(String originalVersionString)
    {
        this.originalVersionString = originalVersionString;
    }

    public PageBuilder getBuilder()
    {
        return builder;
    }

    public void setBuilder(PageBuilder builder)
    {
        this.builder = builder;
    }

    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }
}
