// This class is autogenerated.
package ${dataElement.namespace};

/**
 * ${dataElement.documentation}.
 * @version ${dataElement.version}.
 * @author ${dataElement.author}.
 */
 public enum ${dataElement.name} {
 <#list dataElement.enumeratedValues as field>
      ${field}<#sep>,
 </#list>;
 }