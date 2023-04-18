package br.com.financeiro.bean;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.omnifaces.util.Messages;

import br.com.financeiro.dao.HistoricoDAO;
import br.com.financeiro.dao.ProdutoDAO;
import br.com.financeiro.dominio.Historico;
import br.com.financeiro.dominio.Produto;


@ManagedBean
@ViewScoped
public class ProdutoPesquisaBean {
	
	
	private Produto produto;
	private Boolean exibir;
	private Historico historico;

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	public Boolean getExibir() {
		return exibir;
	}
	
	public void setExibir(Boolean exibir) {
		this.exibir = exibir;
	}
	
	
	
	public Historico getHistorico() {
		return historico;
	}

	public void setHistorico(Historico historico) {
		this.historico = historico;
	}

	@PostConstruct // anotation para estanciar o produto automaticamente ao abrir a pagina
	public void novo() {
		historico = new Historico();
		produto = new Produto();
		exibir = false;
		
		
	}
	
	public void buscar() {
		try {
			ProdutoDAO produtoDAO = new ProdutoDAO();
			Produto resultado = produtoDAO.buscar(produto.getCodigo());

			if (resultado == null) {
				exibir = false;
				Messages.addGlobalWarn("Produto não encontrado!");
				
			} else {
				exibir = true;
				produto = resultado;
			}
		} catch (RuntimeException erro) {
			Messages.addGlobalError("Ocorreu um erro ao buscar o produto!");
			erro.printStackTrace();
		}

	}
	
	public void salvar() {
		try {
		historico.setHorario(new Date());
		historico.setProduto(produto);
		
		HistoricoDAO historicoDAO = new HistoricoDAO();
		historicoDAO.salvar(historico);
		
		Messages.addGlobalInfo("o Histórico foi salvo com sucesso");
		} catch (RuntimeException e) {
			Messages.addGlobalError("Não foi possivel salvar o histórico!");
			e.printStackTrace();
		}
	}
}
