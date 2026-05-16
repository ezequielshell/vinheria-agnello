package com.vinheria.agnello.dao;

import com.vinheria.agnello.infra.ConnectionFactory;
import com.vinheria.agnello.model.ItemCarrinho;
import com.vinheria.agnello.model.Vinho;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoDAO {

    public List<ItemCarrinho> listar(long usuarioId) {
        String sql = """
            SELECT c.quantidade,
                   v.id, v.nome, v.regiao, v.pais, v.safra, v.uva, v.tipo,
                   v.imagem_url, v.preco, v.maturacao, v.potencial_guarda,
                   v.acidez, v.teor_alcoolico, v.harmonizacao,
                   v.comentario_giulio, v.nota_giulio, v.estoque
            FROM tb_carrinho_item c
            JOIN tb_vinho v ON v.id = c.vinho_id
            WHERE c.usuario_id = ?
            ORDER BY c.adicionado_em DESC
            """;
        List<ItemCarrinho> itens = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Vinho v = mapVinho(rs);
                    itens.add(new ItemCarrinho(v, rs.getInt("quantidade")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha listando carrinho", e);
        }
        return itens;
    }

    public int contar(long usuarioId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "SELECT COALESCE(SUM(quantidade), 0) FROM tb_carrinho_item WHERE usuario_id = ?")) {
            st.setLong(1, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha contando itens do carrinho", e);
        }
    }

    public void adicionar(long usuarioId, int vinhoId, int quantidade) {
        // UPSERT: se já existe, incrementa quantidade.
        try (Connection c = ConnectionFactory.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement upd = c.prepareStatement(
                    "UPDATE tb_carrinho_item SET quantidade = quantidade + ? " +
                    "WHERE usuario_id = ? AND vinho_id = ?")) {
                upd.setInt(1, quantidade);
                upd.setLong(2, usuarioId);
                upd.setInt(3, vinhoId);
                int rows = upd.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement ins = c.prepareStatement(
                            "INSERT INTO tb_carrinho_item (usuario_id, vinho_id, quantidade) VALUES (?, ?, ?)")) {
                        ins.setLong(1, usuarioId);
                        ins.setInt(2, vinhoId);
                        ins.setInt(3, quantidade);
                        ins.executeUpdate();
                    }
                }
            }
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Falha adicionando item ao carrinho", e);
        }
    }

    public void definirQuantidade(long usuarioId, int vinhoId, int quantidade) {
        if (quantidade <= 0) {
            remover(usuarioId, vinhoId);
            return;
        }
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "UPDATE tb_carrinho_item SET quantidade = ? WHERE usuario_id = ? AND vinho_id = ?")) {
            st.setInt(1, quantidade);
            st.setLong(2, usuarioId);
            st.setInt(3, vinhoId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha atualizando quantidade", e);
        }
    }

    public void remover(long usuarioId, int vinhoId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "DELETE FROM tb_carrinho_item WHERE usuario_id = ? AND vinho_id = ?")) {
            st.setLong(1, usuarioId);
            st.setInt(2, vinhoId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha removendo item do carrinho", e);
        }
    }

    public void limpar(long usuarioId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "DELETE FROM tb_carrinho_item WHERE usuario_id = ?")) {
            st.setLong(1, usuarioId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha limpando carrinho", e);
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
        return v;
    }
}
