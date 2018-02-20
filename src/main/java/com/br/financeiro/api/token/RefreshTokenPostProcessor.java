package com.br.financeiro.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.br.financeiro.api.config.property.FinanceiroApiProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Removemos o Refresh Token do Corpo(Body) da resposta da requisição e adicionamos em um Cookie Http
 */
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken>
{
	/**
	 *
	 */
	@Autowired
	private FinanceiroApiProperty financeiroApiProperty;

	/**
	 *
	 */
	@Override
	public boolean supports( MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType )
	{
		return returnType.getMethod().getName().equals( "postAccessToken" );
	}

	/**
	 *
	 */
	@Override
	public OAuth2AccessToken beforeBodyWrite( OAuth2AccessToken body, MethodParameter returnType,
											  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
											  ServerHttpRequest request, ServerHttpResponse response )
	{

		final HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		final HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();

		final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;

		final String refreshToken = body.getRefreshToken().getValue();
		this.adicionarRefreshTokenNoCookie( refreshToken, req, resp );
		this.removerRefreshTokenDoBody( token );

		return body;
	}

	/**
	 * Removemos o Refresh Token do Body
	 */
	private void removerRefreshTokenDoBody( DefaultOAuth2AccessToken token )
	{
		token.setRefreshToken( null );
	}

	/**
	 * Adicionamos o Refresh Token em um Cookie Http
	 */
	private void adicionarRefreshTokenNoCookie( String refreshToken, HttpServletRequest req, HttpServletResponse resp )
	{
		final Cookie refreshTokenCookie = new Cookie( "refreshToken", refreshToken );
		refreshTokenCookie.setHttpOnly( true );
		refreshTokenCookie.setSecure( this.financeiroApiProperty.getSeguranca().isEnableHttps() );
		refreshTokenCookie.setPath( req.getContextPath() + "/oauth/token" );
		refreshTokenCookie.setMaxAge( 2592000 );
		resp.addCookie( refreshTokenCookie );
	}
}