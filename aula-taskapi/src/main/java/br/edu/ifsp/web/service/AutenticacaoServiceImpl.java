package br.edu.ifsp.web.service;

import br.edu.ifsp.web.dao.UsuarioDAO;
import br.edu.ifsp.web.model.Usuario;

import java.util.Optional;

public class AutenticacaoServiceImpl implements AutenticacaoService {

    private static final String MENSAGEM = "E-mail ou senha invalidos.";
    private static final String HASH_FALSO = Senhas.gerarHash("nao-existe");

    private final Transacao transacao;

    public AutenticacaoServiceImpl(Transacao transacao) {
        this.transacao = transacao;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new CredenciaisInvalidasException(MENSAGEM);
        }
        Optional<Usuario> encontrado = transacao.executar(
                c -> new UsuarioDAO(c).buscarPorEmail(email.trim()));

        // Verifica o hash mesmo sem usuário para o tempo de resposta não revelar e-mails
        String hash = encontrado.map(Usuario::getSenha).orElse(HASH_FALSO);
        boolean senhaOk = Senhas.verificar(senha, hash);

        if (encontrado.isEmpty() || !senhaOk) {
            throw new CredenciaisInvalidasException(MENSAGEM);
        }
        return encontrado.get().semCredencial();
    }
}
