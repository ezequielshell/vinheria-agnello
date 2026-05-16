package com.vinheria.agnello.dao;

import com.vinheria.agnello.infra.ConnectionFactory;
import com.vinheria.agnello.model.Vinho;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritoDAO {

    public Set<Integer> idsDoUsuario(long usuarioId) {
        Set<Integer> ids = new HashSet<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "SELECT vinho_id FROM tb_favorito WHERE usuario_id = ?")) {
            st.setLong(1, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha listando favoritos", e);
        }
        return ids;
    }

    public List<Vinho> listarVinhosFavoritos(long usuarioId) {
        String sql = """
            SELECT v.id, v.nome, v.regiao, v.pais, v.safra, v.uva, v.tipo,
                   v.imagem_url, v.preco, v.maturacao, v.potencial_guarda,
                   v.acidez, v.teor_alcoolico, v.harmonizacao,
                   v.comentario_giulio, v.nota_giulio, v.estoque
            FROM tb_favorito f
            JOIN tb_vinho v ON v.id = f.vinho_id
            WHERE f.usuario_id = ?
            ORDER BY f.criado_em DESC
            """;
        List<Vinho> result = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) result.add(mapVinho(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha listando favoritos", e);
        }
        return result;
    }

    public void adicionar(long usuarioId, int vinhoId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "INSERT INTO tb_favorito (usuario_id, vinho_id) VALUES (?, ?)")) {
            st.setLong(1, usuarioId);
            st.setInt(2, vinhoId);
            st.executeUpdate();
        } catch (SQLException e) {
            // PK violation (já é favorito) — ignora silenciosamente.
            if (!"23000".equals(e.getSQLState()) && e.getErrorCode() != 1) {
                throw new RuntimeException("Falha adicionando favorito", e);
            }
        }
    }

    public void remover(long usuarioId, int vinhoId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "DELETE FROM tb_favorito WHERE usuario_id = ? AND vinho_id = ?")) {
            st.setLong(1, usuarioId);
            st.setInt(2, vinhoId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha removendo favorito", e);
        }
    }

    private Vinho mapVinho(ResultSet rs) throws SQLException {
        Vinho v = new Vinho();
        v.setId(rs.getInt("id"));
        v.setNome(rs.getString("nome"));
        v.setRegiao(rs.getString("regiao"));
        v.setPais(rs.getString("pais"));
        v.setSafra(rs.getInt("safra"));
        v.setUva(rs.getString("uva"));
        v.setTipo(rs.getString("tipo"));
        v.setImagemUrl(rs.getString("imagem_url"));
        v.setPreco(rs.getDouble("preco"));
        v.setMaturacao(rs.getString("maturacao"));
        v.setPotencialGuarda(rs.getString("potencial_guarda"));
        v.setAcidez(rs.getString("acidez"));
        v.setTeorAlcoolico(rs.getString("teor_alcoolico"));
        v.setHarmonizacao(rs.getString("harmonizacao"));
        v.setComentarioGiulio(rs.getString("comentario_giulio"));
        v.setNotaGiulio(rs.getInt("nota_giulio"));
        v.setEstoque(rs.getInt("estoque"));
        v.setFavorito(true);
        return v;
    }
}
