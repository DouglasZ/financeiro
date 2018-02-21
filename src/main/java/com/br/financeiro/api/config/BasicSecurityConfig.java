package com.br.financeiro.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Esta implementação do Basci Security é para facilitar o desenvolvimento no Front-End
 */
@Profile("basic-security")
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure( AuthenticationManagerBuilder auth ) throws Exception
	{
		auth.userDetailsService( userDetailsService ).passwordEncoder( passwordEncoder() );
	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception
	{
		//Para qualquer requisição http é preciso que o usuário esteja autenticado
		http.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.httpBasic()
				.and()
				.sessionManagement()
					.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) //Indicia que a API Rest não mantenha estado de nada
				.and()
				.csrf().disable(); //Csrf para caso alguem tentar usar um Javascritp injection. Porem a aplicação não a parte Web, logo desabilitamos.
	}
}
