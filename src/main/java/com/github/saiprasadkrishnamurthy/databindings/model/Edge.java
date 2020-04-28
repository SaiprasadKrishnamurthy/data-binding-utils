package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Edge {
    private final int from;
    private final int to;
    private final String label;

    public boolean isInherits() {
        return label.equalsIgnoreCase("inherits");
    }
}
