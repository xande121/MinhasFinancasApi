package com.amantovan.minhasfinancas.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.amantovan.minhasfinancas.exception.RegraNegocioException;
import com.amantovan.minhasfinancas.model.entity.Lancamento;
import com.amantovan.minhasfinancas.model.enuns.StatusLancamento;
import com.amantovan.minhasfinancas.model.enuns.TipoLancamento;
import com.amantovan.minhasfinancas.model.repository.LancamentoRepository;
import com.amantovan.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.amantovan.minhasfinancas.model.repository.UsuarioRepositoryTest;
import com.amantovan.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean LancamentoServiceImpl service;
	@MockBean LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
	
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
		
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class) ;
		
		verify(repository, never()).save(lancamentoASalvar);
		
	}
	

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		doNothing().when(service).validar(lancamentoSalvo);
		
		when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
	
		service.atualizar(lancamentoSalvo);
		
		verify(repository, times(1)).save(lancamentoSalvo);
	}
	

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class) ;
		
		verify(repository, never()).save(lancamento);
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		service.deletar(lancamento);
		
		verify(repository).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class) ;
		
		verify(repository, never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		when(repository.findAll(any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		verify(service).atualizar(lancamento);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		assertThat(resultado.isPresent()).isTrue();
	}
	

	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErroAoValidarCamposDoLancamentos() {
		//olhar curso github para ver solução proposta
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setDescricao("");
		
		Throwable exceptionDescricao = catchThrowable(() -> service.validar(lancamento));
		
		lancamento.setDescricao("descrição teste");
		lancamento.setMes(0);
		
		Throwable exceptionMes = catchThrowable(() -> service.validar(lancamento));
		
		lancamento.setMes(2);
		lancamento.setAno(null);
		
		Throwable exceptionAno = catchThrowable(() -> service.validar(lancamento));
		
		lancamento.setAno(2000);
		lancamento.setUsuario(null);
		
		Throwable exceptionUsuario = catchThrowable(() -> service.validar(lancamento));
		
		lancamento.setUsuario(UsuarioRepositoryTest.criarUsuario());
		lancamento.getUsuario().setId(1l);
		lancamento.setValor(BigDecimal.valueOf(-1d));
		
		Throwable exceptionValor = catchThrowable(() -> service.validar(lancamento));
		
		lancamento.setTipo(null);
		lancamento.setValor(BigDecimal.ONE);

		Throwable exceptionTipo = catchThrowable(() -> service.validar(lancamento));
		
		assertThat(exceptionDescricao).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		assertThat(exceptionMes).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		assertThat(exceptionAno).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		assertThat(exceptionUsuario).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		assertThat(exceptionValor).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		assertThat(exceptionTipo).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento.");
	}
	
	@Test
	public void deveObterSaldoPorUsuario() {
		Long idUsuario = 1l;
		when(repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO))
		.thenReturn(BigDecimal.valueOf(100));

		when(repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO))
		.thenReturn(BigDecimal.valueOf(50));
		
		BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);
		
		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
		
	}
}
