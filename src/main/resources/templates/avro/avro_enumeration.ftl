{
  "namespace": "${dataElement.namespace}",
  "name": "${dataElement.name}",
  "type": "enum",
  "version": "${dataElement.version}",
  "doc": "${dataElement.documentation}",
  "symbols": [
      <#list dataElement.enumeratedValues as enumeratedValue>
        "${enumeratedValue}"<#sep>,
      </#list>
     ]
}