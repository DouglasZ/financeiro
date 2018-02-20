package com.br.financeiro.api.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("application-config")
public class FinanceiroApiProperty
{
	private String host = "http://localhost:4200";

	private final Seguranca seguranca = new Seguranca();

	@Data
	public static class Seguranca
	{
		private boolean enableHttps;
	}
}
