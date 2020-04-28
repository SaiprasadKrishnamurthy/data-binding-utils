package com.github.saiprasadkrishnamurthy.databindings;

import lombok.Data;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = "com.github.saiprasadkrishnamurthy.databindings")
public class DataBindingAppConfig {
}
