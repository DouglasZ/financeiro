package com.br.financeiro.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Implementação de segurança usando o OAuth2
 */
@Profile("oauth-security")
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
	@Override
	public void configure( HttpSecurity http ) throws Exception
	{
		//Para qualquer requisição http é preciso que o usuário esteja autenticado
		http.authorizeRequests()
				.anyRequest().authenticated().and()
				.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
				.csrf().disable(); //Csrf para caso alguem tentar usar um Javascritp injection. Porem a aplicação não a parte Web, logo desabilitamos.
	}

	@Override
	public void configure( ResourceServerSecurityConfigurer resources ) throws Exception
	{
		//Indicia que a API Rest não mantenha estado de nada
		resources.stateless( true );
	}

	/**
	 * Para funcionar a segurança dos métodos
	 */
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler()
	{
		return new OAuth2MethodSecurityExpressionHandler();
	}
}
