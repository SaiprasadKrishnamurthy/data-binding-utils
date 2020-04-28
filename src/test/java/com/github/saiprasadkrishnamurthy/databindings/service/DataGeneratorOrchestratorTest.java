package com.github.saiprasadkrishnamurthy.databindings.service;

import com.github.saiprasadkrishnamurthy.databindings.model.DataBindingsGenerationRequest;
import com.github.saiprasadkrishnamurthy.databindings.model.DataBindingsType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class DataGeneratorOrchestratorTest {

    @Autowired
    private DataGeneratorOrchestrator dataGeneratorOrchestrator;

    @Test
    void generate_with_no_validation_error() {
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setValidationRulesFile("src/test/resources/validationrules/rules1.json");
        dataBindingsGenerationRequest.setSchemasBaseDir("src/test/resources/input");
        File tmp = new File(FileUtils.getTempDirectory().getAbsolutePath() + "/" + System.currentTimeMillis());
        dataBindingsGenerationRequest.setOutputDir(tmp.getAbsolutePath());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.deleteDirectory(tmp);
            } catch (IOException ignore) {
            }
        }));
        dataBindingsGenerationRequest.setBindings(Collections.singletonList(DataBindingsType.AvroSchema));
        dataGeneratorOrchestrator.generate(dataBindingsGenerationRequest);
        assertThat(Paths.get(tmp.getAbsolutePath(), "Engineer_V1.avsc").toFile().exists(), equalTo(true));
    }

    @Test
    void generate_with_validation_error() {
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setValidationRulesFile("src/test/resources/validationrules/rules2.json");
        dataBindingsGenerationRequest.setSchemasBaseDir("src/test/resources/input");
        File tmp = new File(FileUtils.getTempDirectory().getAbsolutePath() + "/" + System.currentTimeMillis());
        dataBindingsGenerationRequest.setOutputDir(tmp.getAbsolutePath());
        dataBindingsGenerationRequest.setBindings(Collections.singletonList(DataBindingsType.AvroSchema));
        dataGeneratorOrchestrator.generate(dataBindingsGenerationRequest);
        assertThat(Paths.get(tmp.getAbsolutePath(), "Engineer_V1.avsc").toFile().exists(), equalTo(false));
        assertThat(Paths.get(tmp.getAbsolutePath(), "validation_errors.json").toFile().exists(), equalTo(true));
    }
}