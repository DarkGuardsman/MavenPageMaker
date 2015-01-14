package com.builtbroken.maven.page;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by robert on 1/1/2015.
 */
public class Version
{
    private String category;
    private String version;
    private String build;
    private String original_entry;
    private PageBuilder builder;
    private Page page;

    public Version(String version_line)
    {
        this.original_entry = version_line;
        setCategory(version_line.substring(0, version_line.indexOf("-")));
        if (version_line.contains("b"))
        {
            setVersion(version_line.substring(version_line.indexOf("-") + 1, version_line.indexOf("b")));
            setBuild(version_line.substring(version_line.indexOf("b") + 1, version_line.length()));
        }
        else
        {
            setVersion(version_line.substring(version_line.indexOf("-") + 1, version_line.length()));
        }
    }

    public String toHtml()
    {
        //Turn pattern into links
        List<String> file_names = Helpers.getFilesEndingWithOnPage(getFileURLPath(), ".jar");
        String pom = Helpers.getPomFile(getFileURLPath());
        StringBuilder files_string = new StringBuilder();
        String html = builder.getVersionEntryTemplate();
        String date = "???";
        try
        {
            Document doc = Helpers.getXMLFile(getFileURLPath() + pom);
            doc.getDocumentElement().normalize();


        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (SAXException e)
        {
            e.printStackTrace();
        }

        //Inject version into template
        html = html.replace("#Version", version);
        html = html.replace("#Build", build);

        //Inject files into template
        for(String file: file_names)
        {
            String file_string = builder.getFileEntryTemplate();
            file_string = file_string.replace("#URL", "http://adf.ly/" + builder.adfly_id +"/" + getFileURLPath().replace("http://", "") + file);
            String displayName = file;

            //Clean up URL display name to remove version numbers and other junk
            if(displayName.contains("deobf"))
            {
                //TODO may have to change this if more than one file contains deobf
                displayName = "Developer.jar";
            }
            else
            {
                displayName = displayName.replace(original_entry, "");
                displayName = displayName.replace("dev", "");
                displayName = displayName.replace("-", "");
            }
            //Inject displayer name
            file_string = file_string.replace("#Name", displayName);

            //Add file entry to builder
            files_string.append("\n" + file_string);
        }

        //Inject file string into template
        html = html.replace("#File", files_string);

        return html;
    }

    public String getFileURLPath()
    {
        return builder.url_string + original_entry;
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

    public String getOriginal_entry()
    {
        return original_entry;
    }

    public void setOriginal_entry(String original_entry)
    {
        this.original_entry = original_entry;
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
