package br.com.financeiro.bean;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import br.com.financeiro.dao.UsuarioDAO;
import br.com.financeiro.dominio.Pessoa;
import br.com.financeiro.dominio.Usuario;

@ManagedBean
@SessionScoped
@ViewScoped
public class AutenticacaoBean {
	
	private Usuario usuario;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public void inicializar() {
		usuario = new Usuario();
		usuario.setPessoa(new Pessoa());
	}
	
	public void autenticar() {
		try {
			
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			Usuario usuarioLogado = usuarioDAO.autenticar(usuario.getPessoa().getCpf(), usuario.getSenha());
			
			if(usuarioLogado == null) {
				Messages.addGlobalError("Usuário ou senha incorretos!");
			}
			
			Faces.redirect("./pages/principal.xhtml");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
