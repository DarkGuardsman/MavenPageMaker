MavenPageMaker
==============

Java program designed to run from a builder server to generate a webpage from a maven. In which it creates a table of downloads allowing other sites to include or copy the page to create a download section. 


## Config
Run the program without argument will cause it to generate a config. Most of the line in the config are the same as the program arguments. More options will be added later to change the format of the output page.

## Program Args
All though you can run the program with arguments it is best to just use the config. This way you do not have to run the program with arguments each time. However the arguments are as follows.

Maven - URL to the maven folder

ID - maven id

Group - maven group id

adfly - optional, used to add adfly links to the front of the site links
