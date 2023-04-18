package br.com.financeiro.dao;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.financeiro.dominio.Usuario;
import br.com.financeiro.util.HibernateUtil;

public class UsuarioDAO extends GenericDAO<Usuario> {
	
	public Usuario autenticar(String cpf, String senha) {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Usuario.class);
			consulta.createAlias("pessoa", "p"); // criando um alias para pessoa e o seu apelido p.
			consulta.add(Restrictions.eq("p.cpf", cpf));
			
			SimpleHash hash = new SimpleHash("md5", senha);// encriptando a senha;
			consulta.add(Restrictions.eq("senha", hash.toHex()));
			Usuario resultado = (Usuario) consulta.uniqueResult();// retorna um resultado apenas se autenticou ou não.
			
			return resultado;
			
		} catch (RuntimeException e) {
			throw e;
		}finally {
			sessao.close();
		}
	}

}
