package com.builtbroken.maven.page;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by robert on 1/1/2015.
 */
public class Page extends ArrayList<Version>
{
    private String version;
    public PageBuilder builder;

    public Page(PageBuilder builder, String version)
    {
        this.builder = builder;
        this.version = version;
    }

    @Override
    public boolean add(Version e)
    {
        if (super.add(e))
        {
            e.setPage(this);
            return true;
        }
        return false;
    }

    /**
     * Called to create then write the output to disk that represents the download
     * segment for this page
     *
     * @param output_folder - location to output the file in
     * @throws IOException
     */
    public synchronized void outputToFile(File output_folder) throws IOException
    {
        File home = new File(output_folder, "downloads");
        File file = new File(home, (version != null && !version.isEmpty() ? "downloads-" + version : "downloads") + ".php");
        if (!home.exists())
        {
            home.mkdirs();
        }
        String page = builder.getPageTemplate();
        StringBuilder b = new StringBuilder();
        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        //Output line per line of the versions
        for (int i = size() - 1; i >= 0; i--)
        {
            Version line = get(i);
            String h = line.toHtml();
            if (h != null && !h.isEmpty())
                b.append("\n" + builder.spacer_entry + h);
        }
        output.write("<!--Generated using Maven Download Page Maker by Robert Seifert-->");
        output.write("\n<!--Project github https://github.com/DarkGuardsman/MavenPageMaker -->");
        output.write("\n<!--Page Created on " + new Date() + "-->");


        page = page.replace("#version", version);
        page = page.replace("#Entry", b.toString());
        page = page.replace("#entry", b.toString());
        output.write(page);
        output.close();

        System.out.println("\t\tNew Page: " + file.getAbsolutePath());
    }
}
