package com.amantovan.minhasfinancas.service;

import java.util.List;

import com.amantovan.minhasfinancas.model.entity.Lancamento;
import com.amantovan.minhasfinancas.model.enuns.StatusLancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);
	Lancamento atualizar(Lancamento lancamento);
	void deletar(Lancamento lancamento);
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	void validar(Lancamento lancamento);
}
