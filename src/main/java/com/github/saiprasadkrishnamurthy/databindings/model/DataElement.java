package com.github.saiprasadkrishnamurthy.databindings.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private String author = "unascribed";

    private String documentation = "TODO";
    private List<Field> fields = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<String> enumeratedValues = new ArrayList<>();
    private boolean topLevelContainerType;
    private List<String> identifierFields = new ArrayList<>();

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

    public String getIdentifiers() {
        if (identifierFields.isEmpty()) {
            return "";
        }
        return "of = {" + identifierFields.stream().map(i -> "\"" + i + "\"").collect(Collectors.joining(", ")) + "} ";
    }

    public boolean isExtends() {
        return StringUtils.isNotBlank(baseType);
    }
}
