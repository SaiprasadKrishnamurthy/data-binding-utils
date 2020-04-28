package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Canonical representation of a field.
 *
 * @author Sai.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field {
    private static final Map<String, String> JAVA_PRIMITIVE_TYPES = new HashMap<>() {{
        put("string", "String");
        put("int", "Integer");
        put("long", "Long");
        put("double", "Double");
        put("float", "Float");
        put("boolean", "Boolean");
        put("map", "Object");
    }};

    private String name;
    private String type;
    private String documentation = "TODO";
    private boolean required = true;
    private boolean array;

    public String getJavaType() {
        return JAVA_PRIMITIVE_TYPES.getOrDefault(type, type);
    }

    public boolean isAJavaType() {
        return JAVA_PRIMITIVE_TYPES.containsKey(type);
    }
}
