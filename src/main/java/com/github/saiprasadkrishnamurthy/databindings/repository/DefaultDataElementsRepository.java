package com.github.saiprasadkrishnamurthy.databindings.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.DataBindingsGenerationRequest;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElement;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElements;
import com.github.saiprasadkrishnamurthy.databindings.model.DataElementsRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of DataElementsRepository that looks up the base directory and loads all the DataElements recursively.
 *
 * @author Sai.
 */
@Repository
public class DefaultDataElementsRepository implements DataElementsRepository {

    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Override
    public DataElements getDataElements(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        return scanDataElements(dataBindingsGenerationRequest);
    }

    private DataElements scanDataElements(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        DataElements dataElements = new DataElements();
        try {
            Files.walk(Paths.get(dataBindingsGenerationRequest.getSchemasBaseDir()))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith("json"))
                    .filter(file -> !dataBindingsGenerationRequest.getExcludedFileNames().contains(file.getName()))
                    .map(file -> {
                        try {
                            DataElement dataElement = OBJECTMAPPER.readValue(new String(Files.readAllBytes(file.toPath())), DataElement.class);
                            dataElement.setFileName(file.getAbsolutePath());
                            return dataElement;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .forEach(dataElement -> dataElements.put(dataElement.getQualifiedName(), dataElement));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return dataElements;
    }
}
