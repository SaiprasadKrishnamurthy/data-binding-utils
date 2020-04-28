package com.github.saiprasadkrishnamurthy.databindings.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.DataBindingsGenerationRequest;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElement;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElements;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElementsRepository;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.collection.IsIn.in;

class DefaultDataElementsRepositoryTest {

    private DataElementsRepository dataElementsRepository = new DefaultDataElementsRepository();
    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Test
    void getDataElements() throws Exception {
        DataElement person = OBJECTMAPPER.readValue(IOUtils.toString(DefaultDataElementsRepositoryTest.class.getClassLoader().getResourceAsStream("input/person.json"), Charset.defaultCharset()), DataElement.class);
        DataElement address = OBJECTMAPPER.readValue(IOUtils.toString(DefaultDataElementsRepositoryTest.class.getClassLoader().getResourceAsStream("input/address.json"), Charset.defaultCharset()), DataElement.class);
        DataElement employee = OBJECTMAPPER.readValue(IOUtils.toString(DefaultDataElementsRepositoryTest.class.getClassLoader().getResourceAsStream("input/employee.json"), Charset.defaultCharset()), DataElement.class);
        DataElement engineer = OBJECTMAPPER.readValue(IOUtils.toString(DefaultDataElementsRepositoryTest.class.getClassLoader().getResourceAsStream("input/engineer.json"), Charset.defaultCharset()), DataElement.class);
        DataElement engineerType = OBJECTMAPPER.readValue(IOUtils.toString(DefaultDataElementsRepositoryTest.class.getClassLoader().getResourceAsStream("input/engineerType.json"), Charset.defaultCharset()), DataElement.class);
        person.setFileName(new File("src/test/resources/input/person.json").getAbsolutePath());
        address.setFileName(new File("src/test/resources/input/address.json").getAbsolutePath());
        engineer.setFileName(new File("src/test/resources/input/engineer.json").getAbsolutePath());
        employee.setFileName(new File("src/test/resources/input/employee.json").getAbsolutePath());
        engineerType.setFileName(new File("src/test/resources/input/engineerType.json").getAbsolutePath());
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setSchemasBaseDir("src/test/resources/input");
        DataElements dataElements = dataElementsRepository.getDataElements(dataBindingsGenerationRequest);
        Map<String, DataElement> expected = ImmutableMap.of("com.foo.data.Person", person,
                "com.foo.data.AddressInfo", address,
                "com.foo.data.Employee", employee,
                "com.foo.data.Engineer", engineer,
                "com.foo.data.EngineerType", engineerType
        );
        assertThat(dataElements.entrySet(), everyItem(is(in(expected.entrySet()))));
    }
}