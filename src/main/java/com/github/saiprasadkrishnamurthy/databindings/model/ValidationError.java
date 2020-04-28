package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    private String file;
    private String message;
    private SeverityType severity;
}
