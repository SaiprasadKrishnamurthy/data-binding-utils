package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Request modelling the generation of the bindings.
 *
 * @author Sai.
 */
@Data
public class DataBindingsGenerationRequest {
    private String schemasBaseDir;
    private String validationRulesFile;
    private String outputDir = System.getProperty("user.dir");
    private List<String> excludedFileNames = new ArrayList<>();
    private List<DataBindingsType> bindings = Arrays.asList(DataBindingsType.AvroSchema, DataBindingsType.PlainPojo, DataBindingsType.Swagger);

    public List<String> bindingGeneratorNames() {
        return bindings.stream().map(b -> StringUtils.uncapitalize(b.name() + "BindingsGenerator")).collect(Collectors.toList());
    }

    public String getKey() {
        return schemasBaseDir + "_" + excludedFileNames + "_" + bindings;
    }
}
