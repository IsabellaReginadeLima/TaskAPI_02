package br.edu.ifsp.web.cli;

import br.edu.ifsp.web.dao.ConnectionFactory;
import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.AplicacaoException;
import br.edu.ifsp.web.service.AutenticacaoService;
import br.edu.ifsp.web.service.AutenticacaoServiceImpl;
import br.edu.ifsp.web.service.CredenciaisInvalidasException;
import br.edu.ifsp.web.service.RecursoNaoEncontradoException;
import br.edu.ifsp.web.service.RegraDeNegocioException;
import br.edu.ifsp.web.service.TarefaService;
import br.edu.ifsp.web.service.TarefaServiceImpl;
import br.edu.ifsp.web.service.Transacao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportadorConclusoes {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Uso: ImportadorConclusoes <arquivo-de-ids> <email> <senha>");
            System.exit(2);
        }

        Transacao transacao = new Transacao(new ConnectionFactory());
        AutenticacaoService autenticacao = new AutenticacaoServiceImpl(transacao);
        TarefaService tarefas = new TarefaServiceImpl(transacao);

        Usuario usuario;
        try {
            usuario = autenticacao.autenticar(args[1], args[2]);
        } catch (CredenciaisInvalidasException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
            return;
        } catch (AplicacaoException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(3);
            return;
        }

        List<String> linhas;
        try {
            linhas = Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            System.exit(4);
            return;
        }

        for (String linha : linhas) {
            String valor = linha.trim();
            if (valor.isEmpty()) {
                continue;
            }
            System.out.println(valor + ": " + concluir(tarefas, usuario, valor));
        }
    }

    private static String concluir(TarefaService tarefas, Usuario usuario, String valor) {
        try {
            tarefas.concluir(usuario, Integer.parseInt(valor));
            return "concluida";
        } catch (NumberFormatException e) {
            return "id invalido";
        } catch (RecursoNaoEncontradoException e) {
            return "nao encontrada";
        } catch (RegraDeNegocioException e) {
            return "ja estava concluida";
        } catch (RuntimeException e) {
            return "erro (" + e.getMessage() + ")";
        }
    }
}
