# Introduction #

Analogous to the <a href='http://pppc-base.googlecode.com'>3PC BASE middleware</a>, we are using [Maven](http://maven.apache.org/) to build PCOM in a platform- and IDE-agnostic way. Furthermore, we are using [Subversion](http://subversion.tigris.org/) for source code management. Note that this page is not intended to be a primer on Maven or Subversion. If you want to learn more about Maven, you can read [this free book](http://www.sonatype.com/Support/Books/Maven-The-Complete-Reference), for example. For Subversion, you can read [this free book](http://svnbook.red-bean.com/index.en.html) which is not only comprehensive but also amusing, at times.

# Getting the Sources #

Assuming that you have already installed a [Subversion client](http://subversion.tigris.org), a [Java SE SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) as well as [Maven](http://maven.apache.org) or an IDE-specific Maven plug-in such as [m2eclipse](http://m2eclipse.sonatype.org/) for [Eclipse](http://www.eclipse.org), you can simply checkout the [pcom-project](http://pppc-pcom.googlecode.com/svn/code/trunk/pcom-project) from the trunk folder in the SVN. This will give you the latest development snaphot. Alternatively, you can also checkout one of the tagged versions from the [tags directory](http://pppc-pcom.googlecode.com/svn/code/tags).

# Building the Code #

Once you have downloaded the code, you should be able to build the code from the command line, for example, by running `mvn clean install`. This should build all modules and install them in your local repository. Note that this will also build the Eclipse plug-in which can be used to generate stubs from Java interfaces. Thereby, the tycho plug-in will download all required code from the Eclipse download site which can take a long time to complete. If you are not working on the Eclipse plug-in, you might want to comment out the **pcom-tool-eclipse** module which will significantly speed up your build.

# Coding with Eclipse #

Assuming that you do not want to program with notepad, you can find some additional instructions on using Eclipse on the wiki page with build instructions of the <a href='http://code.google.com/p/pppc-base/wiki/BuildInstructions'>3PC BASE Middleware</a>.