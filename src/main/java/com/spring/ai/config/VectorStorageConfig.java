package com.spring.ai.config;

import java.util.List;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStorageConfig {
	
	@Bean
	public TokenTextSplitter tokenTextSplitter() {
		return new TokenTextSplitter(500,100,5,10000,true, List.of('.', '?', '!', '\n', ';', '|', '-'));
	}
}
