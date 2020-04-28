package com.github.saiprasadkrishnamurthy.databindings.model;

import java.util.List;

/**
 * Validator.
 * @author Sai.
 */
public interface Validator {
    List<ValidationError> validate(DataBindingsGenerationRequest dataBindingsGenerationRequest);
}
