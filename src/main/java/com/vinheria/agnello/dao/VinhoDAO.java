package com.vinheria.agnello.dao;

import com.vinheria.agnello.infra.ConnectionFactory;
import com.vinheria.agnello.model.Vinho;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VinhoDAO {

    private static final String COLS = """
        id, nome, regiao, pais, safra, uva, tipo, imagem_url, preco,
        maturacao, potencial_guarda, acidez, teor_alcoolico, harmonizacao,
        comentario_giulio, nota_giulio, estoque
        """;

    public List<Vinho> listarTodos() {
        return query("SELECT " + COLS + " FROM tb_vinho ORDER BY id", st -> {});
    }

    public Optional<Vinho> buscarPorId(int id) {
        List<Vinho> res = query("SELECT " + COLS + " FROM tb_vinho WHERE id = ?",
                st -> st.setInt(1, id));
        return res.isEmpty() ? Optional.empty() : Optional.of(res.get(0));
    }

    public List<Vinho> buscar(String termo) {
        String like = "%" + termo.toLowerCase().trim() + "%";
        return query(
            "SELECT " + COLS + " FROM tb_vinho " +
            "WHERE LOWER(nome) LIKE ? OR LOWER(regiao) LIKE ? OR LOWER(pais) LIKE ? " +
            "   OR LOWER(uva) LIKE ? OR LOWER(tipo) LIKE ? " +
            "ORDER BY nome",
            st -> {
                for (int i = 1; i <= 5; i++) st.setString(i, like);
            });
    }

    public List<Vinho> filtrarPorTipo(String tipo) {
        return query(
            "SELECT " + COLS + " FROM tb_vinho WHERE LOWER(tipo) LIKE ? ORDER BY nome",
            st -> st.setString(1, "%" + tipo.toLowerCase() + "%"));
    }

    public Vinho getDestaque() {
        List<Vinho> r = query(
            "SELECT " + COLS + " FROM tb_vinho ORDER BY nota_giulio DESC, preco DESC",
            st -> st.setMaxRows(1));
        return r.isEmpty() ? null : r.get(0);
    }

    public int contarTotal() {
        try (Connection c = ConnectionFactory.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tb_vinho")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Falha contando vinhos", e);
        }
    }

    public void inserir(Vinho v) {
        String sql = "INSERT INTO tb_vinho (" + COLS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, v.getId());
            st.setString(2, v.getNome());
            st.setString(3, v.getRegiao());
            st.setString(4, v.getPais());
            st.setInt(5, v.getSafra());
            st.setString(6, v.getUva());
            st.setString(7, v.getTipo());
            st.setString(8, v.getImagemUrl());
            st.setDouble(9, v.getPreco());
            st.setString(10, v.getMaturacao());
            st.setString(11, v.getPotencialGuarda());
            st.setString(12, v.getAcidez());
            st.setString(13, v.getTeorAlcoolico());
            st.setString(14, v.getHarmonizacao());
            st.setString(15, v.getComentarioGiulio());
            st.setInt(16, v.getNotaGiulio());
            st.setInt(17, v.getEstoque());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha inserindo vinho " + v.getId(), e);
        }
    }

    public void atualizarEstoque(int vinhoId, int delta) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(
                "UPDATE tb_vinho SET estoque = estoque + ? WHERE id = ? AND estoque + ? >= 0")) {
            st.setInt(1, delta);
            st.setInt(2, vinhoId);
            st.setInt(3, delta);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha atualizando estoque " + vinhoId, e);
        }
    }

    private List<Vinho> query(String sql, StatementBinder binder) {
        List<Vinho> result = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            binder.bind(st);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro consultando vinhos: " + e.getMessage(), e);
        }
        return result;
    }

    private Vinho map(ResultSet rs) throws SQLException {
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

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement st) throws SQLException;
    }
}
