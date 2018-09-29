package de.andreasgerhard.exceptgen;

import de.andreasgerhard.exceptgen.model.Entry;

import java.util.List;
import java.util.Optional;

public class TestTool {

    /**
     * Returns given domain found in the given list of entries.
     * @param domain  domain like "CUSTOMER.001"
     * @param entries processed messages from xml file
     * @return found entry or null
     */
    public static Optional<Entry> retrieveEntry(String domain, List<Entry> entries) {

        for (Entry entry : entries) {
            if (entry.getDomain().equals(domain)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

}
