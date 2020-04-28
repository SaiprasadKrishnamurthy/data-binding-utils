package com.github.saiprasadkrishnamurthy.databindings.service;

import com.github.saiprasadkrishnamurthy.databindings.model.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Converts the agnostic schema meta model to dependency graph.
 *
 * @author Sai.
 */
@Service
public class DependencyGraphBindingsGenerator implements DataBindingsGenerator {

    private final DataElementsRepository dataElementsRepository;

    public DependencyGraphBindingsGenerator(final DataElementsRepository dataElementsRepository) {
        this.dataElementsRepository = dataElementsRepository;
    }

    @Override
    public DataBindingsGenerationResponse generate(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        StopWatch stopWatch = new StopWatch();
        DataBindingsGenerationResponse response = new DataBindingsGenerationResponse();
        stopWatch.start();
        try {
            DataElements dataElements = dataElementsRepository.getDataElements(dataBindingsGenerationRequest);
            List<Node> nodes = new ArrayList<>();
            List<Edge> edges = new ArrayList<>();
            MutableInt index = new MutableInt();
            dataElements.forEach((key, value) -> {
                Node node = new Node(index.incrementAndGet(), key);
                if (!nodes.contains(node)) {
                    nodes.add(node);
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(value.getBaseType())) {
                    Node parent = new Node(index.incrementAndGet(), value.getBaseType());
                    if (!nodes.contains(parent)) {
                        nodes.add(parent);
                    }
                    Edge edge = new Edge(nodes.get(nodes.indexOf(parent)).getId(), nodes.get(nodes.indexOf(node)).getId(), "inherits");
                    if (!edges.contains(edge)) {
                        edges.add(edge);
                    }
                }
                value.getFields().stream()
                        .filter(f -> !f.isAJavaType())
                        .forEach(field -> {
                            Node ref = new Node(index.incrementAndGet(), field.getType());
                            if (!nodes.contains(ref)) {
                                nodes.add(ref);
                            }
                            Edge edge = new Edge(nodes.get(nodes.indexOf(ref)).getId(), nodes.get(nodes.indexOf(node)).getId(), "refers");
                            if (!edges.contains(edge)) {
                                edges.add(edge);
                            }
                        });
            });
            Configuration cfg = new Configuration(new Version("2.3.30"));
            cfg.setClassLoaderForTemplateLoading(DependencyGraphBindingsGenerator.class.getClassLoader(), "templates/dependencygraph");
            cfg.setIncompatibleImprovements(new Version(2, 3, 20));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocale(Locale.US);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            try (StringWriter out = new StringWriter()) {
                Template recordTemplate = cfg.getTemplate("dependency_graph.ftl");
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("nodes", nodes);
                templateData.put("edges", edges);
                recordTemplate.process(templateData, out);
                File file = new File(dataBindingsGenerationRequest.getOutputDir() + File.separator + "DependencyGraph.html");
                response.getFilesGenerated().add(file.getAbsolutePath());
                FileUtils.write(file, out.toString(), Charset.defaultCharset());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            stopWatch.stop();
            response.setTotalTimeTakenInSeconds(stopWatch.getTotalTimeSeconds());
        }
        return response;
    }
}
