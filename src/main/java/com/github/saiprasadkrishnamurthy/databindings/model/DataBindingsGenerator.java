package com.github.saiprasadkrishnamurthy.databindings.model;

/**
 * Schema bindings generator.
 *
 * @author Sai.
 */
public interface DataBindingsGenerator {
    DataBindingsGenerationResponse generate(DataBindingsGenerationRequest dataBindingsGenerationRequest);
}
