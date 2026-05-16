package com.vinheria.agnello.dao;

import com.vinheria.agnello.infra.ConnectionFactory;
import com.vinheria.agnello.model.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnderecoDAO {

    public List<Endereco> listarDoUsuario(long usuarioId) {
        String sql = """
            SELECT id, usuario_id, cep, logradouro, numero, complemento,
                   bairro, cidade, uf, principal
            FROM tb_endereco
            WHERE usuario_id = ?
            ORDER BY principal DESC, id DESC
            """;
        List<Endereco> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha listando endereços", e);
        }
        return list;
    }

    public Optional<Endereco> buscarPrincipal(long usuarioId) {
        List<Endereco> l = listarDoUsuario(usuarioId);
        return l.stream().filter(Endereco::isPrincipal).findFirst()
                .or(() -> l.isEmpty() ? Optional.empty() : Optional.of(l.get(0)));
    }

    public long inserir(Endereco e) {
        try (Connection c = ConnectionFactory.getConnection()) {
            c.setAutoCommit(false);
            if (e.isPrincipal()) {
                try (PreparedStatement r = c.prepareStatement(
                        "UPDATE tb_endereco SET principal = 0 WHERE usuario_id = ?")) {
                    r.setLong(1, e.getUsuarioId());
                    r.executeUpdate();
                }
            }
            long id;
            try (PreparedStatement st = c.prepareStatement(
                    "INSERT INTO tb_endereco (usuario_id, cep, logradouro, numero, complemento, " +
                    "bairro, cidade, uf, principal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"ID"})) {
                st.setLong(1, e.getUsuarioId());
                st.setString(2, e.getCep());
                st.setString(3, e.getLogradouro());
                st.setString(4, e.getNumero());
                st.setString(5, e.getComplemento());
                st.setString(6, e.getBairro());
                st.setString(7, e.getCidade());
                st.setString(8, e.getUf());
                st.setInt(9, e.isPrincipal() ? 1 : 0);
                st.executeUpdate();
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Endereço sem ID retornado");
                    id = keys.getLong(1);
                }
            }
            c.commit();
            e.setId(id);
            return id;
        } catch (SQLException ex) {
            throw new RuntimeException("Falha inserindo endereço", ex);
        }
    }

    public void remover(long id, long usuarioId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "DELETE FROM tb_endereco WHERE id = ? AND usuario_id = ?")) {
            st.setLong(1, id);
            st.setLong(2, usuarioId);
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha removendo endereço", ex);
        }
    }

    private Endereco map(ResultSet rs) throws SQLException {
        Endereco e = new Endereco();
        e.setId(rs.getLong("id"));
        e.setUsuarioId(rs.getLong("usuario_id"));
        e.setCep(rs.getString("cep"));
        e.setLogradouro(rs.getString("logradouro"));
        e.setNumero(rs.getString("numero"));
        e.setComplemento(rs.getString("complemento"));
        e.setBairro(rs.getString("bairro"));
        e.setCidade(rs.getString("cidade"));
        e.setUf(rs.getString("uf"));
        e.setPrincipal(rs.getInt("principal") == 1);
        return e;
    }
}
