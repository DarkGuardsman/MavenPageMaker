package com.builtbroken.maven.page;

import java.io.File;
import java.util.ArrayList;
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
        if(version_line.contains("b"))
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
        List<String> file_names = new ArrayList();
        String html = "\n\t<tr>\n\t\t<td><span>" + getVersion() + "</span></br>#" + getBuild() +"</td><td>";
        html += "\n\t\t\t<ul>";
        for (String pattern : getBuilder().file_patterns_to_load)
        {
            //Creates the file link
            String link_name = pattern;
            link_name = link_name.replace("$I", getBuilder().maven_id);
            link_name = link_name.replace("$V", original_entry);


            //Creates display name
            String display_name = pattern;
            display_name = display_name.replace("$I", getBuilder().maven_id);
            display_name = display_name.replace("$V", "");
            display_name = display_name.replace("-", "");
            link_name = builder.url_string + original_entry + "/" +  link_name;
            if (builder.getAdfly_id() != null)
            {
                link_name = link_name.replace("http://", "");
                link_name = "adf.ly" + "/" + builder.getAdfly_id() + "/" + link_name;
            }

            html += "\n\t\t\t\t<li><a href=\"" + link_name + "\" target=\"_blank\">" + display_name + "</a></li>";
        }
        html += "\n\t\t\t</ul>\n\t\t</td>\n\t</tr>";
        return html;
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
