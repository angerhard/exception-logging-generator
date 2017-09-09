package com.andreasgerhard.exceptgen;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Map;

public interface Configurate {

    String getPathToMessageXml();

    String getSrcPathForExceptions();

    String getClassPackageName();

    String getSrcPathForI18nFile();

    String getPropertyFileName();

    String getLoggingHelper();

    public Log getLog();

    public Map getPluginContext();

    public MavenProject getProject();
}
