package com.github.saiprasadkrishnamurthy.databindings.service;

import com.github.saiprasadkrishnamurthy.databindings.model.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

/**
 * Converts the agnostic schema meta model to Swagger API pojos.
 *
 * @author Sai.
 */
@Service
public class SwaggerBindingsGenerator implements DataBindingsGenerator {

    private final DataElementsRepository dataElementsRepository;

    public SwaggerBindingsGenerator(final DataElementsRepository dataElementsRepository) {
        this.dataElementsRepository = dataElementsRepository;
    }

    @Override
    public DataBindingsGenerationResponse generate(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            DataBindingsGenerationResponse response = new DataBindingsGenerationResponse();
            DataElements dataElements = dataElementsRepository.getDataElements(dataBindingsGenerationRequest);
            Configuration cfg = new Configuration(new Version("2.3.30"));
            cfg.setClassLoaderForTemplateLoading(SwaggerBindingsGenerator.class.getClassLoader(), "templates/swagger");
            cfg.setIncompatibleImprovements(new Version(2, 3, 20));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocale(Locale.US);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Template recordTemplate = cfg.getTemplate("swagger_class.ftl");
            Template enumerationTemplate = cfg.getTemplate("swagger_enumeration.ftl");
            List<JavaType> javaTypes = new ArrayList<>();
            dataElements.forEach((key, value) -> {
                        try (StringWriter out = new StringWriter()) {
                            Map<String, Object> templateData = new HashMap<>();
                            templateData.put("dataElement", value);
                            if (value.getType() == DataElementType.object) {
                                recordTemplate.process(templateData, out);
                            } else if (value.getType() == DataElementType.enumeration) {
                                enumerationTemplate.process(templateData, out);
                            }
                            javaTypes.add(new JavaType(value.getNamespace(), value.getName(), out.toString()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            // Dump the java types into the file system.
            javaTypes.forEach(jt -> {
                try {
                    File pkg = new File(dataBindingsGenerationRequest.getOutputDir() + File.separator + jt.getNamespace().replace(".", File.separator));
                    FileUtils.forceMkdirParent(pkg);
                    FileUtils.writeStringToFile(Paths.get(pkg.getAbsolutePath(), jt.getName() + ".java").toFile(), jt.getContents(), Charset.defaultCharset());
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
}
