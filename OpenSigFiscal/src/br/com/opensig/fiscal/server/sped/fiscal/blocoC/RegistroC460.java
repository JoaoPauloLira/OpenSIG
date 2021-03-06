package br.com.opensig.fiscal.server.sped.fiscal.blocoC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC460 extends ARegistro<DadosC460, ComEcfVenda> {

	private Map<String, List<DadosC470>> analitico = new HashMap<String, List<DadosC470>>();
	private ComEcfVendaProduto vendaProd = new ComEcfVendaProduto();

	@Override
	public void executar() {
		qtdLinhas = 0;
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			bloco = getDados(dados);
			out.write(bloco);
			out.flush();

			// itens das vendas
			if (dados.getComEcfVendaFechada() && dados.getComEcfVendaCancelada() == false) {
				RegistroC470 r470 = new RegistroC470();
				FiltroObjeto fo = new FiltroObjeto("comEcfVenda", ECompara.IGUAL, dados);
				List<ComEcfVendaProduto> vps = service.selecionar(vendaProd, 0, 0, fo, false).getLista();
				for (ComEcfVendaProduto vp : vps) {
					if (vp.getComEcfVendaProdutoCancelado() == false) {
						r470.setDados(vp);
						r470.executar();
						qtdLinhas += r470.getQtdLinhas();
						setAnalitico(r470.getBloco());
					}
				}
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	protected DadosC460 getDados(ComEcfVenda dados) throws Exception {
		DadosC460 d = new DadosC460();
		d.setCod_mod(dados.getComEcf().getComEcfCodigo());
		d.setCod_sit(dados.getComEcfVendaCancelada() ? "02" : "00");
		d.setNum_doc(dados.getComEcfVendaCoo());
		if (dados.getComEcfVendaCancelada() == false) {
			d.setDt_doc(dados.getComEcfVendaData());
			d.setVl_doc(dados.getComEcfVendaLiquido());
			d.setVl_pis(dados.getComEcfVendaLiquido() * pis / 100);
			d.setVl_cofins(dados.getComEcfVendaLiquido() * cofins / 100);
			if (dados.getEmpCliente() != null) {
				String doc = dados.getEmpCliente().getEmpEntidade().getEmpEntidadeDocumento1().replaceAll("\\D", "");
				if (!doc.replaceAll("[0]", "").equals("")) {
					d.setCpf_cnpj(doc);
					d.setNom_adq(dados.getEmpCliente().getEmpEntidade().getEmpEntidadeNome1());
				}
			}
		} else {
			d.setDt_doc(null);
			d.setVl_doc(null);
			d.setVl_pis(null);
			d.setVl_cofins(null);
			d.setCpf_cnpj(null);
			d.setNom_adq(null);
		}

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private void setAnalitico(DadosC470 d) {
		String chave = d.getCst_icms() + d.getCfop() + d.getAliq_icms();
		List<DadosC470> lista = analitico.get(chave);
		if (lista == null) {
			lista = new ArrayList<DadosC470>();
			lista.add(d);
			analitico.put(chave, lista);
		} else {
			lista.add(d);
		}
	}

	public Map<String, List<DadosC470>> getAnalitico() {
		return analitico;
	}
}
