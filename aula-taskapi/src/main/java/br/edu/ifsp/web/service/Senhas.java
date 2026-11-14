package br.edu.ifsp.web.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Formato armazenado: pbkdf2$<iteracoes>$<salt base64>$<hash base64>
public final class Senhas {

    private static final int ITERACOES = 120_000;
    private static final int BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Senhas() {}

    public static String gerarHash(String senha) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(senha, salt, ITERACOES);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2$" + ITERACOES + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenado) {
        String[] partes = armazenado == null ? new String[0] : armazenado.split("\\$");
        if (senha == null || partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        Base64.Decoder b64 = Base64.getDecoder();
        byte[] salt = b64.decode(partes[2]);
        byte[] esperado = b64.decode(partes[3]);
        byte[] calculado = pbkdf2(senha, salt, Integer.parseInt(partes[1]));
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] pbkdf2(String senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponivel", e);
        }
    }
}
