package com.andreas_gerhard.exceptgen;

import com.andreas_gerhard.exceptgen.vo.Entry;
import com.andreas_gerhard.exceptgen.vo.Exception;
import com.andreas_gerhard.exceptgen.vo.Parameter;
import com.andreasgerhard.exceptgen.messages.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionBuilder {

    private VelocityEngine ve;

    private Set<String> domainDuplicateChecker = new HashSet<>();

    /**
     * Generates the needed classes, given by the configuration of th emaven plugin.
     * @param config the abstract configuration from maven plugin
     * @throws java.lang.Exception something gone wrong -> see exception(
     */
    public ExceptionBuilder(Configurate config) throws java.lang.Exception {

        List<Entry> entries = new ArrayList<>();
        File srcPath = retrieveSrcPathFromConfiguration(config.getSrcPath());

        MessagesType messages = unmarshallXmlFile(config);
        for (MessageType messageType : messages.getMessage()) {
            validateMessageType(config, messageType);
            Entry entry = new Entry();
            entries.add(entry);
            buildEntry(messageType, entry);
            buildException(config, messageType);
        }

        postProcessFindInheritClassName(entries);
        buildExceptionsFromTemplate(entries, srcPath);
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

    private File retrieveSrcPathFromConfiguration(String srcPathForExceptions) throws java.lang.Exception {
        File srcPathExceptions = new File(srcPathForExceptions);
        if (!srcPathExceptions.isDirectory() && !srcPathExceptions.canWrite()) {
            throw new java.lang.Exception(String.format("At least path %s must exists and has to be writable", srcPathForExceptions));
        }
        return srcPathExceptions;
    }

    private void buildEntry(MessageType messageType, Entry entry) {
        entry.setDomain(messageType.getDomain());
        entry.setName(messageType.getName());
        entry.setBackendText(messageType.getBackendMessage());
        SortedSet<Parameter> backEndParams = new TreeSet<>();
        gainParameter(messageType.getBackendMessage(), backEndParams);
        entry.setBackendParameters(new ArrayList<>(backEndParams));
    }

    private void buildException(Configurate config, MessageType messageType) {
        if (messageType.getException() != null) {
            ExceptionType exceptionType = messageType.getException();
            Exception exception = new Exception();
            exception.setPackageName(config.getClassPackageName() == null ? exceptionType.getPackage() : config.getClassPackageName());
            exception.setFqClassName(exception.getPackageName() + ensureFirstLetterBig(messageType.getName()));
            exception.setFqClassNameInherit(exception.getFqClassNameInherit());
            SortedSet<Parameter> frontEndParams = new TreeSet<>();
            FrontendMessagesType frontendMessages = messageType.getFrontendMessages();
            List<FrontendMessageType> frontendMessage = frontendMessages.getFrontendMessage();
            for (FrontendMessageType frontendMessageType : frontendMessage) {
                gainParameter(frontendMessageType.getValue(), frontEndParams);
            }
            exception.setFrontEndParameters(new ArrayList<>(frontEndParams));
        }
    }

    private void validateMessageType(Configurate config, MessageType messageType) throws java.lang.Exception {
        if (messageType.getDomain() != null && !domainDuplicateChecker.add(messageType.getDomain().toLowerCase())) {
            throw new java.lang.Exception(String.format("Domain %s has a forbidden duplicate in %s", messageType.getDomain(), config.getPathToMessageXml()));
        }
    }

    private MessagesType unmarshallXmlFile(Configurate config) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(MessagesType.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (MessagesType) unmarshaller.unmarshal(new File(config.getPathToMessageXml()));
    }

    private void gainParameter(String text, Set<Parameter> result) {
        Pattern p = Pattern.compile("\\{(?<parameter>.*)\\}");
        Matcher matcher = p.matcher(text);
        while (matcher.find()) {
            Parameter parameter = new Parameter();
            result.add(parameter);
            String group = matcher.group();
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

        InputStream input = ExceptionBuilder.class.getClassLoader()
                .getResourceAsStream("/template/exception.vm");
        if (input == null) {
            throw new IOException("Template file exception.vm doesn't exist");
        }

        for (Entry entry : entries) {
            if (entry.getException() != null) {
                Exception exc = entry.getException();
                File targetFile = new File(srcTarget, makeJavaFileAppendixFromFQ(exc.getFqClassName()));
                targetFile.getParentFile().mkdirs();

                VelocityContext context = new VelocityContext();
                context.put("entries", entries);

                Template template = ve.getTemplate("/template/exception.vm", "UTF-8");
                BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
                template.merge(context, writer);
                writer.flush();
                writer.close();

            }
        }

    }

    private String makeJavaFileAppendixFromFQ(String fq) {
        String replace = String.format("/%s.java", fq.replace(".", "/"));
        return replace;
    }



}
