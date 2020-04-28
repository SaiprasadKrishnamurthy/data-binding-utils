package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Response modelling the generation of the bindings.
 *
 * @author Sai.
 */
@Data
public class DataBindingsGenerationResponse {
    private String outputDir;
    private double totalTimeTakenInSeconds;
    private List<String> filesGenerated = new ArrayList<>();
}
