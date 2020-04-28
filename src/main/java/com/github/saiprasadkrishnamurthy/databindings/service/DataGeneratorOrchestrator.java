package com.github.saiprasadkrishnamurthy.databindings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data generator orchestration.
 *
 * @author Sai.
 */
@Slf4j
@Service
public class DataGeneratorOrchestrator {

    private final ApplicationContext applicationContext;

    public DataGeneratorOrchestrator(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void generate(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        try {
            Validator validator = applicationContext.getBean(Validator.class);
            List<ValidationError> errors = validator.validate(dataBindingsGenerationRequest);
            if (errors.isEmpty()) {
                Map<String, DataBindingsGenerator> generators = applicationContext.getBeansOfType(DataBindingsGenerator.class);
                List<DataBindingsGenerator> gens = dataBindingsGenerationRequest.bindingGeneratorNames().stream()
                        .filter(generators::containsKey)
                        .map(generators::get)
                        .collect(Collectors.toList());
                gens.forEach(g -> {
                    DataBindingsGenerationResponse response = g.generate(dataBindingsGenerationRequest);

                });
            } else {
                log.error(" **************** Validation Errors ****************** ");
                File file = new File(dataBindingsGenerationRequest.getOutputDir() + File.separator + "validation_errors.json");
                FileUtils.write(file,
                        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(errors), Charset.defaultCharset());
                log.error(" **************** Validation Errors Reported in ****************** " + file.getAbsolutePath());
                log.warn(" **************** Bindings not generated ****************** ");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }


}
