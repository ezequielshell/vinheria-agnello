package com.vinheria.agnello.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.vinheria.agnello.dao.UsuarioDAO;
import com.vinheria.agnello.model.Usuario;

import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern EMAIL_RGX = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public static class AuthException extends RuntimeException {
        public AuthException(String msg) { super(msg); }
    }

    public Usuario autenticar(String email, String senhaRaw) {
        if (email == null || senhaRaw == null) {
            throw new AuthException("Informe email e senha.");
        }
        Optional<Usuario> opt = usuarioDAO.buscarPorEmail(email.trim());
        if (opt.isEmpty()) {
            throw new AuthException("E-mail ou senha inválidos.");
        }
        Usuario u = opt.get();
        BCrypt.Result r = BCrypt.verifyer().verify(senhaRaw.toCharArray(), u.getSenhaHash());
        if (!r.verified) {
            throw new AuthException("E-mail ou senha inválidos.");
        }
        u.setSenhaHash(null);
        return u;
    }

    public Usuario cadastrar(String nome, String email, String senhaRaw,
                             String telefone, String cpf) {
        validar(nome, email, senhaRaw);
        if (usuarioDAO.emailJaExiste(email.trim())) {
            throw new AuthException("Já existe uma conta com este e-mail.");
        }
        Usuario u = new Usuario();
        u.setNome(nome.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setSenhaHash(BCrypt.withDefaults().hashToString(12, senhaRaw.toCharArray()));
        u.setTelefone(telefone);
        u.setCpf(cpf);
        usuarioDAO.inserir(u);
        u.setSenhaHash(null);
        return u;
    }

    private void validar(String nome, String email, String senha) {
        if (nome == null || nome.trim().length() < 2) {
            throw new AuthException("Informe o nome completo.");
        }
        if (email == null || !EMAIL_RGX.matcher(email.trim()).matches()) {
            throw new AuthException("E-mail inválido.");
        }
        if (senha == null || senha.length() < 6) {
            throw new AuthException("Senha deve ter ao menos 6 caracteres.");
        }
    }
}
