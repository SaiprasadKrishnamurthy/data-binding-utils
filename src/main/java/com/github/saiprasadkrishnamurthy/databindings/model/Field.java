package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Canonical representation of a field.
 *
 * @author Sai.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field {
    private String name;
    private String type;
    private String documentation = "TODO";
    private boolean required = true;
    private boolean array;
}
