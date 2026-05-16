package com.vinheria.agnello.dao;

import com.vinheria.agnello.infra.ConnectionFactory;
import com.vinheria.agnello.model.ItemCarrinho;
import com.vinheria.agnello.model.ItemPedido;
import com.vinheria.agnello.model.Pedido;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAO {

    /**
     * Cria pedido + itens em transação única. Decrementa estoque dos vinhos e limpa o carrinho.
     * Retorna o ID do pedido recém-criado.
     */
    public long criar(long usuarioId, List<ItemCarrinho> itens,
                      String formaPagamento, String enderecoEntrega) {

        BigDecimal total = itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try (Connection c = ConnectionFactory.getConnection()) {
            c.setAutoCommit(false);
            long pedidoId;

            try (PreparedStatement ped = c.prepareStatement(
                    "INSERT INTO tb_pedido (usuario_id, status, total, forma_pagamento, endereco_entrega) " +
                    "VALUES (?, 'CONFIRMADO', ?, ?, ?)",
                    new String[]{"ID"})) {
                ped.setLong(1, usuarioId);
                ped.setBigDecimal(2, total);
                ped.setString(3, formaPagamento);
                ped.setString(4, enderecoEntrega);
                ped.executeUpdate();
                try (ResultSet keys = ped.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Pedido sem ID retornado");
                    pedidoId = keys.getLong(1);
                }
            }

            try (PreparedStatement it = c.prepareStatement(
                    "INSERT INTO tb_pedido_item (pedido_id, vinho_id, quantidade, preco_unitario) " +
                    "VALUES (?, ?, ?, ?)");
                 PreparedStatement est = c.prepareStatement(
                    "UPDATE tb_vinho SET estoque = estoque - ? WHERE id = ? AND estoque >= ?")) {

                for (ItemCarrinho i : itens) {
                    it.setLong(1, pedidoId);
                    it.setInt(2, i.getVinho().getId());
                    it.setInt(3, i.getQuantidade());
                    it.setBigDecimal(4, BigDecimal.valueOf(i.getVinho().getPreco()));
                    it.addBatch();

                    est.setInt(1, i.getQuantidade());
                    est.setInt(2, i.getVinho().getId());
                    est.setInt(3, i.getQuantidade());
                    est.addBatch();
                }
                it.executeBatch();
                est.executeBatch();
            }

            try (PreparedStatement clr = c.prepareStatement(
                    "DELETE FROM tb_carrinho_item WHERE usuario_id = ?")) {
                clr.setLong(1, usuarioId);
                clr.executeUpdate();
            }

            c.commit();
            return pedidoId;
        } catch (SQLException e) {
            throw new RuntimeException("Falha criando pedido: " + e.getMessage(), e);
        }
    }

    public List<Pedido> listarDoUsuario(long usuarioId) {
        String sql = """
            SELECT id, usuario_id, criado_em, status, total, forma_pagamento, endereco_entrega
            FROM tb_pedido
            WHERE usuario_id = ?
            ORDER BY criado_em DESC
            """;
        List<Pedido> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) list.add(mapPedido(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha listando pedidos", e);
        }
        return list;
    }

    public Optional<Pedido> buscarComItens(long pedidoId, long usuarioId) {
        Pedido p;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "SELECT id, usuario_id, criado_em, status, total, forma_pagamento, endereco_entrega " +
                "FROM tb_pedido WHERE id = ? AND usuario_id = ?")) {
            st.setLong(1, pedidoId);
            st.setLong(2, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                p = mapPedido(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha buscando pedido", e);
        }

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "SELECT pi.id, pi.pedido_id, pi.vinho_id, pi.quantidade, pi.preco_unitario, " +
                "       v.nome, v.imagem_url " +
                "FROM tb_pedido_item pi JOIN tb_vinho v ON v.id = pi.vinho_id " +
                "WHERE pi.pedido_id = ?")) {
            st.setLong(1, pedidoId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    ItemPedido i = new ItemPedido();
                    i.setId(rs.getLong("id"));
                    i.setPedidoId(rs.getLong("pedido_id"));
                    i.setVinhoId(rs.getLong("vinho_id"));
                    i.setQuantidade(rs.getInt("quantidade"));
                    i.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
                    i.setVinhoNome(rs.getString("nome"));
                    i.setVinhoImagem(rs.getString("imagem_url"));
                    p.getItens().add(i);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha listando itens do pedido", e);
        }
        return Optional.of(p);
    }

    private Pedido mapPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getLong("id"));
        p.setUsuarioId(rs.getLong("usuario_id"));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) p.setCriadoEm(ts.toLocalDateTime());
        p.setStatus(rs.getString("status"));
        p.setTotal(rs.getBigDecimal("total"));
        p.setFormaPagamento(rs.getString("forma_pagamento"));
        p.setEnderecoEntrega(rs.getString("endereco_entrega"));
        return p;
    }
}
