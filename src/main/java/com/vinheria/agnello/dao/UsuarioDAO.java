package com.vinheria.agnello.dao;

import com.vinheria.agnello.infra.ConnectionFactory;
import com.vinheria.agnello.model.Usuario;

import java.sql.*;
import java.util.Optional;

public class UsuarioDAO {

    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha_hash, telefone, cpf, criado_em " +
                     "FROM tb_usuario WHERE LOWER(email) = LOWER(?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha buscando usuário por email", e);
        }
    }

    public Optional<Usuario> buscarPorId(long id) {
        String sql = "SELECT id, nome, email, senha_hash, telefone, cpf, criado_em " +
                     "FROM tb_usuario WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha buscando usuário por id", e);
        }
    }

    public long inserir(Usuario u) {
        String sql = "INSERT INTO tb_usuario (nome, email, senha_hash, telefone, cpf) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql, new String[]{"ID"})) {
            st.setString(1, u.getNome());
            st.setString(2, u.getEmail());
            st.setString(3, u.getSenhaHash());
            st.setString(4, u.getTelefone());
            st.setString(5, u.getCpf());
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    u.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha inserindo usuário: " + e.getMessage(), e);
        }
        throw new RuntimeException("Insert sem ID retornado");
    }

    public void atualizar(Usuario u) {
        String sql = "UPDATE tb_usuario SET nome = ?, telefone = ?, cpf = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, u.getNome());
            st.setString(2, u.getTelefone());
            st.setString(3, u.getCpf());
            st.setLong(4, u.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha atualizando usuário", e);
        }
    }

    public boolean emailJaExiste(String email) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "SELECT 1 FROM tb_usuario WHERE LOWER(email) = LOWER(?)")) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha verificando email", e);
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setTelefone(rs.getString("telefone"));
        u.setCpf(rs.getString("cpf"));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) u.setCriadoEm(ts.toLocalDateTime());
        return u;
    }
}
