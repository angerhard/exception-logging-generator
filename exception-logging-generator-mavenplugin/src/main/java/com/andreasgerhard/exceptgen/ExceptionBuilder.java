package com.andreasgerhard.exceptgen;

import com.andreasgerhard.exceptgen.messages.*;
import com.andreasgerhard.exceptgen.vo.Entry;
import com.andreasgerhard.exceptgen.vo.Exception;
import com.andreasgerhard.exceptgen.vo.Parameter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionBuilder {

    private Set<String> domainDuplicateChecker = new HashSet<>();

    public ExceptionBuilder(Configurate config) throws java.lang.Exception {

        List<Entry> entries = new ArrayList<>();

        MessagesType messages = unmarshallXmlFile(config);
        for (MessageType messageType : messages.getMessage()) {
            validateMessageType(config, messageType);
            Entry entry = new Entry();
            entries.add(entry);
            buildEntry(messageType, entry);
            buildException(config, messageType);
        }
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

    private static String ensureFirstLetterBig(String str) {
        if (str != null && str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    private void gainParameter(String text, Set<Parameter> result) {
        Pattern p = Pattern.compile("\\{(?<parameter>.*)\\}");
        Matcher matcher = p.matcher(text);
        while(matcher.find()) {
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

}
