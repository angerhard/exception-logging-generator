package com.andreasgerhard.exceptgen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

@org.apache.maven.plugins.annotations.Mojo(name="generate-exceptions")
public class Mojo extends AbstractMojo
{


    /**
     * Root source path for generating the exceptions
     */
    @Parameter( property = "generate-exceptions.src" )
    private String srcPathForExceptions;

    /**
     * Define the path for generating i18n.properties.
     * This path has to be relative to the maven project path.
     */
    @Parameter( property = "generate-exceptions.resources" )
    private String srcPathForI18nFile;

    /**
     * The name of the i18n property file without .properties or any
     * locate definition.
     */
    @Parameter( property = "generate-exceptions.propertyFileName", defaultValue = "exception")
    private String propertyFileName;

    /**
     * Define a fully qualified class name to generate a helper logging text class buider.
     * Will be generated in the defined src path.
     */
    @Parameter(property = "generate-exceptions.loggingHelper")
    private String loggingHelper;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}
