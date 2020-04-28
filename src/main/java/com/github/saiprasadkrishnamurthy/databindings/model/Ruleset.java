package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Ruleset model.
 *
 * @author Sai.
 */
@Data
public class Ruleset {
    private String name;
    private String description;
    private List<Rule> validationRules = new ArrayList<>();
}
