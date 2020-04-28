package com.github.saiprasadkrishnamurthy.databindings.model;

import java.util.HashMap;
import java.util.Optional;

/**
 * A container for all the scanned parsed data elements.
 *
 * @author Sai.
 */
public class DataElements extends HashMap<String, DataElement> {

    /**
     * Adds the data element.
     *
     * @param dataElement
     */
    public void add(final DataElement dataElement) {
        this.put(dataElement.getQualifiedName(), dataElement);
    }

    /**
     * Gets the DataElement
     *
     * @param qualifiedName
     * @return
     */
    public Optional<DataElement> get(final String qualifiedName) {
        return super.get(qualifiedName) == null ? Optional.empty() : Optional.of(super.get(qualifiedName));
    }
}
