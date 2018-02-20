package com.br.financeiro.api.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Gerador de senha encodada
 */
public class GeradorSenha
{
	public static void main( String[] args )
	{
		final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println( encoder.encode( "admin" ) );
	}

}