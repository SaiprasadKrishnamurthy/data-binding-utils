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
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class AvroSchemaBindingsGeneratorTest {

    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Mock
    private DataElementsRepository dataElementsRepository;

    private AvroSchemaBindingsGenerator avroSchemaBindingsGenerator;

    @BeforeEach
    void setUp() {
        avroSchemaBindingsGenerator = new AvroSchemaBindingsGenerator(dataElementsRepository);
    }

    @Test
    void generate() throws Exception {
            DataElement person = OBJECTMAPPER.readValue(IOUtils.toString(AvroSchemaBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("input/person.json"), Charset.defaultCharset()), DataElement.class);
            DataElement address = OBJECTMAPPER.readValue(IOUtils.toString(AvroSchemaBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("input/address.json"), Charset.defaultCharset()), DataElement.class);
            DataElement employee = OBJECTMAPPER.readValue(IOUtils.toString(AvroSchemaBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("input/employee.json"), Charset.defaultCharset()), DataElement.class);
            DataElement engineer = OBJECTMAPPER.readValue(IOUtils.toString(AvroSchemaBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("input/engineer.json"), Charset.defaultCharset()), DataElement.class);
            DataElement engineerType = OBJECTMAPPER.readValue(IOUtils.toString(AvroSchemaBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("input/engineerType.json"), Charset.defaultCharset()), DataElement.class);
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
        Map<String, DataElement> expected = ImmutableMap.of("com.foo.data.Person", person,
                "com.foo.data.AddressInfo", address,
                "com.foo.data.Employee", employee,
                "com.foo.data.Engineer", engineer,
                "com.foo.data.EngineerType", engineerType
        );
        DataElements dataElements = new DataElements();
        dataElements.putAll(expected);
        when(dataElementsRepository.getDataElements(dataBindingsGenerationRequest)).thenReturn(dataElements);
        avroSchemaBindingsGenerator.generate(dataBindingsGenerationRequest);
        assertThat(Paths.get(tmp.getAbsolutePath(), "Engineer_V1.avsc").toFile().exists(), equalTo(true));
        assertThat(FileUtils.readFileToString(Paths.get(tmp.getAbsolutePath(), "Engineer_V1.avsc").toFile(), Charset.defaultCharset()),
                equalTo(IOUtils.toString(AvroSchemaBindingsGeneratorTest.class.getClassLoader().getResourceAsStream("output/Engineer_V1.avsc"), Charset.defaultCharset())));
    }
}