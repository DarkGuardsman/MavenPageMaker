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
        if(super.add(e))
        {
            e.setPage(this);
            return true;
        }
        return false;
    }

    /** Creates the top segment of the output page
     * @param output - stream to write to
     * @throws IOException
     */
    protected void outputHeader(BufferedWriter output) throws IOException
    {
        output.write("<!--Generated using Maven Download Page Maker by Robert Seifert-->");
        output.write("\n<!--Project github https://github.com/DarkGuardsman/MavenPageMaker -->");
        output.write("\n<!--Page Created on " + new Date() + "-->");
        output.write("\n<div id=\"maven-build-div-" + version + "\" class=\"maven-downloads\">");
        output.write("\n<h3> Minecraft " + version + " Downloads </h3>");
        output.write("\n\t<table id=\"maven-build-table-" + version + "\">");
        output.write("\n<thead><tr><td>Version</td><td>Files</td></tr></thead>");
    }

    public void outputVersion(BufferedWriter output, Version v) throws IOException
    {
        output.write("      " + v.toHtml());
    }

    /** Creates the bottom segment of the output page. Normally this comes
     * to only some closing html tags
     * @param output - stream to write to
     * @throws IOException
     */
    protected void outputFooter(BufferedWriter output) throws IOException
    {
        output.write("\n</table>");
        output.write("\n</div>");
    }

    /** Called to create then write the output to disk that represents the download
     * segment for this page
     *
     * @param output_folder - location to output the file in
     * @throws IOException
     */
    public synchronized void outputToFile(File output_folder) throws IOException
    {
        File home = new File(output_folder, "downloads");
        File file = new File(home, "downloads-" + version + ".php");
        if(!home.exists())
        {
            home.mkdirs();
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        outputHeader(output);

        //Output line per line of the versions
        for (int i = size() -1; i >= 0; i--)
        {
            Version line = get(i);
            outputVersion(output, line);
        }

        outputFooter(output);

        output.close();

        System.out.println("Outputted Download Version" + version + " HTML File To  " + file.getAbsolutePath());
    }
}
