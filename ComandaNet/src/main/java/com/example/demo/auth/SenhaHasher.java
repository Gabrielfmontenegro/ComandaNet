package com.example.demo.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Armazena senhas como PBKDF2 com salt aleatório; a senha original não é guardada. */
public final class SenhaHasher {
    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_CHAVE = 256;
    private static final int TAMANHO_SALT = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaHasher() {}

    public static String gerar(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenada) {
        if (senha == null || armazenada == null) return false;
        String[] partes = armazenada.split(":", 2);
        if (partes.length != 2) return false;
        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            byte[] hashRecebido = derivar(senha.toCharArray(), salt);
            return MessageDigest.isEqual(hashEsperado, hashRecebido);
        } catch (IllegalArgumentException erro) {
            return false;
        }
    }

    private static byte[] derivar(char[] senha, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, ITERACOES, TAMANHO_CHAVE);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception erro) {
            throw new IllegalStateException("Não foi possível proteger a senha.", erro);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }
}
