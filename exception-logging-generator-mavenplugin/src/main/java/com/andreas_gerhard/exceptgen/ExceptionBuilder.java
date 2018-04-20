package com.andreas_gerhard.exceptgen;

import com.andreas_gerhard.exceptgen.vo.*;
import com.andreas_gerhard.exceptgen.vo.Exception;
import com.andreasgerhard.exceptgen.messages.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExceptionBuilder {

    private final List<Entry> entries;
    private VelocityEngine ve;
    private Set<String> domainDuplicateChecker = new HashSet<>();

    /**
     * Generates the needed classes, given by the configuration of th emaven plugin.
     *
     * @param config the abstract configuration from maven plugin
     * @throws java.lang.Exception something gone wrong -> see exception(
     */
    public ExceptionBuilder(Configurate config) throws java.lang.Exception {

        entries = new ArrayList<>();
        File srcPath = retrieveSrcPathFromConfiguration(config);
        File resourcesPath = retrieveResourcePathFromConfiguration(config);

        MessagesType messages = unmarshallXmlFile(config);
        MasterException masterException = retrieveMasterException(config, messages);

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

        if (masterException != null) {
            buildMasterExceptionsFromTemplate(srcPath, masterException, config);
        }
        buildExceptionsFromTemplate(entries, srcPath, masterException);
        buildPropertiesFromTemplate(entries, resourcesPath, config);
    }

    private static String ensureFirstLetterBig(String str) {
        if (str != null && str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    private MasterException retrieveMasterException(Configurate config, MessagesType messages) {
        MasterException masterException = null;
        if (messages.getMasterException() != null) {
            masterException = new MasterException();
            masterException.setMasterInheritClassName(messages.getMasterExceptionInherit());
            if (messages.getMasterException().contains(".")) {
                String className = messages.getMasterException().substring(messages.getMasterException().lastIndexOf(".") + 1);
                String packageName = messages.getMasterException().substring(0, messages.getMasterException().lastIndexOf("."));
                masterException.setMasterClassName(className);
                masterException.setMasterPackageName(packageName);
            } else {
                masterException.setMasterClassName(messages.getMasterException());
                masterException.setMasterPackageName(config.getClassPackageName());
            }
        }
        return masterException;
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
            exception.setPackageName(exceptionType.getPackage() == null || exceptionType.getPackage().isEmpty() ? config.getClassPackageName() : exceptionType.getPackage());
            exception.setFqClassName(String.format("%s.%sException", exception.getPackageName(), ensureFirstLetterBig(messageType.getName())));
            exception.setClassName(String.format("%sException", ensureFirstLetterBig(messageType.getName())));
            exception.setFqClassNameInherit(exceptionType.getInherit());
            if (exceptionType.getReturnCode() != null && exceptionType.getReturnCode().matches("^\\d+$")) {
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
            parameter.setTag("\\\\{" + parameter.getName() + ":([^}]*?)\\\\}");
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

    private void buildExceptionsFromTemplate(List<Entry> entries, File srcTarget, MasterException masterException) throws java.lang.Exception {

        for (Entry entry : entries) {
            if (entry.getException() != null) {
                InputStream input = ExceptionBuilder.class.getClassLoader()
                        .getResourceAsStream("template/exception.vm");
                if (input == null) {
                    throw new IOException("Template path doesn't exist");
                }
                Exception exc = entry.getException();
                File targetFile = new File(srcTarget, makeJavaFileAppendixFromFQ(exc.getFqClassName()));
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

    private void buildMasterExceptionsFromTemplate(File srcTarget, MasterException masterException, Configurate config) throws java.lang.Exception {

        InputStream input = ExceptionBuilder.class.getClassLoader()
                .getResourceAsStream("template/masterexception.vm");
        if (input == null) {
            throw new IOException("Template path doesn't exist");
        }

        File targetFile = new File(srcTarget, makeJavaFileAppendixFromFQ(masterException.getMasterPackageName() + "." + masterException.getMasterClassName()));
        targetFile.getParentFile().mkdirs();

        VelocityContext context = new VelocityContext();
        context.put("e", masterException);

        OutputStreamWriter streamWriter = new OutputStreamWriter(
                new FileOutputStream(targetFile),
                Charset.forName(config.getEncoding()).newEncoder()
        );

        BufferedWriter writer = new BufferedWriter(streamWriter);
        ve.evaluate(context, writer, "masterexception.vm", new InputStreamReader(input));

        writer.flush();
        writer.close();
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

            OutputStreamWriter streamWriter = new OutputStreamWriter(
                    new FileOutputStream(targetFile),
                    Charset.forName("ISO-8859-1").newEncoder()
            );
            BufferedWriter writer = new BufferedWriter(streamWriter);
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
