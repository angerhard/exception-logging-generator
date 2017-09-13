package com.andreas_gerhard.exceptgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import com.andreas_gerhard.exceptgen.vo.Entry;
import com.andreas_gerhard.exceptgen.vo.Exception;
import com.andreas_gerhard.exceptgen.vo.Parameter;
import com.andreas_gerhard.exceptgen.vo.Text;
import com.andreasgerhard.exceptgen.messages.ExceptionType;
import com.andreasgerhard.exceptgen.messages.FrontendMessageType;
import com.andreasgerhard.exceptgen.messages.FrontendMessagesType;
import com.andreasgerhard.exceptgen.messages.MessageType;
import com.andreasgerhard.exceptgen.messages.MessagesType;

public class ExceptionBuilder {

    private VelocityEngine ve;

    private Set<String> domainDuplicateChecker = new HashSet<>();
    private final List<Entry> entries;

    /**
     * Generates the needed classes, given by the configuration of th emaven plugin.
     * @param config the abstract configuration from maven plugin
     * @throws java.lang.Exception something gone wrong -> see exception(
     */
    public ExceptionBuilder(Configurate config) throws java.lang.Exception {

        entries = new ArrayList<>();
        File srcPath = retrieveSrcPathFromConfiguration(config);
        File resourcesPath = retrieveResourcePathFromConfiguration(config);

        MessagesType messages = unmarshallXmlFile(config);
        for (MessageType messageType : messages.getMessage()) {
            validateMessageType(config, messageType);
            Entry entry = new Entry();
            entry.setProperties(config.getPropertyFileName());
            entries.add(entry);
            buildEntry(messageType, entry);
            buildException(config, messageType, entry);
        }

        postProcessFindInheritClassName(entries);
        initTemplateEngineInternal();
        buildExceptionsFromTemplate(entries, srcPath);
        buildPropertiesFromTemplate(entries, resourcesPath, config);
    }

    private void postProcessFindInheritClassName(List<Entry> entries) {
        for (Entry entry : entries) {
            Exception exception = entry.getException();
            if (exception != null
                    && exception.getFqClassNameInherit() != null
                    && !exception.getFqClassNameInherit().contains(".")) {
                for (Entry eInterit : entries) {
                    if (eInterit.getException() != null
                            && eInterit.getException().getFqClassName() != null
                            && eInterit.getException().getFqClassName().endsWith(exception.getFqClassNameInherit())) {
                        exception.setFqClassNameInherit(eInterit.getException().getFqClassName());
                    }
                }
            }
        }
    }

    private static String ensureFirstLetterBig(String str) {
        if (str != null && str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    private File retrieveSrcPathFromConfiguration(Configurate config) throws java.lang.Exception {
        File srcPathExceptions = new File(config.getBaseDir(), config.getSrcPath());
        return retrieveFile(srcPathExceptions);
    }

    private File retrieveResourcePathFromConfiguration(Configurate config) throws java.lang.Exception {
        File srcPathExceptions = new File(config.getBaseDir(), config.getResourcesPath());
        return retrieveFile(srcPathExceptions);
    }

    private File retrieveFile(File file) throws java.lang.Exception {
        if (file.isFile()) {
            throw new java.lang.Exception(String.format("At least path %s should not be a file", file));
        }
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        if (!file.isDirectory() && !file.canWrite()) {
            throw new java.lang.Exception(String.format("At least path %s must exists and has to be writable", file));
        }
        return file;
    }

    private void buildEntry(MessageType messageType, Entry entry) {
        entry.setDomain(messageType.getDomain());
        entry.setName(messageType.getName());
        entry.setBackendText(messageType.getBackendMessage());
        SortedSet<Parameter> backEndParams = new TreeSet<>();
        gainParameter(messageType.getBackendMessage(), backEndParams);
        entry.setBackendParameters(new ArrayList<>(backEndParams));
    }

    private void buildException(Configurate config, MessageType messageType, Entry entry) {
        if (messageType.getException() != null) {
            ExceptionType exceptionType = messageType.getException();
            Exception exception = new Exception();
            exception.setPackageName(config.getClassPackageName() == null ? exceptionType.getPackage() : config.getClassPackageName());
            exception.setFqClassName(String.format("%s.%sException", exception.getPackageName(), ensureFirstLetterBig(messageType.getName())));
            exception.setClassName(String.format("%sException", ensureFirstLetterBig(messageType.getName())));
            exception.setFqClassNameInherit(exceptionType.getInherit());
            if (exceptionType.getReturnCode()!= null && exceptionType.getReturnCode().matches("^\\d+$")) {
                exception.setReturnCode(Integer.parseInt(exceptionType.getReturnCode()));
            }
            SortedSet<Parameter> frontEndParams = new TreeSet<>();
            FrontendMessagesType frontendMessages = messageType.getFrontendMessages();
            List<FrontendMessageType> frontendMessage = frontendMessages.getFrontendMessage();
            for (FrontendMessageType frontendMessageType : frontendMessage) {
                gainParameter(frontendMessageType.getValue(), frontEndParams);
                exception.getFrontEndText().add(new Text(frontendMessageType.getLocale(), frontendMessageType.getValue()));
            }
            exception.setFrontEndParameters(new ArrayList<>(frontEndParams));
            entry.setException(exception);
        }
    }

    private void validateMessageType(Configurate config, MessageType messageType) throws java.lang.Exception {
        if (messageType.getDomain() != null && !domainDuplicateChecker.add(messageType.getDomain().toLowerCase())) {
            throw new java.lang.Exception(String.format("Domain %s has a forbidden duplicate in %s", messageType.getDomain(), config.getPathToMessageXml()));
        }
    }

    private MessagesType unmarshallXmlFile(Configurate config) throws java.lang.Exception {
        File configXmlFile = new File(config.getBaseDir(), config.getPathToMessageXml());
        if (!configXmlFile.exists()) {
            throw new java.lang.Exception(String.format("Message xml not found at given position: %s", configXmlFile.getAbsolutePath()));
        }

        JAXBContext jc = JAXBContext.newInstance(MessagesType.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        FileInputStream fis = new FileInputStream(configXmlFile);
        Source source = new StreamSource(fis);
        JAXBElement<MessagesType> unmarshal = (JAXBElement<MessagesType>) unmarshaller.unmarshal(source, MessagesType.class);
        return unmarshal.getValue();
    }

    private void gainParameter(String text, Set<Parameter> result) {
        Pattern p = Pattern.compile("\\{(?<parameter>([^}]*?))\\}");
        Matcher matcher = p.matcher(text);
        while (matcher.find()) {
            Parameter parameter = new Parameter();

            String group = matcher.group("parameter");
            if (group.contains(":")) {
                String[] split = group.split(":");
                parameter.setName(split[0]);
                if (split[1].startsWith("!")) {
                    parameter.setIgnoreI18n(true);
                    parameter.setFq(split[1].substring(1));
                } else {
                    parameter.setFq(split[1]);
                }
            } else {
                parameter.setName(group);
                parameter.setFq("java.lang.String");
            }
            parameter.setTag("\\\\{"+parameter.getName()+":([^}]*?)\\\\}");
            result.add(parameter);
        }
    }


    private void initTemplateEngineInternal() {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        ve.init();
    }

    private void initTemplateEngineExternal() {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        ve.setProperty("file.resource.loader.class",
                FileResourceLoader.class.getName());
        ve.init();
    }

    private void buildExceptionsFromTemplate(List<Entry> entries, File srcTarget) throws java.lang.Exception {

        for (Entry entry : entries) {
            if (entry.getException() != null) {
                InputStream input = ExceptionBuilder.class.getClassLoader()
                        .getResourceAsStream("template/exception.vm");
                if (input == null) {
                    throw new IOException("Template path doesn't exist");
                }
                Exception exc = entry.getException();
                File targetFile = new File(srcTarget, makeJavaFileAppendixFromFQ(exc.getFqClassName()) );
                targetFile.getParentFile().mkdirs();

                VelocityContext context = new VelocityContext();
                context.put("e", entry);

                BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, false));
                ve.evaluate(context, writer, "exception.vm", new InputStreamReader(input));

                writer.flush();
                writer.close();

            }
        }
    }

    private void buildPropertiesFromTemplate(List<Entry> entries, File srcTarget, Configurate config) throws java.lang.Exception {

        Set<String> languages = entries.stream()
                .filter(entry -> entry.getException() != null)
                .map(entry -> entry.getException().getFrontEndText())
                .flatMap(Collection::stream)
                .map(Text::getLocale)
                .collect(Collectors.toSet());

        for (String language : languages) {
            InputStream input = ExceptionBuilder.class.getClassLoader()
                    .getResourceAsStream("template/properties.vm");
            if (input == null) {
                throw new IOException("Template path doesn't exist");
            }

            File targetFile = new File(srcTarget, config.getPropertyFileName() + "_" + language + ".properties");
            targetFile.getParentFile().mkdirs();

            VelocityContext context = new VelocityContext();
            context.put("entries", entries);
            context.put("language", language);

            BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, false));
            ve.evaluate(context, writer, "exception.vm", new InputStreamReader(input));

            writer.flush();
            writer.close();
        }

    }

    private String makeJavaFileAppendixFromFQ(String fq) {
        String replace = String.format("/%s.java", fq.replace(".", "/"));
        return replace;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
