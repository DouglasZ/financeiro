package com.br.financeiro.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
{
	/**
	 *
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Autorizamos o cliente acessar o Authorizationserver
	 */
	@Override
	public void configure( ClientDetailsServiceConfigurer clients ) throws Exception
	{
		clients.inMemory()
				.withClient( "angular" )
				.secret( "@ngul@r0" )
				.scopes( "read", "write" )
				.authorizedGrantTypes( "password", "refresh_token" )
				.accessTokenValiditySeconds( 10000 ) //Indica quantos segundos o accessToken fica funcionando
				.refreshTokenValiditySeconds( 3600 * 24 )
			.and()
				.withClient( "mobile" )
				.secret( "m0b1l30" )
				.scopes( "read" )
				.authorizedGrantTypes( "password", "refresh_token" )
				.accessTokenValiditySeconds( 10000 ) //Indica quantos segundos o accessToken fica funcionando
				.refreshTokenValiditySeconds( 3600 * 24 );
	}

	/**
	 *
	 */
	@Override
	public void configure( AuthorizationServerEndpointsConfigurer endpoints ) throws Exception
	{
		endpoints
				.tokenStore( tokenStore() )
				.accessTokenConverter( accessTokenConverter() ) // Foi adicionando para usar o JWT
				.reuseRefreshTokens( false )
				.authenticationManager( authenticationManager ); //Verificamos se o usuário e senha está tudo certo

	}

	/**
	 *
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter()
	{
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey( "financeiro" );
		return accessTokenConverter;
	}

	/**
	 *
	 */
	@Bean
	public TokenStore tokenStore()
	{
		//Agora estamos usando o JWT Token
		return new JwtTokenStore( accessTokenConverter() );
		//return new InMemoryTokenStore();
	}
}
