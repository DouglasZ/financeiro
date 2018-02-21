package com.br.financeiro.api.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.br.financeiro.api.model.Usuario;
import com.br.financeiro.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService
{
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername( String email ) throws UsernameNotFoundException
	{
		final Optional<Usuario> usuarioOptional = this.usuarioRepository.findByEmail( email );
		final Usuario usuario = usuarioOptional.orElseThrow( () -> new UsernameNotFoundException( "Usuário e/ou senha inválidos." ) );
		return new UsuarioSistema( usuario, getPermissoes( usuario ) );
	}

	private Collection<? extends GrantedAuthority> getPermissoes( Usuario usuario )
	{
		final Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		usuario.getPermissoes().forEach( p -> authorities.add( new SimpleGrantedAuthority( p.getDescricao().toUpperCase() ) ) );
		return authorities;
	}

}