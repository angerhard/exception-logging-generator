package com.andreas_gerhard.exceptgen;

import com.andreas_gerhard.exceptgen.vo.Entry;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

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
        List<Entry> entries = getEntries();
        for (Entry entry : entries) {
            Assert.assertNotNull(entry.getDomain());
            if (entry.getDomain().equals("CUSTOMER.001")) {
                Assert.assertNotNull(entry.getException());
                Assert.assertEquals("test.test.CustomerException", entry.getException().getFqClassName());
            }
        }

    }

    @Test
    public void initInheritException() throws Exception {
        List<Entry> entries = getEntries();
        for (Entry entry : entries) {
            Assert.assertNotNull(entry.getDomain());
            if (entry.getDomain().equals("CUSTOMER.002")) {
                Assert.assertNotNull(entry.getException());
                Assert.assertEquals("test.test.CustomerException", entry.getException().getFqClassNameInherit());
            }
        }
    }

    @Test
    public void initFrontendText() throws Exception {
        List<Entry> entries = getEntries();
        for (Entry entry : entries) {
            Assert.assertNotNull(entry.getDomain());
            if (entry.getDomain().equals("CUSTOMER.001")) {
                Assert.assertNotNull(entry.getException().getFrontEndText());
                Assert.assertEquals(2, entry.getException().getFrontEndText().size());
            }
        }
    }

    @Test
    public void initBackendParameter() throws Exception {
        List<Entry> entries = getEntries();
        for (Entry entry : entries) {
            Assert.assertNotNull(entry.getDomain());
            if (entry.getDomain().equals("CUSTOMER.003")) {
                Assert.assertNotNull(entry.getBackendParameters());
                Assert.assertEquals("Integer id, String name", entry.getParameterString());
            }
        }
    }

    @Test
    public void initFrontendParameter() throws Exception {
        List<Entry> entries = getEntries();
        for (Entry entry : entries) {
            Assert.assertNotNull(entry.getDomain());
            if (entry.getDomain().equals("CUSTOMER.002")) {
                Assert.assertNotNull(entry.getException().getFrontEndParameters());
                Assert.assertEquals("Integer id", entry.getException().getParameterString());
            }
        }
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