package br.edu.ifsp.web.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessaoListener implements HttpSessionListener {

    private static final AtomicInteger ATIVAS = new AtomicInteger();

    public static int sessoesAtivas() {
        return ATIVAS.get();
    }

    @Override
    public void sessionCreated(HttpSessionEvent e) {
        int total = ATIVAS.incrementAndGet();
        System.out.println("[Sessao] CRIADA " + e.getSession().getId()
                + " em " + LocalDateTime.now() + " — ativas: " + total);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent e) {
        int total = ATIVAS.decrementAndGet();
        System.out.println("[Sessao] DESTRUIDA " + e.getSession().getId()
                + " em " + LocalDateTime.now() + " — ativas: " + total);
    }
}
