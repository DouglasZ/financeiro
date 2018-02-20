package com.br.financeiro.api.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Foi implementado apenas para exemplo usando o Security Basic
 * A implementação real da aplicação será utilizando o OAth2
 */
//Essa anotação está comentada para não usar essa classe para a autenticação
//E sim usar a classe de segurança com o OAuth2
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure( AuthenticationManagerBuilder auth ) throws Exception
	{
		auth.inMemoryAuthentication()
				.withUser( "admin" ).password( "admin" ).roles( "ROLE" );
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception
	{
		//Para qualquer requisição http é preciso que o usuário esteja autenticado
		http.authorizeRequests()
				.anyRequest().authenticated().and()
				.httpBasic().and()
				.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and() //Indicia que a API Rest não mantenha estado de nada

				.csrf().disable(); //Csrf para caso alguem tentar usar um Javascritp injection. Porem a aplicação não a parte Web, logo desabilitamos.
	}
}
