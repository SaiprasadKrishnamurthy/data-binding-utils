package com.github.saiprasadkrishnamurthy.databindings.model;

/**
 * Data elements repository.
 *
 * @author Sai.
 */
public interface DataElementsRepository {
    DataElements getDataElements(DataBindingsGenerationRequest dataBindingsGenerationRequest);
}
