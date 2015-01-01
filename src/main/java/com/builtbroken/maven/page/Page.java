package com.builtbroken.maven.page;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
        output.write("<div id=\"maven-build-div-" + version + "\" class=\"maven-downloads\">");
        output.write("  <h3> Minecraft " + version + "Downloads </h3>");
        output.write("  <table id=\"maven-build-table-" + version + "\">");
        output.write("      <thead><tr><td>Version</td><td>Files</td></tr></thead>");
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
        output.write("  </table>");
        output.write("</div>");
    }

    /** Called to create then write the output to disk that represents the download
     * segment for this page
     *
     * @param output_folder - location to output the file in
     * @throws IOException
     */
    public synchronized void outputToFile(File output_folder) throws IOException
    {
        File file = new File(output_folder, "downloads" + File.separator + "downloads-" + version + ".php");

        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        outputHeader(output);

        //Output line per line of the versions
        for (Version line : this)
        {
            outputVersion(output, line);
        }

        outputFooter(output);

        output.close();

        System.out.println("Outputted Download Version" + version + " HTML File To  " + file.getAbsolutePath());
    }
}
