package com.br.financeiro.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pegamos o refresh token do Cookie e colocamos na requisição devolta para funcionar a aplicação
 */
@Component
//Estamos definindo esse filtro com prioridade muito alta, porque é preciso analisar essa requisição antes das outras.
//Porque se for um requisição que tenha o refresh token com grant_type ou se for para o oauth/token que tenha o cookie
//Precisamos adiciona-la na requisição para poder funcionar a aplicação
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter
{
	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;

		if ( "/oauth/token".equalsIgnoreCase( req.getRequestURI() )
				&& "refresh_token".equals( req.getParameter( "grant_type" ) )
				&& req.getCookies() != null )
		{
			for ( Cookie cookie : req.getCookies() )
			{
				if ( cookie.getName().equals( "refreshToken" ) )
				{
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper( req, refreshToken );
				}
			}
		}

		chain.doFilter( req, response );
	}

	@Override
	public void init( FilterConfig filterConfig ) throws ServletException
	{

	}

	@Override
	public void destroy()
	{

	}

	static class MyServletRequestWrapper extends HttpServletRequestWrapper
	{
		private String refreshToken;

		public MyServletRequestWrapper( HttpServletRequest request, String refreshToken )
		{
			super( request );
			this.refreshToken = refreshToken;
		}

		@Override
		public Map<String, String[]> getParameterMap()
		{
			final ParameterMap<String, String[]> map = new ParameterMap<>( getRequest().getParameterMap() );
			map.put( "refresh_token", new String[]{this.refreshToken} );
			map.setLocked( true );
			return map;
		}
	}
}