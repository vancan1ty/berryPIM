berryPIM
==================

Introduction
------------------

berryPIM is my personal information management system.  It manages information such including my calendar, my notes,
my finances, todos, contacts, and arbitrary personal documents.  In the near future I hope to add expanded support
for emails and passwords.

Goals
------------------

berryPIM has specific design goals which are different than the design goals of any other PIM software I have seen.
1. Be available offline on my computer & online on most web-connected devices.
   1. BerryPIM accomplishes this by using git as a revision management tool for data.  git enables plug-and-play
   content history tracking, and also enables a distributed usage model where I can either use the application deployed
   online directly from my server (any device) or use the application deployed locally without internet (my computer).
2. Be usable from devices with low specifications.
   1. I have a dumbphone.  berryPIM works with my dumbphone, thereby giving me access to all of my documents and
   information on the go, from anywhere.
   2. In order to achieve this, at this time I have chosen to make most of the rendering and functionality run on
   the server-side, thereby imposing a minimum of requirements on the client.
3. Store data in a reliable matter, and use a general purpose format which allows me to leverage preexisting tools
and practices.
   1. I use XML as my data storage format.  For my application, this format works extremely well.  There are a
   number of tools available to parse, validate, and query xml.
   2. I use Xpath and XQuery to link the UI together of berryPIM and embed on-the-fly scripting/customization into
   berryPIM.  I reuse tools for each of the different application domains.

Building/Running
------------------

berryPIM is built using Maven.  It is intended that the build can either produce a standalone uberjar or a war file
for deployment within an existing java webserver.  However, at the moment, really only the standalone jar is tested
and the other option needs work.

Build a standalone jar as follows:

    mvn clean package -Pstandalone

For now, the simplest way to run berryPIM looks like the following:

    java -DBERRYPIM_PWFILE=berrypim_passwords.txt -DBERRYPIM_DATA_ROOT=[path/to/data/folder] -jar berrypim-1.0-SNAPSHOT.jar

More info on how to get started with berryPIM is on its way soon.  Right now, it's going to be a pain for anyone
besides me to get started with.

