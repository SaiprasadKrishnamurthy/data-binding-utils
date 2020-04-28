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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class DataGeneratorOrchestratorTest {

    @Autowired
    private DataGeneratorOrchestrator dataGeneratorOrchestrator;

    private static final int TIMEOUT_SECONDS = 10;

    private final CountDownLatch countDownLatch = new CountDownLatch(TIMEOUT_SECONDS);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    void generate_with_no_validation_error() throws Exception {
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setValidationRulesFile("src/test/resources/validationrules/rules1.json");
        dataBindingsGenerationRequest.setSchemasBaseDir("src/test/resources/input");
        File tmp = new File("generated_" + System.currentTimeMillis());
        FileUtils.forceMkdir(tmp);
        dataBindingsGenerationRequest.setOutputDir(tmp.getAbsolutePath());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.deleteDirectory(tmp);
            } catch (IOException ignore) {
            }
        }));
        dataBindingsGenerationRequest.setBindings(Collections.singletonList(DataBindingsType.AvroSchema));
        dataGeneratorOrchestrator.generate(dataBindingsGenerationRequest);
        waitUntilFileExists(tmp);
        assertThat(Paths.get(tmp.getAbsolutePath(), "Engineer_V1.avsc").toFile().exists(), equalTo(true));
    }

    private void waitUntilFileExists(File tmp) throws Exception {
        executorService.submit(() -> {
            while (true) {
                if (tmp.exists() && tmp.list() != null && tmp.list().length > 0) {
                    IntStream.range(0, TIMEOUT_SECONDS)
                            .forEach(i -> countDownLatch.countDown());
                }
                Thread.sleep(TIMEOUT_SECONDS);
            }
        });
        countDownLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    void generate_with_validation_error() throws Exception {
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setValidationRulesFile("src/test/resources/validationrules/rules2.json");
        dataBindingsGenerationRequest.setSchemasBaseDir("src/test/resources/input");
        File tmp = new File("generated_" + System.currentTimeMillis());
        FileUtils.forceMkdir(tmp);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.deleteDirectory(tmp);
            } catch (IOException ignore) {
            }
        }));
        dataBindingsGenerationRequest.setOutputDir(tmp.getAbsolutePath());
        dataBindingsGenerationRequest.setBindings(Collections.singletonList(DataBindingsType.AvroSchema));
        dataGeneratorOrchestrator.generate(dataBindingsGenerationRequest);
        waitUntilFileExists(tmp);
        assertThat(Paths.get(tmp.getAbsolutePath(), "Engineer_V1.avsc").toFile().exists(), equalTo(false));
        assertThat(Paths.get(tmp.getAbsolutePath(), "validation_errors.json").toFile().exists(), equalTo(true));
    }
}