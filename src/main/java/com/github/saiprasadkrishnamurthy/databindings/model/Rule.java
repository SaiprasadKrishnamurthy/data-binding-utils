package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.Data;

/**
 * A Rule model.
 *
 * @author Sai.
 */
@Data
public class Rule {
    private String name;
    private String description;
    private String errorCondition;
    private SeverityType severity = SeverityType.Major;

}
