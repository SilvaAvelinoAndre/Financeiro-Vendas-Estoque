package br.com.financeiro.dao;

import java.math.BigDecimal;

import org.junit.Test;

import br.com.financeiro.dominio.Fornecedor;
import br.com.financeiro.dominio.Produto;

public class ProdutoDAOTest {
	
	@SuppressWarnings("removal")
	@Test
	public void salvar(){
		FornecedorDAO fornecedorDAO = new FornecedorDAO();
		Fornecedor fornecedor = fornecedorDAO.buscar(new Long("1"));
		
		Produto produto = new Produto();
		produto.setDescricao("Curso de Java");
		produto.setFornecedor(fornecedor);
		produto.setPreco(new BigDecimal("13.70"));
		produto.setQuantidade(new Short("7"));
		
		ProdutoDAO produtoDAO = new ProdutoDAO();
		produtoDAO.salvar(produto);
		
		System.out.println("Produto salvo com sucesso");
	}

}
