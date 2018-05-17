package com.br.financeiro.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.financeiro.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>
{
	/**
	 * Com o Optional, se não encontrar valor não precisamos ficar verificando se é diferente de null
	 */
	Optional<Usuario> findByEmail( String email );

	/**
	 * Buscamos usuários que tem uma determinada permissão
	 */
	List<Usuario> findByPermissoesDescricao(String permissaoDescricao);
}