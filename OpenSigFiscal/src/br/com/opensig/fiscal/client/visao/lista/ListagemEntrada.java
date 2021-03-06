package br.com.opensig.fiscal.client.visao.lista;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.AComando;
import br.com.opensig.core.client.controlador.comando.FabricaComando;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.lista.ComandoExcluir;
import br.com.opensig.core.client.visao.abstrato.IFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoCartaEntrada;
import br.com.opensig.fiscal.client.servico.FiscalProxy;
import br.com.opensig.fiscal.client.visao.form.FormularioErro;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaEntrada;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtextux.client.widgets.window.ToastWindow;

public class ListagemEntrada extends AListagemNota<FisNotaEntrada> {

	private IComando cmdCancelar;
	private IComando cmdCarta;
	private String motivo;

	public ListagemEntrada(IFormulario<FisNotaEntrada> formulario) {
		super(formulario);
		nomes.put("id", "fisNotaEntradaId");
		nomes.put("numero", "fisNotaEntradaNumero");
		nomes.put("cadastro", "fisNotaEntradaCadastro");
		nomes.put("data", "fisNotaEntradaData");
		nomes.put("valor", "fisNotaEntradaValor");
		nomes.put("chave", "fisNotaEntradaChave");
		nomes.put("icms", "fisNotaEntradaIcms");
		nomes.put("ipi", "fisNotaEntradaIpi");
		nomes.put("pis", "fisNotaEntradaPis");
		nomes.put("cofins", "fisNotaEntradaCofins");
		nomes.put("evento", "fisNotaEntradaEvento");
		nomes.put("protocolo", "fisNotaEntradaProtocolo");
		nomes.put("xml", "fisNotaEntradaXml");
		nomes.put("protocoloCancelado", "fisNotaEntradaProtocoloCancelado");
		nomes.put("xmlCancelado", "fisNotaEntradaXmlCancelado");
		nomes.put("protocoloCarta", "fisNotaEntradaProtocoloCarta");
		nomes.put("xmlCarta", "fisNotaEntradaXmlCarta");
		nomes.put("recibo", "fisNotaEntradaRecibo");
		nomes.put("erro", "fisNotaEntradaErro");
		inicializar();

		// cancelando
		cmdCancelar = new AComando() {
			public void execute(Map contexto) {
				super.execute(contexto);
				int id = UtilClient.getSelecionado(getPanel());
				classe.setId(id);

				FiscalProxy<FisNotaEntrada> proxy = new FiscalProxy<FisNotaEntrada>();
				proxy.cancelarEntrada(classe, motivo, new AsyncCallback<Map<String, String>>() {
					public void onFailure(Throwable caught) {
						getPanel().getEl().unmask();
						MessageBox.alert(OpenSigCore.i18n.txtCancelar(), caught.getMessage());
					};

					public void onSuccess(Map<String, String> result) {
						getPanel().getEl().unmask();
						getStore().reload();
					};
				});
			}
		};

		// carta
		cmdCarta = new AComando() {
			public void execute(Map contexto) {
				super.execute(contexto);
				int id = UtilClient.getSelecionado(getPanel());
				classe.setId(id);

				FiscalProxy<FisNotaEntrada> proxy = new FiscalProxy<FisNotaEntrada>();
				proxy.cartaEntrada(classe, motivo, new AsyncCallback<Map<String, String>>() {
					public void onFailure(Throwable caught) {
						getPanel().getEl().unmask();
						MessageBox.alert(OpenSigCore.i18n.txtCarta(), caught.getMessage());
					};

					public void onSuccess(Map<String, String> result) {
						getPanel().getEl().unmask();
						getStore().reload();
					};
				});
			}
		};
	}

	protected String getXml(FisNotaEntrada result) {
		return result.getFisNotaEntradaXml();
	}

	protected String getXmlCancelado(FisNotaEntrada result) {
		return result.getFisNotaEntradaXmlCancelado();
	};

	protected String getXmlCarta(FisNotaEntrada result) {
		return result.getFisNotaEntradaXmlCarta();
	}

	protected String getChave(FisNotaEntrada result) {
		return result.getFisNotaEntradaChave();
	}

	protected String getErro(FisNotaEntrada result) {
		return result.getFisNotaEntradaErro();
	}

	protected void mostrarErro(FisNotaEntrada result) {
		Window wnd = new Window(OpenSigCore.i18n.txtErro(), 800, 600, true, false);
		FormularioErro<FisNotaEntrada> frmErro = new FormularioErro<FisNotaEntrada>(result, getFuncao(), wnd);
		frmErro.setLista(this);
		frmErro.mostrarDados();
		wnd.add(frmErro);
		wnd.show();
	}

	@Override
	public IComando AntesDaAcao(IComando comando) {
		Record rec = getSelectionModel().getSelected();

		// valida se pode excluir ou cancelar
		if (comando instanceof ComandoExcluir) {
			if (rec != null && rec.getAsInteger("fisNotaStatus.fisNotaStatusId") == ENotaStatus.AUTORIZADO.getId()) {
				comando = null;
				MessageBox.prompt(OpenSigCore.i18n.txtCancelar(), OpenSigCore.i18n.msgConfirma(), new PromptCallback() {
					public void execute(String btnID, String text) {
						if (btnID.equalsIgnoreCase("ok")) {
							if (text == null) {
								new ToastWindow(OpenSigCore.i18n.txtCancelar(), OpenSigCore.i18n.errInvalido()).show();
							} else {
								getPanel().getEl().mask(OpenSigCore.i18n.txtAguarde());
								motivo = text;
								cmdCancelar.execute(contexto);
							}
						}
					}
				}, true);
			} else {
				MessageBox.alert(OpenSigCore.i18n.txtCancelar(), OpenSigCore.i18n.errSelecionar());
			}
		} else if (comando instanceof ComandoCartaEntrada) {
			if (rec != null && rec.getAsInteger("fisNotaStatus.fisNotaStatusId") == ENotaStatus.AUTORIZADO.getId()) {
				MessageBox.prompt(OpenSigCore.i18n.txtCarta(), "Texto de correcao completo. (min=15 max=1000)", new PromptCallback() {
					public void execute(String btnID, String text) {
						if (btnID.equalsIgnoreCase("ok")) {
							if (text == null || text.trim().length() < 15 || text.trim().length() > 1000) {
								new ToastWindow(OpenSigCore.i18n.txtCarta(), OpenSigCore.i18n.errInvalido()).show();
							} else {
								getPanel().getEl().mask(OpenSigCore.i18n.txtAguarde());
								motivo = text.trim();
								cmdCarta.execute(contexto);
							}
						}
					}
				}, true);
			} else {
				MessageBox.alert(OpenSigCore.i18n.txtCarta(), OpenSigCore.i18n.errSelecionar());
			}
		}

		return comando;
	}

	@Override
	public void irPara() {
		Menu mnuContexto = new Menu();

		// compra
		String strCompra = FabricaComando.getInstancia().getComandoCompleto("ComandoCompra");
		SisFuncao compra = UtilClient.getFuncaoPermitida(strCompra);
		MenuItem itemCompra = gerarFuncao(compra, "fisNotaEntrada.fisNotaEntradaId", "fisNotaEntradaId");
		if (itemCompra != null) {
			mnuContexto.addItem(itemCompra);
		}

		if (mnuContexto.getItems().length > 0) {
			MenuItem mnuItem = getIrPara();
			mnuItem.setMenu(mnuContexto);

			getMenu().addSeparator();
			getMenu().addItem(mnuItem);
		}
	}
}
