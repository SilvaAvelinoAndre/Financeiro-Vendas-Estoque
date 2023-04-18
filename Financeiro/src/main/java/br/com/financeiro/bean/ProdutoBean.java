package br.com.financeiro.bean;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.financeiro.dao.FornecedorDAO;
import br.com.financeiro.dao.ProdutoDAO;
import br.com.financeiro.dominio.Fornecedor;
import br.com.financeiro.dominio.Produto;
import br.com.financeiro.util.HibernateUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class ProdutoBean implements Serializable {
	private Produto  produto;
	private List<Produto> produtos;
	private List<Fornecedor> fornecedor;
	
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	public List<Fornecedor> getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(List<Fornecedor> fornecedor) {
		this.fornecedor = fornecedor;
	}

	@PostConstruct
	public void listar() {
		try {
			ProdutoDAO produtoDAO = new ProdutoDAO();
			produtos = produtoDAO.listar();

			
		} catch (RuntimeException erro) {
			Messages.addGlobalError("Ocorreu um erro ao tentar listar os produtos");
			erro.printStackTrace();
		}
	}
	
	public void novo() {
		try {
			produto = new Produto();

			FornecedorDAO fabricanteDAO = new FornecedorDAO();
			fornecedor = fabricanteDAO.listar();
		} catch (RuntimeException erro) {
			Messages.addFlashGlobalError("Ocorreu um erro ao tentar gerar um novo produto");
			erro.printStackTrace();
		}
	}
	
	public void editar(ActionEvent evento){
		try {
			produto = (Produto) evento.getComponent().getAttributes().get("produtoSelecionado");

			//Código da erro se não cadastrar uma imagem se ela já não existir , porém permite a edição de outros componentes
			produto.setCaminho("D:/Uploads Imagens/" + produto.getCodigo() + ".jpg");
			
			FornecedorDAO fornecedorDAO = new FornecedorDAO();
			fornecedor = fornecedorDAO.listar();
		} catch (RuntimeException erro) {
			Messages.addFlashGlobalError("Ocorreu um erro ao tentar selecionar um produto");
			erro.printStackTrace();
		}	
	}
	
	public void salvar() {
		try {
			//validação para obrigar o usuario cadastrar uma imagem
			if(produto.getCaminho() == null) {
				Messages.addGlobalInfo("Por favor cadastre uma imagem!!");
				return; // faz sair do metodo salvar se entrar nes if.
			}
			
			ProdutoDAO produtoDAO = new ProdutoDAO();
			Produto produtoRetorno = produtoDAO.merge(produto);

			Path origem = Paths.get(produto.getCaminho());
			Path destino = Paths.get("D:/Uploads Imagens/" 
			+ produtoRetorno.getCodigo() + ".jpg");
			Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
			
			produto = new Produto();

			FornecedorDAO fornecedorDAO = new FornecedorDAO();
			fornecedor = fornecedorDAO.listar();

			produtos = produtoDAO.listar();

			Messages.addGlobalInfo("Produto salvo com sucesso");
		} catch (RuntimeException | IOException erro) {
			Messages.addFlashGlobalError("Ocorreu um erro ao tentar salvar o produto");
			erro.printStackTrace();
		}
	}

	public void excluir(ActionEvent evento) {
		try {
			produto = (Produto) evento.getComponent().getAttributes().get("produtoSelecionado");

			ProdutoDAO produtoDAO = new ProdutoDAO();
			produtoDAO.excluir(produto);
			Path arquivo = Paths.get("D:/Uploads Imagens/" + produto.getCodigo()+ ".jpg");
			Files.deleteIfExists(arquivo);
			
			produtos = produtoDAO.listar();

			Messages.addGlobalInfo("Produto removido com sucesso");
		} catch (RuntimeException |IOException erro) {
			Messages.addFlashGlobalError("Ocorreu um erro ao tentar remover o produto");
			erro.printStackTrace();
		}
	}
	
	
	public void upload(FileUploadEvent evento) {
		try {
		UploadedFile arquivoUpload = evento.getFile();
		
		
		Path arquivoTemporario = Files.createTempFile(null, null);
		Files.copy(arquivoUpload.getInputstream(), arquivoTemporario, StandardCopyOption.REPLACE_EXISTING);
		produto.setCaminho(arquivoTemporario.toString());
		Messages.addGlobalInfo("Imagem salva com sucesso");
		
		} catch (IOException e) {
			Messages.addFlashGlobalError("Ocorreu um erro ao tentar carregar a imagem!");
			e.printStackTrace();
		}
		
	}
	
	public void imprimir() {
		try {
			//Cria tabela para buscar os dados filtrados na tabela do produto.xhtml usando o formListagem:tabela
			DataTable tabela = (DataTable) Faces.getViewRoot().findComponent("formListagem:tabela");
			//Mapeia passando uma string retornando um objeto usando o getFilters para trazer filtros na pagina
			Map<String, Object> filtros = tabela.getFilters();

			//Separa os filtros pela colunas no caso descrição do produto e do fornecedor
			String proDescricao = (String) filtros.get("descricao");
			String forDescricao = (String) filtros.get("fornecedor.descricao");

			//Passa o caminho do relatório ja buildado
			String caminho = Faces.getRealPath("/relatorios/produtos.jasper");

			//Mapeia os parametros passados como filtro na pagina xhtml
			Map<String, Object> parametros = new HashMap<>();
			//Testa se o parametro é nulo se for traz a pagina toda senão só os parametros
			if (proDescricao == null) {
				parametros.put("PRODUTO_DESCRICAO", "%%");
			} else {
				parametros.put("PRODUTO_DESCRICAO", "%" + proDescricao + "%");
			}
			//Mesmo teste do if acima
			if (forDescricao == null) {
				parametros.put("FORNECEDOR_DESCRICAO", "%%");
			} else {
				parametros.put("FORNECEDOR_DESCRICAO", "%" + forDescricao + "%");
			}

			//Puxa a conexaõ com banco usando o getConexao criado para pegar a conexão 
			// do hibernate e passar para o JDBC que o jasper trabalha.
			Connection conexao = HibernateUtil.getConexao();

			JasperPrint relatorio = JasperFillManager.fillReport(caminho, 

parametros, conexao);

			JasperPrintManager.printReport(relatorio, true);

} catch (JRException erro) {
			Messages.addGlobalError("Ocorreu um erro ao tentar gerar o relatório");
			erro.printStackTrace();
		}
	}
	
}
	
	

