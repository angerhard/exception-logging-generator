package com.andreas_gerhard.exceptgen;

import org.apache.maven.plugin.logging.Log;

import java.io.File;

public interface Configurate {

    String getPathToMessageXml();

    String getSrcPath();

    String getClassPackageName();


    String getPropertyFileName();

    String getLoggingHelper();

    public Log getLog();

    File getBaseDir();

    String getResourcesPath();

    String getEncoding();
}
