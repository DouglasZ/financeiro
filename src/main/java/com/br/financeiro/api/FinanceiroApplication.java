package com.br.financeiro.api;

import com.br.financeiro.api.config.property.FinanceiroApiProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FinanceiroApiProperty.class)
public class FinanceiroApplication
{
	public static void main( String[] args )
	{
		SpringApplication.run( FinanceiroApplication.class, args );
	}
}
