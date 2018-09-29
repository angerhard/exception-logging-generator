package de.andreasgerhard.exceptgen;

import de.andreasgerhard.exceptgen.model.Entry;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExceptionBuilderTest {

    private String _exceptionIdentificaionDomain = "customerNotFound";
    private Configurate standard = new Configurate() {
        @Override
        public String getPathToMessageXml() {
            return "/src/test/resources/test-messages.xml";
        }

        @Override
        public String getSrcPath() {
            return "/target/tmp";
        }

        @Override
        public File getBaseDir() {
            Properties props = new Properties();
            try {
                props.load(this.getClass().getResourceAsStream("/project.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String basedir = String.valueOf(props.get("project.basedir"));
            return new File(basedir);
        }

        @Override
        public String getClassPackageName() {
            return "test.test";
        }

        @Override
        public String getPropertyFileName() {
            return "i18ndemo";
        }

        @Override
        public String getLoggingHelper() {
            return "test.test.test.Logger";
        }

        @Override
        public String getResourcesPath() {
            return "/target/tmp";
        }

        @Override
        public Log getLog() {
            return new DefaultLog(new ConsoleLogger());
        }

    };

    @Test
    public void initCreateException() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.001", getEntries());
        Assert.assertTrue(entry.isPresent());
        Assert.assertNotNull(entry.get().getException());
        Assert.assertEquals("com.andreasgerhard.exception.CustomerException", entry.get().getException().getFqClassName());
    }

    @Test
    public void initInheritException() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.002", getEntries());
        Assert.assertTrue(entry.isPresent());
        Assert.assertNotNull(entry.get().getException());
        Assert.assertEquals("com.andreasgerhard.exception.CustomerException", entry.get().getException().getFqClassNameInherit());
    }

    @Test
    public void initFrontendText() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.001", getEntries());
        Assert.assertTrue(entry.isPresent());
        Assert.assertNotNull(entry.get().getException().getFrontEndText());
        Assert.assertEquals(2, entry.get().getException().getFrontEndText().size());
    }

    @Test
    public void initBackendParameter() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.003", getEntries());
        Assert.assertTrue(entry.isPresent());
        Assert.assertNotNull(entry.get().getBackendParameters());
        Assert.assertEquals("Integer id, String name", entry.get().getParameterString());
    }

    @Test
    public void initFrontendParameter() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.002", getEntries());
        Assert.assertTrue(entry.isPresent());
        Assert.assertNotNull(entry.get().getException().getFrontEndParameters());
        Assert.assertEquals("Integer id", entry.get().getException().getParameterString());
    }

    @Test
    public void correctPackage() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.001", getEntries());
        Entry e = entry.get();
        String fqClassName = e.getException().getFqClassName();
        Assert.assertTrue(String.format("Package has to be overwriten when defined in xml " +
                "(should: com.andreasgerhard.exception, is %s).",
                fqClassName), fqClassName.startsWith("com.andreasgerhard.exception"));
    }

    @Test
    public void correctPackageUsingStandard() throws Exception {
        Optional<Entry> entry = TestTool.retrieveEntry("CUSTOMER.001", getEntries());
        Entry e = entry.get();
        String fqClassName = e.getException().getFqClassName();
        Assert.assertTrue(String.format("Package has to be overwriten when defined in xml " +
                "(should: com.andreasgerhard.exception, is %s).",
                fqClassName), fqClassName.startsWith("com.andreasgerhard.exception"));
    }

    @Ignore
    public void methodLikeInExceptionTest() {
        StringBuilder result = new StringBuilder("{\n");
        for (String locale : new String[]{"de", "en"}) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(standard.getLoggingHelper(), Locale.forLanguageTag(locale));
            result.append(String.format("\t[\"%s\", \"%s\"]\n", locale, resourceBundle.getString(_exceptionIdentificaionDomain)));
        }
        result.append("}\n");
        System.out.println(result.toString());
    }

    private List<Entry> getEntries() throws Exception {
        ExceptionBuilder exceptionBuilder = new ExceptionBuilder(standard);
        return exceptionBuilder.getEntries();
    }
}