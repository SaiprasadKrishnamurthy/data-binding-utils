package com.github.saiprasadkrishnamurthy.databindings.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Converts the agnostic schema meta model to avro schema json.
 *
 * @author Sai.
 */
@Service
public class AvroSchemaBindingsGenerator implements DataBindingsGenerator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final DataElementsRepository dataElementsRepository;

    public AvroSchemaBindingsGenerator(final DataElementsRepository dataElementsRepository) {
        this.dataElementsRepository = dataElementsRepository;
    }

    @Override
    public DataBindingsGenerationResponse generate(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            DataBindingsGenerationResponse response = new DataBindingsGenerationResponse();
            Map<String, String> avroSchemas = new HashMap<>();
            DataElements dataElements = dataElementsRepository.getDataElements(dataBindingsGenerationRequest);
            Configuration cfg = new Configuration(new Version("2.3.30"));
            cfg.setClassLoaderForTemplateLoading(AvroSchemaBindingsGenerator.class.getClassLoader(), "templates/avro");
            cfg.setIncompatibleImprovements(new Version(2, 3, 20));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocale(Locale.US);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Template recordTemplate = cfg.getTemplate("avro_record.ftl");
            Template enumerationTemplate = cfg.getTemplate("avro_enumeration.ftl");
            List<String> topLevelContainerTypes = new ArrayList<>();
            dataElements.forEach((key, value) -> {
                        try (StringWriter out = new StringWriter()) {
                            if (value.isTopLevelContainerType()) {
                                topLevelContainerTypes.add(value.getQualifiedName());
                            }
                            if (StringUtils.hasText(value.getBaseType()) && dataElements.get(value.getBaseType()).isPresent()) {
                                gatherAllFields(dataElements.get(value.getBaseType()).get(), value.getFields(), dataElements);
                            }
                            Map<String, Object> templateData = new HashMap<>();
                            templateData.put("dataElement", value);
                            if (value.getType() == DataElementType.object) {
                                recordTemplate.process(templateData, out);
                            } else if (value.getType() == DataElementType.enumeration) {
                                enumerationTemplate.process(templateData, out);
                            }
                            avroSchemas.put(value.getQualifiedName(), out.toString());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            Map<String, String> inlinedAvroSchemas = inlineTypes(avroSchemas);
            inlinedAvroSchemas.entrySet().stream()
                    .filter(entry -> topLevelContainerTypes.contains(entry.getKey()))
                    .map(this::toMap)
                    .forEach(doc -> {
                        try {
                            String name = doc.get("name").toString();
                            String version = doc.get("version").toString();
                            String fileName = name + "_v" + version + ".avsc";
                            File file = new File(dataBindingsGenerationRequest.getOutputDir() + File.separator + fileName);
                            response.getFilesGenerated().add(file.getAbsolutePath());
                            FileUtils.write(file, toString(doc), Charset.defaultCharset());
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    });
            stopWatch.stop();
            response.setOutputDir(dataBindingsGenerationRequest.getOutputDir());
            response.setTotalTimeTakenInSeconds(stopWatch.getTotalTimeSeconds());
            return response;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<String, Object> toMap(final Map.Entry<String, String> entry) {
        try {
            return OBJECT_MAPPER.readValue(entry.getValue(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(final Object doc) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> inlineTypes(final Map<String, String> avroSchemas) {
        Map<String, String> inlinedAvroSchemas = new HashMap<>(avroSchemas);
        avroSchemas.forEach((key, value) -> {
            String[] tokens = org.apache.commons.lang3.StringUtils.substringsBetween(value, "\"", "\"");
            Arrays.stream(tokens)
                    .filter(avroSchemas::containsKey)
                    .forEach(token -> {
                        String inlined = inlinedAvroSchemas.get(key).replace("\"" + token + "\"", avroSchemas.get(token));
                        inlinedAvroSchemas.put(key, inlined);
                    });
        });
        return inlinedAvroSchemas;
    }

    // Recursively gather all the inherited fields.
    private void gatherAllFields(final DataElement dataElement, final List<Field> fields, final DataElements dataElements) {
        dataElement.getFields().stream()
                .filter(f -> !fields.contains(f))
                .forEach(fields::add);
        if (StringUtils.hasText(dataElement.getBaseType()) && dataElements.get(dataElement.getBaseType()).isPresent()) {
            gatherAllFields(dataElements.get(dataElement.getBaseType()).get(), fields, dataElements);
        }
    }
}
