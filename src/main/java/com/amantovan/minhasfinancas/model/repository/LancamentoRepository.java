package com.amantovan.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amantovan.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
