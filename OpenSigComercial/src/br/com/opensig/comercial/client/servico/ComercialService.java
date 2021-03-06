package br.com.opensig.comercial.client.servico;

import java.util.List;
import java.util.Map;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComConsumo;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComTroca;
import br.com.opensig.comercial.shared.modelo.ComValorProduto;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.servico.CoreService;
import br.com.opensig.fiscal.shared.modelo.FisNotaEntrada;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;

public interface ComercialService extends CoreService {

	public FisNotaSaida gerarNfe(ComVenda venda, ComFrete frete, String nfe, int[] cupom) throws ComercialException;
	
	public FisNotaEntrada gerarNfe(ComCompra compra, ComFrete frete, String nfe) throws ComercialException;

	public void gerarCompra(IFiltro filtro) throws ComercialException;
	
	public void fecharCompra(ComCompra compra) throws ComercialException;

	public void gerarVenda(IFiltro filtro) throws ComercialException;
	
	public String[][] fecharVenda(ComVenda venda) throws ComercialException;

	public void fecharFrete(ComFrete frete) throws ComercialException;
	
	public void fecharConsumo(ComConsumo consumo) throws ComercialException;

	public void fecharEcfVenda(ComEcfVenda venda) throws ComercialException;
	
	public ComCompra salvarCompra(ComCompra compra) throws ComercialException;

	public ComVenda salvarVenda(ComVenda venda) throws ComercialException;

	public ComTroca salvarTroca(ComTroca troca) throws ComercialException;
	
	public ComValorProduto salvarValor(ComValorProduto valor) throws ComercialException;

	public ComEcfVenda salvarEcfVenda(ComEcfVenda venda) throws ComercialException;
	
	public ComEcfZ salvarEcfZ(ComEcfZ z) throws ComercialException;
	
	public void excluirCompra(ComCompra compra) throws ComercialException;

	public void excluirVenda(ComVenda venda) throws ComercialException;

	public void cancelarVenda(ComVenda venda) throws ComercialException;

	public void excluirFrete(ComFrete frete) throws ComercialException;
	
	public void excluirConsumo(ComConsumo consumo) throws ComercialException;

	public void cancelarEcfVenda(ComEcfVenda venda) throws ComercialException;
	
	public Map<String, Integer> importarEcfVenda(List<String> nomesArquivos) throws ComercialException;
}
