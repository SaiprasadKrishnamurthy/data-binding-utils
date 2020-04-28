package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author
 */
@Data
@AllArgsConstructor
public class JavaType {
    private final String namespace;
    private final String name;
    private final String contents;
}
