package com.github.saiprasadkrishnamurthy.databindings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.*;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class RulesBasedValidatorTest {

    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Mock
    private DataElementsRepository dataElementsRepository;

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new RulesBasedValidator(dataElementsRepository);
    }


    @Test
    void validate() throws Exception {
        DataElement person = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/person.json"), Charset.defaultCharset()), DataElement.class);
        DataElement address = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/address.json"), Charset.defaultCharset()), DataElement.class);
        DataElement employee = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/employee.json"), Charset.defaultCharset()), DataElement.class);
        DataElement engineer = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/engineer.json"), Charset.defaultCharset()), DataElement.class);
        DataElement engineerType = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/engineerType.json"), Charset.defaultCharset()), DataElement.class);
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setValidationRulesFile("src/test/resources/validationrules/rules1.json");
        Map<String, DataElement> expected = ImmutableMap.of("com.foo.data.Person", person,
                "com.foo.data.AddressInfo", address,
                "com.foo.data.Employee", employee,
                "com.foo.data.Engineer", engineer,
                "com.foo.data.EngineerType", engineerType
        );
        DataElements dataElements = new DataElements();
        dataElements.putAll(expected);
        when(dataElementsRepository.getDataElements(dataBindingsGenerationRequest)).thenReturn(dataElements);
        List<ValidationError> errors = validator.validate(dataBindingsGenerationRequest);
        assertThat(errors.size(), is(equalTo(0)));
    }

    @Test
    void validate_with_validation_errors_raised() throws Exception {
        DataElement person = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/person.json"), Charset.defaultCharset()), DataElement.class);
        DataElement address = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/address.json"), Charset.defaultCharset()), DataElement.class);
        DataElement employee = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/employee.json"), Charset.defaultCharset()), DataElement.class);
        DataElement engineer = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/engineer.json"), Charset.defaultCharset()), DataElement.class);
        DataElement engineerType = OBJECTMAPPER.readValue(IOUtils.toString(RulesBasedValidatorTest.class.getClassLoader().getResourceAsStream("input/engineerType.json"), Charset.defaultCharset()), DataElement.class);
        DataBindingsGenerationRequest dataBindingsGenerationRequest = new DataBindingsGenerationRequest();
        dataBindingsGenerationRequest.setValidationRulesFile("src/test/resources/validationrules/rules2.json");
        Map<String, DataElement> expected = ImmutableMap.of("com.foo.data.Person", person,
                "com.foo.data.AddressInfo", address,
                "com.foo.data.Employee", employee,
                "com.foo.data.Engineer", engineer,
                "com.foo.data.EngineerType", engineerType
        );
        DataElements dataElements = new DataElements();
        dataElements.putAll(expected);
        when(dataElementsRepository.getDataElements(dataBindingsGenerationRequest)).thenReturn(dataElements);
        List<ValidationError> errors = validator.validate(dataBindingsGenerationRequest);
        assertThat(errors.size(), is(equalTo(2)));
        assertThat(errors, is(equalTo(Arrays.asList(
                new ValidationError("Engineer", "Engineer definitions must extend from the baseType \"com.foo.data.Employee\"", SeverityType.Critical),
                new ValidationError("Engineer", "Engineer definitions must extend from the baseType \"com.foo.data.Employee\"", SeverityType.Critical)
                ))));
    }
}