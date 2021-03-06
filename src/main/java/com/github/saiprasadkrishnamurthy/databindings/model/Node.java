package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "contents"})
public class Node {
    private final int id;
    private final String name;
    private final String contents;
}
