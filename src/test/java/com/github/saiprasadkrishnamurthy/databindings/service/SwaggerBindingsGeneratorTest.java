package com.github.saiprasadkrishnamurthy.databindings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.DataBindingsGenerationRequest;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElement;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElements;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElementsRepository;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class SwaggerBindingsGeneratorTest {

    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Mock
    private DataElementsRepository dataElementsRepository;

    private SwaggerBindingsGenerator swaggerBindingsGenerator;

    @BeforeEach
    void setUp() {
        swaggerBindingsGenerator = new SwaggerBindingsGenerator(dataElementsRepository);
    }

    @Test
    void generate() throws Exception {
        DataElement engineer = OBJECTMAPPER.readValue(IOUtils.toString(SwaggerBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("input/engineer.json"), Charset.defaultCharset()), DataElement.class);
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setSchemasBaseDir("src/test/resources/input");
        File tmp = new File(FileUtils.getTempDirectory().getAbsolutePath() + "/" + System.currentTimeMillis());
        dataBindingsGenerationRequest.setOutputDir(tmp.getAbsolutePath());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.deleteDirectory(tmp);
            } catch (IOException ignore) {
            }
        }));
        Map<String, DataElement> expected = ImmutableMap.of(
                "com.foo.data.Engineer", engineer
        );
        DataElements dataElements = new DataElements();
        dataElements.putAll(expected);
        when(dataElementsRepository.getDataElements(dataBindingsGenerationRequest)).thenReturn(dataElements);
        swaggerBindingsGenerator.generate(dataBindingsGenerationRequest);
        assertThat(Paths.get(tmp.getAbsolutePath(), "com", "foo", "data", "Engineer.java").toFile().exists(), equalTo(true));
        List<String> actualFileLines = FileUtils.readLines(Paths.get(tmp.getAbsolutePath(), "com", "foo", "data", "Engineer.java").toFile(), Charset.defaultCharset());
        List<String> expectedFileLines = Arrays.asList(IOUtils.toString(SwaggerBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("output/EngineerSwagger.java"), Charset.defaultCharset()).split("\n"));
        assertThat(actualFileLines.stream().map(String::trim).collect(toList()), equalTo(expectedFileLines.stream().map(String::trim).collect(toList())));
    }
}