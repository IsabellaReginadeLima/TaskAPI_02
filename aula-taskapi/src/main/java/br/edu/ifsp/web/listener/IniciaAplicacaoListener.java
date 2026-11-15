package br.edu.ifsp.web.listener;

import br.edu.ifsp.web.dao.ConnectionFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;

@WebListener
public class IniciaAplicacaoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent e) {
        ServletContext ctx = e.getServletContext();

        // No Tomcat, drivers que estão em WEB-INF/lib nem sempre são
        // registrados automaticamente no DriverManager. Carregar a classe
        // aqui garante o registro (evita "No suitable driver found").
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("[TaskAPI] Driver MySQL não encontrado em WEB-INF/lib!");
        }

        // TODO 1: crie uma ConnectionFactory e publique-a no ServletContext
        //         com a chave "connectionFactory".
        ctx.setAttribute("connectionFactory", new ConnectionFactory());

        // TODO 2: registre no console que a aplicação iniciou.
        System.out.println("[TaskAPI] Aplicação INICIADA em " + LocalDateTime.now()
                + " — ConnectionFactory publicada no ServletContext.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        e.getServletContext().removeAttribute("connectionFactory");

        // Boa prática: desregistra o driver carregado por esta aplicação
        // para o Tomcat não acusar vazamento de memória no redeploy.
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (Driver d : Collections.list(DriverManager.getDrivers())) {
            if (d.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(d);
                } catch (SQLException ex) {
                    System.err.println("[TaskAPI] Falha ao desregistrar driver: " + ex.getMessage());
                }
            }
        }

        // TODO 3: registre no console que a aplicação foi encerrada.
        System.out.println("[TaskAPI] Aplicação ENCERRADA em " + LocalDateTime.now());
    }
}
