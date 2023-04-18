package br.com.financeiro.util;

	import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.service.ServiceRegistry;

	public class HibernateUtil {

	    private static final SessionFactory fabricaDeSessoes = criarFabricaDeSessoes();

	    public static SessionFactory getFabricaDeSessoes() {
	        return fabricaDeSessoes;
	    }
	    
	    // Método criado para transferir a conexão com o bd do hibernate para o JDBC
	    //isto é necessário para a criação de relatorios de onde o  jasper vai se conectar ao banco.
		public static Connection getConexao() {
			Session sessao = fabricaDeSessoes.openSession();

			Connection conexao = sessao.doReturningWork(new ReturningWork<Connection>() {

				@Override
				public Connection execute(Connection conn) throws SQLException {
					return conn;
				}
			});

			return conexao;
		}

	    
	    private static SessionFactory criarFabricaDeSessoes() {
	        try {
	            // Cria uma conex�o a partir do hibernate config
	        	
	        	Configuration configuracao = new Configuration();
	        	configuracao.configure();
	        	
	        	ServiceRegistry servicoDeRegistro = new StandardServiceRegistryBuilder()
	        			.applySettings(configuracao.getProperties()).build();
	        	
	        	SessionFactory fabricaDeSessoes = configuracao.buildSessionFactory(servicoDeRegistro);
	        	 
	        	return fabricaDeSessoes;
	        	
	        	
	        	
	        	//	            return new Configuration().configure().buildSessionFactory(
//				    new StandardServiceRegistryBuilder().build() );
	       
	        }
	        catch (Throwable ex) {
	            // Mensagem de erro ao conectar
	            System.err.println("Falha na conexao!!!" + ex);
	            throw new ExceptionInInitializerError(ex);
	        }
	    }

	   

	}


