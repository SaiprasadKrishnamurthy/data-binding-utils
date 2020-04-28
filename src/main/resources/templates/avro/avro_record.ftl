{
  "namespace": "${dataElement.namespace}",
  "name": "${dataElement.name}",
  "type": "record",
  "version": "${dataElement.version}",
  "doc": "${dataElement.documentation}",
  "fields": [
      <#list dataElement.fields as field>
      {
        "name": "${field.name}",
        "doc": "${field.documentation}",
        <#if field.array>
            <#if field.required>
                "type": {
                         "type": "array",
                         "items": ["null", "${field.type}"]
                    }
            <#else>
                "type": {
                    "type": "array",
                    "items": ["null", "${field.type}"]
                }
            </#if>
        <#else>
             <#if field.required>
                "type": "${field.type}"
             <#else>
                "type": ["null", "${field.type}"]
             </#if>
        </#if>
      }<#sep>,
      </#list>
     ]
}