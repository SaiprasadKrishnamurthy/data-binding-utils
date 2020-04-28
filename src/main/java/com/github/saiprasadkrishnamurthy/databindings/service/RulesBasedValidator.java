package com.github.saiprasadkrishnamurthy.databindings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.databindings.model.*;
import com.github.saiprasadkrishnamurthy.databindings.util.FunctionLibrary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.AbstractRulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.JsonRuleDefinitionReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * A Simple Rule Engine based validator.
 *
 * @author Sai.
 */
@Slf4j
@Service
public class RulesBasedValidator implements Validator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final DataElementsRepository dataElementsRepository;

    public RulesBasedValidator(final DataElementsRepository dataElementsRepository) {
        this.dataElementsRepository = dataElementsRepository;
    }

    @Override
    public List<ValidationError> validate(final DataBindingsGenerationRequest dataBindingsGenerationRequest) {
        try {
            if (StringUtils.isBlank(dataBindingsGenerationRequest.getValidationRulesFile())) {
                return Collections.emptyList();
            }
            DataElements dataElements = dataElementsRepository.getDataElements(dataBindingsGenerationRequest);
            // Transform the rules to an internal model that the easyrules expects.
            String rulesJson = FileUtils.readFileToString(new File(dataBindingsGenerationRequest.getValidationRulesFile()), Charset.defaultCharset());
            Ruleset ruleset = OBJECT_MAPPER.readValue(rulesJson, Ruleset.class);
            MVELRuleFactory ruleFactory = new MVELRuleFactory(new JsonRuleDefinitionReader());
            Rules rules = new Rules();
            internalRuleModel(ruleset, ruleFactory).forEach(rules::register);
            return dataElements.values().stream()
                    .map(de -> evaluateRule(rules, de)).filter(errors -> !errors.isEmpty())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error while validation ", ex);
            throw new RuntimeException(ex);
        }
    }

    private List<ValidationError> evaluateRule(final Rules rules, final DataElement de) {
        try {
            Facts facts = new Facts();
            facts.put("doc", de);
            facts.put("file", de.getFileName());
            facts.put("errors", new ArrayList<ValidationError>());
            facts.put("fn", new FunctionLibrary());
            // fire rules on known facts
            AbstractRulesEngine rulesEngine = new DefaultRulesEngine();
            rulesEngine.fire(rules, facts);
            return (List<ValidationError>) facts.get("errors");
        } catch (Exception ex) {
            log.error("Error while evaluating a rule ", ex);
            throw new RuntimeException(ex);
        }
    }

    private List<Rule> internalRuleModel(final Ruleset ruleset, final MVELRuleFactory mvelRuleFactory) {
        MutableInt index = new MutableInt();
        return ruleset.getValidationRules().stream()
                .map(rule -> {
                    Map<String, Object> r = new HashMap<>();
                    r.put("name", rule.getName());
                    r.put("description", rule.getDescription());
                    r.put("condition", rule.getErrorCondition());
                    r.put("priority", index.incrementAndGet());
                    r.put("actions", singletonList("fn.addError(errors, file, '" + rule.getDescription() + "', '" + rule.getSeverity() + "')"));
                    return r;
                })
                .map(r -> {
                    try {
                        return mvelRuleFactory.createRule(new StringReader(OBJECT_MAPPER.writeValueAsString(singletonList(r))));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                })
                .collect(Collectors.toList());
    }
}
