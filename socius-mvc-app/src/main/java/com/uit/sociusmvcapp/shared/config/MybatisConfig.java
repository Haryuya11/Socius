package com.uit.sociusmvcapp.shared.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/** MyBatis configuration class that scans for mapper interfaces in the specified base packages. */
@Configuration
@MapperScan(basePackages = "com.uit.sociusmvcapp.**.persistence")
public class MybatisConfig {}
