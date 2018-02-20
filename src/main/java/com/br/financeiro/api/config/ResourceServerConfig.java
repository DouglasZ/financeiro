package com.br.financeiro.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Implementação de segurança usando o OAuth2
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true) //Habilitamos a segurança nos métodos
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
	@Autowired
	private UserDetailsService userDetailsService;


	@Autowired
	public void configure( AuthenticationManagerBuilder auth ) throws Exception
	{
		auth.userDetailsService( userDetailsService ).passwordEncoder( this.passwordEncoder() );
	}

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

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
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
