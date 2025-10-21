package com.uit.sociuscoremodules.shared.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/** MyBatis configuration class that scans for mapper interfaces in the specified base packages. */
@Configuration
@MapperScan(basePackages = "com.uit.sociuscoremodules.**.persistence")
public class MybatisConfig {}
