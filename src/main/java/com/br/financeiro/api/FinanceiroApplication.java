package com.br.financeiro.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import com.br.financeiro.api.config.property.FinanceiroApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(FinanceiroApiProperty.class)
public class FinanceiroApplication
{
	private static ApplicationContext APPLICATION_CONTEXT;

	public static void main( String[] args )
	{
		APPLICATION_CONTEXT = SpringApplication.run( FinanceiroApplication.class, args );
	}

	/**
	 * Método que retorna uma instância de qualquer classe dentro da aplicação
	 * <p>
	 * Isso é usando quando precisamos usar uma instância da classe A que não pode ser injetada na classe B,
	 * pelo fato de que a instância da classe B está sendo construida pelo Hibernate e não pelo Spring
	 */
	public static <T> T getBean( Class<T> type )
	{
		return APPLICATION_CONTEXT.getBean( type );
	}
}
