package com.br.financeiro.api.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("application-config")
public class FinanceiroApiProperty
{
	// A Configuração está no application.properties
	private String host;

	private final Seguranca seguranca = new Seguranca();

	/**
	 * Configuraçãoes para a Segurança da aplicação
	 */
	@Data
	public static class Seguranca
	{
		private boolean enableHttps;
	}

	private final Mail mail = new Mail();

	/**
	 * Configurações para envio de email
	 */
	@Data
	public static class Mail {

		private String host;

		private Integer port;

		private String username;

		private String password;
	}

	private final S3 s3 = new S3();
	/**
	 * Configurações do upload de arquivos S3
	 */
	@Data
	public static class S3 {

		private String accessKeyId;

		private String secretAccessKey;

		private String endPoint;

		// Esse bucket é único quando usando na Amazon S3
		// Para estudo estou usando o S3Ninja.
		private String bucket = "financeiro-arquivos";

		private Boolean serverEnabled;
	}
}
