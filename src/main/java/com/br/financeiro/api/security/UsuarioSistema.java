package com.br.financeiro.api.security;

import java.util.Collection;

import com.br.financeiro.api.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Classe responsável por definir as informações do usuário que irá aparecer no Front-End
 */
public class UsuarioSistema extends User
{
	private Usuario usuario;

	public UsuarioSistema( Usuario usuario, Collection<? extends GrantedAuthority> authorities )
	{
		super( usuario.getEmail(), usuario.getSenha(), authorities );
		this.usuario = usuario;
	}

	public Usuario getUsuario()
	{
		return usuario;
	}

}