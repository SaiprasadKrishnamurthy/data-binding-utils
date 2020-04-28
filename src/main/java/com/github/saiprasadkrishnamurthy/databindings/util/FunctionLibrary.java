package com.github.saiprasadkrishnamurthy.databindings.util;

import com.github.saiprasadkrishnamurthy.databindings.model.SeverityType;
import com.github.saiprasadkrishnamurthy.databindings.model.ValidationError;

import java.util.List;

/**
 * Function Library used by the rule engine.
 *
 * @author Sai.
 */
public class FunctionLibrary {
    //.. define as per your needs and reference it like fn.metod_name(...) in your rule condition expressions.
    public void addError(final List<ValidationError> errors, final String file, final String message, final String severityType) {
        errors.add(new ValidationError(file, message, SeverityType.valueOf(severityType)));
    }
}
