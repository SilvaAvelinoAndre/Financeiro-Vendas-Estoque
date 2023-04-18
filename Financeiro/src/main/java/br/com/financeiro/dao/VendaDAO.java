package br.com.financeiro.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.omnifaces.util.Messages;

import br.com.financeiro.dominio.ItemVenda;
import br.com.financeiro.dominio.Produto;
import br.com.financeiro.dominio.Venda;
import br.com.financeiro.util.HibernateUtil;

public class VendaDAO extends GenericDAO<Venda> {
	@SuppressWarnings("removal")
	public void salvar(Venda venda, List<ItemVenda> itensVenda){
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction transacao = null;

		try {
			transacao = sessao.beginTransaction();
		
			sessao.save(venda);
			
			for(int posicao = 0; posicao < itensVenda.size(); posicao++){
				ItemVenda itemVenda = itensVenda.get(posicao);
				itemVenda.setVenda(venda);
				
				sessao.save(itemVenda);
				
				//Código para atualizar a quantidade do produto no estoque depois da venda
				Produto produto = itemVenda.getProduto();
				
				int quantidade = produto.getQuantidade() - itemVenda.getQuantidade();
				
				if(quantidade >= 0) {
				//Convertendo o Short da quantidade para uma String concatenando com as aspas no final
				produto.setQuantidade(new Short((quantidade) + ""));
				sessao.update(produto); // atualização  depois que o item for removido.
				}else {
					Messages.addGlobalError("Quantidade em estoque insuficiente!");
					throw new RuntimeException("Quantidade em estoque insuficiente");
				}
			}
			
			transacao.commit();
		} catch (RuntimeException erro) {
			if (transacao != null) {
				transacao.rollback();
			}
			throw erro;
		} finally {
			sessao.close();
		}
	}
}
