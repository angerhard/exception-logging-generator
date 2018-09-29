package de.andreasgerhard.exceptgen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.charset.Charset;

@org.apache.maven.plugins.annotations.Mojo(name="generate-exceptions")
public class Mojo extends AbstractMojo implements Configurate {


    /**
     * Path to the message.xml for retrieving log messages and exceptions.
     */
    @Parameter( property = "generate-exceptions.messageXml" )
    private String pathToMessageXml;

    /**
     * Use this package, when no other package name has been declared in the messages.xml.
     */
    @Parameter( property = "generate-exceptions.classPackageName" )
    private String classPackageName;

    /**
     * Relative source path for generating the exceptions and logging helper.
     */
    @Parameter( property = "generate-exceptions.src" )
    private String srcPath;

    /**
     * Relative source path for generating the exceptions and logging helper.
     * Will be generated in the defined resource path.
     */
    @Parameter( property = "generate-exceptions.resources" )
    private String resourcesPath;

    /**
     * The name of the i18n property file without .properties or any
     * locate definition.
     */
    @Parameter( property = "generate-exceptions.propertyFileName", defaultValue = "exception")
    private String propertyFileName;


    @Parameter(readonly = true, defaultValue = "${project.build.sourceEncoding}")
    private String sourceEncoding;

    @Parameter(readonly = true, defaultValue = "${project.reporting.outputEncoding}")
    private String outputEncoding;

    /**
     * Define a fully qualified class name to generate a helper logging text class buider.
     * Will be generated in the defined src path.
     */
    @Parameter(property = "generate-exceptions.loggingHelper")
    private String loggingHelper;

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File configXmlFile = new File(getBaseDir(), getPathToMessageXml());
        if (configXmlFile.exists()) {
            try {
                new ExceptionBuilder(this);
            } catch (Exception e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        } else {
            getLog().info("no message.xml, do not generate exception classes.");
        }

    }

    @Override
    public String getPathToMessageXml() {
        return pathToMessageXml;
    }

    @Override
    public String getSrcPath() {
        return srcPath;
    }

    @Override
    public String getPropertyFileName() {
        return propertyFileName;
    }

    @Override
    public String getLoggingHelper() {
        return loggingHelper;
    }

    @Override
    public Log getLog() {
        return super.getLog();
    }

    @Override
    public String getClassPackageName() {
        return classPackageName;
    }

    @Override
    public File getBaseDir() {
        return project.getBasedir();
    }

    @Override
    public String getResourcesPath() {
        return resourcesPath;
    }


    @Override
    public String getEncoding() {
        return outputEncoding == null ? (sourceEncoding == null ? Charset.defaultCharset().name() : sourceEncoding) : outputEncoding;
    }
}
