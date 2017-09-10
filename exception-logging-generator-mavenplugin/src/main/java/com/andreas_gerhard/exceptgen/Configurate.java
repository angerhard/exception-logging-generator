package com.andreas_gerhard.exceptgen;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Map;

public interface Configurate {

    String getPathToMessageXml();

    String getSrcPath();

    String getClassPackageName();


    String getPropertyFileName();

    String getLoggingHelper();

    public Log getLog();

    public Map getPluginContext();

    public MavenProject getProject();
}
