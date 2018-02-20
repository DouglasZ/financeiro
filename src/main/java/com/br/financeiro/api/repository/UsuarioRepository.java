package com.br.financeiro.api.repository;

import java.util.Optional;

import com.br.financeiro.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>
{
	/**
	 * Com o Optional, se não encontrar valor não precisamos ficar verificando se é diferente de null
	 */
	public Optional<Usuario> findByEmail( String email );
}