package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a canonical schema element.
 *
 * @author Sai.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataElement {
    private String qualifiedName;
    private String version = "TODO";
    private DataElementType type = DataElementType.object;
    private String baseType;
    private String author = "TODO";

    private String documentation = "TODO";
    private List<Field> fields = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<String> enumeratedValues = new ArrayList<>();
    private boolean topLevelContainerType;

    private String fileName;

    public String getNamespace() {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    public String getName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }

    public String getFileName() {
        return fileName == null ? qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1) : fileName;
    }
}
