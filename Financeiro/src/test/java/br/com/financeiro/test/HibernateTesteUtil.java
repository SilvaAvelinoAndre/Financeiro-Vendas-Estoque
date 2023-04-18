package br.com.financeiro.test;

import org.hibernate.Session;
import org.junit.Test;

import br.com.financeiro.util.HibernateUtil;

public class HibernateTesteUtil {
	@Test
	public void conectar(){
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		sessao.close();
		HibernateUtil.getFabricaDeSessoes().close();
	}
}